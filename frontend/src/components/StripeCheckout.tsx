import {useEffect, useState} from "react";
import {
	Box,
	Button,
	Card,
	CardMedia,
	Container,
	Dialog,
	Grid,
	Typography,
	IconButton,
	Stack,
	Chip,
	RadioGroup,
	FormControlLabel,
	Radio,
	FormControl,
	FormLabel,
} from "@mui/material";
import { styled } from "@mui/system";
import { FaLock, FaTimes } from "react-icons/fa";
import { format, addDays } from "date-fns";
import {Range} from "react-date-range";
import {RentableDTOType} from "../types/rentableDTOType.ts";
import {
	CardCvcElement,
	CardExpiryElement,
	CardNumberElement,
	useElements,
	useStripe
} from "@stripe/react-stripe-js";

import { Theme } from "@mui/material/styles";
import {createPaymentIntent, isErrorResponse, validateReservation} from "../services/api.ts";
import {StripePaymentIntentType} from "../types/stripePaymentIntentType.ts";
import {loadingStore} from "../services/AuthStore.ts";

interface styledDialogProps {
	theme?: Theme;
}

const StyledDialog = styled(Dialog)(({} : styledDialogProps) => ({
	"& .MuiDialog-paper": {
		maxWidth: "800px",
		width: "100%",
		borderRadius: "16px",
		backgroundColor: "rgb(36, 36, 36)"
	}
}));

const PaymentCard = styled(Card)({
	padding: "24px",
	height: "100%"
});

interface StripeCheckoutProps {
	modalOpen: boolean;
	closeModal: () => void;
	addDates: (startDate: Date, endDate: Date) => void;
	dateRange: Range;
	rentable: RentableDTOType | null;
	price: number;
}



const StripeCheckout = ({modalOpen, closeModal, dateRange, rentable, price} : StripeCheckoutProps) => {
	const stripe = useStripe();
	const elements = useElements();

	const {setLoading} = loadingStore()

	const [cardHolder, setCardHolder] = useState("");
	const [error, setError] = useState<string>("");
	const [paymentMethod, setPaymentMethod] = useState<"online" | "pickup">("online");
	const [isModalOpen, setIsModalOpen] = useState(modalOpen);
	const [paymentIntent , setPaymentIntent] = useState<StripePaymentIntentType | null>(null);

	useEffect(() => {
		setIsModalOpen(modalOpen);

		const handleFetchingPaymentIntent = async ()=> {
			if (modalOpen && !paymentIntent && rentable && dateRange.startDate && dateRange.endDate) {
				setLoading(true, "")
				var response = await createPaymentIntent(
					dateRange.startDate,
					dateRange.endDate,
					rentable.id
				);

				console.log(dateRange.startDate)
				console.log(dateRange.endDate)

				if (isErrorResponse(response)) {
					setError("Please close and open the modal")
					setLoading(false, "")
					return;
				}

				setPaymentIntent(response.data)

				setLoading(false, "")
			}
		}

		handleFetchingPaymentIntent()
	}, [modalOpen]);

	const propertyData = {
		name: "Luxury Beach Villa",
		image: "https://images.unsplash.com/photo-1582268611958-ebfd161ef9cf",
		startDate: new Date(),
		endDate: addDays(new Date(), 7),
		pricePerNight: 299,
		taxes: 89.7,
		cleaningFee: 150
	};

	const handleSubmit = async () => {

		setLoading(true, "Processing payment")
		if (!stripe || !elements) {
			setError("Stripe is not ready.");
			setLoading(false, "")
			return
		}

		if (!paymentIntent || !rentable) {
			setError("Could not find the rentable or the intent to pay.")
			setLoading(false, "")
			return
		}

		setError("");

		const validationResponse = await validateReservation(
			dateRange,
			rentable.id
		);

		if (isErrorResponse(validationResponse)) {
			setError(validationResponse.data.message)
			setLoading(false, "")
			return;
		}


		const cardElement = elements.getElement(CardNumberElement);
		if (!cardElement) {
			setLoading(false, "")
			return;
		}

		try {
			const { paymentIntent: confirmedPayment, error: intentError } = await stripe.confirmCardPayment(paymentIntent.clientSecret, {
				payment_method: {
					card: cardElement,
					billing_details: {
						name: cardHolder,
					},
				},
			});

			if (intentError) {
				setError(intentError.message || "Error confirming payment.");
				setLoading(false, "")
				return;
			}

			if (confirmedPayment.status === "succeeded") {
				console.log("✅ Payment successful!");
				setError("");
				setLoading(false, "")
				setIsModalOpen(false)
			} else {
				setError("Payment was not successful.");
				setLoading(false, "")
			}
		} catch (err) {
			setError("Unexpected error occurred. Please try again.");
			setLoading(false, "")
		}

	};

	// @ts-ignore
	const checkoutContent = (
		<Container maxWidth="lg" sx={{ py: 3 }}>
			<Grid container spacing={4}>
				<Grid item xs={12} md={7}>
					<PaymentCard>
						<Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
							<Typography variant="h5" fontWeight="bold">Payment Details</Typography>
							<Chip icon={<FaLock />} label="Secure Payment" color="success" variant="outlined" />
						</Stack>

						<FormControl component="fieldset" sx={{ mb: 3 }}>
							<FormLabel component="legend">Select Payment Method</FormLabel>
							<RadioGroup
								value={paymentMethod}
								onChange={(e) => setPaymentMethod(e.target.value as "pickup" | "online")}
							>
								<FormControlLabel value="online" control={<Radio />} label="Pay Online" />
								<FormControlLabel value="pickup" control={<Radio />} label="Pay at Pickup (Cash)" />
							</RadioGroup>
						</FormControl>

						{paymentMethod === "online" && (
							<form onSubmit={handleSubmit}>
								<Grid container spacing={3}>
									{/* Card Number */}
									<Grid item xs={12}>
										<label style={{ color: "whitesmoke", fontSize: "14px", marginBottom: "5px", display: "block" }}>Card Number</label>
										<div style={{
											border: "1px solid rgb(36, 36, 36)",
											borderRadius: "4px",
											padding: "14px 10px",
											height: "20px"
										}}>
											<CardNumberElement />
										</div>
									</Grid>

									{/* Expiry Date */}
									<Grid item xs={12} sm={6}>
										<label style={{ color: "whitesmoke", fontSize: "14px", marginBottom: "5px", display: "block" }}>Expiry Date</label>
										<div style={{
											border: "1px solid rgb(36, 36, 36)",
											borderRadius: "4px",
											padding: "14px 10px",
											height: "20px"
										}}>
											<CardExpiryElement />
										</div>
									</Grid>

									{/* CVC */}
									<Grid item xs={12} sm={6}>
										<label style={{ color: "whitesmoke", fontSize: "14px", marginBottom: "5px", display: "block" }}>CVC</label>
										<div style={{
											border: "1px solid rgb(36, 36, 36)",
											borderRadius: "4px",
											padding: "14px 10px",
											height: "20px"
										}}>
											<CardCvcElement />
										</div>
									</Grid>

									<Grid item xs={12}>
											<input
												type="text"
												style={{
													width: "94%",
													marginRight: "5%",
													backgroundColor: "transparent",
													border: "1px solid rgb(36, 36, 36)",
													borderRadius: "4px",
													padding: "14px 10px",
													height: "20px",
													color: "rgb(36, 36, 36)",
												}}
												placeholder="Card holder"
												value={cardHolder}
												onChange={(e) => setCardHolder(e.target.value)}
											/>
									</Grid>

									<Grid item xs={12}>
										<span style={{
											color: "red"
										}}>
											{error}
										</span>
									</Grid>
								</Grid>
							</form>
						)}
					</PaymentCard>
				</Grid>

				<Grid item xs={12} md={5}>
					<PaymentCard>
						<Typography variant="h5" fontWeight="bold" mb={3}>Booking Summary</Typography>

						<CardMedia
							component="img"
							height="200"
							image={propertyData.image}
							alt={propertyData.name}
							sx={{ borderRadius: 2, mb: 2 }}
						/>

						<Typography variant="h6" gutterBottom>{rentable?.name}</Typography>

						<Stack spacing={2}>
							<Box display="flex" justifyContent="space-between">
								<Typography>Check-in</Typography>
								<Typography>{dateRange.startDate ? format(dateRange.startDate, "MMM dd, yyyy") : ""}</Typography>
							</Box>
							<Box display="flex" justifyContent="space-between">
								<Typography>Check-out</Typography>
								<Typography>{dateRange.endDate ? format(dateRange.endDate, "MMM dd, yyyy") : ""}</Typography>
							</Box>
							<Box display="flex" justifyContent="space-between">
								<Typography>Price per night</Typography>
								<Typography>{rentable?.price} DKK</Typography>
							</Box>
							<Box display="flex" justifyContent="space-between" pt={2}>
								<Typography variant="h6">Total</Typography>
								<Typography variant="h6">{price} DKK</Typography>
							</Box>
						</Stack>

						<Button
							variant="contained"
							fullWidth
							size="large"
							sx={{ mt: 3 }}
							onClick={() => handleSubmit()}
						>
							Confirm Payment
						</Button>

						{/*<Alert severity="info" sx={{ mt: 2 }}>
							You won't be charged yet. We'll hold this price for you.
						</Alert>*/}
					</PaymentCard>
				</Grid>
			</Grid>
		</Container>
	);

	return (
		<>
			<StyledDialog
				open={isModalOpen}
				onClose={() => closeModal()}
				fullWidth
				maxWidth="md"
			>
				<Box position="relative" p={2}>
					<IconButton
						onClick={() => closeModal()}
						sx={{ position: "absolute", right: 8, top: 8 }}
					>
						<FaTimes />
					</IconButton>
					{checkoutContent}
				</Box>
			</StyledDialog>
		</>
	);
};

export default StripeCheckout;