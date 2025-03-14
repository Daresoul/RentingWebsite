import {useEffect, useState} from "react";
import 'react-date-range/dist/styles.css';
import 'react-date-range/dist/theme/default.css';
import { Box, Container, Grid, Typography, Button, Modal, Paper, Stack, IconButton } from "@mui/material";
import { styled } from "@mui/system";
import { FaExpandAlt } from "react-icons/fa";
import {DateRangePicker, Range, RangeKeyDict} from "react-date-range";
import {RentableDTOType} from "../types/rentableDTOType.ts";
import {fetchRentable, isErrorResponse} from "../services/api.ts";
import {useAuthStore, loadingStore} from "../services/AuthStore.ts";
import StripeCheckout from "./StripeCheckout.tsx";
import { Elements } from "@stripe/react-stripe-js";
import {stripePromise} from "./LoadStripe.tsx";

const StyledImage = styled("img")(({
	width: "100%",
	height: "400px",
	objectFit: "cover",
	borderRadius: "12px",
	transition: "transform 0.3s ease",
	"&:hover": {
		transform: "scale(1.02)"
	}
}));

interface RentableProps {
	onOpenChange: (open: boolean, modalType?: "login" | "register") => void;
}

const RentalProperty = ({ onOpenChange }: RentableProps) => {
	const { isLoggedIn } = useAuthStore();
	const { setLoading } = loadingStore();

	const [rentable, setRentable] = useState<RentableDTOType | null>(null);

	const [dateRange, setDateRange] = useState<Range>({
		startDate: new Date(),
		endDate: new Date(),
		key: 'selection',
	});

	const [stripeModalDisplayed, setStripeModalDisplayed] = useState<boolean>(false);

	const [disabledDates, setDisabledDates] = useState<Date[]>([])
	const [showFullDescription, setShowFullDescription] = useState(false);
	const [imageModal, setImageModal] = useState(false);

	const [price, setPrice] = useState<number>(0);

	useEffect(() => {
		const constFetchRental = async () => {
			setLoading(true, "Fetching Data")
			var response = await fetchRentable(window.location.href.split("/")[4])

			if (isErrorResponse(response)) {
				setLoading(false, "")
				return;
			}

			var data = response.data;

			let dates: Date[] = []
			for(let i = 0; i < data.dates.length; i++) {
				dates.push(new Date(data.dates[i]))
			}
			setDisabledDates(dates)
			console.log(data)
			setRentable(data)
			setLoading(false, "")
		};

		constFetchRental();
	}, [])

	const addDisabledDates = async (startDate: Date, endDate: Date) => {
		const dates: Date[] = [];
		let currentDate = new Date(startDate);

		while (currentDate <= endDate) {
			dates.push(new Date(currentDate));
			currentDate.setDate(currentDate.getDate() + 1);
		}

		setDisabledDates([...disabledDates, ...dates])
	}

	function getDaysBetweenDates(date1: Date, date2: Date) {
		const oneDay = 1000 * 60 * 60 * 24; // Milliseconds in a day
		const diffInTime = Math.abs(date2.getTime() - date1.getTime()); // Difference in milliseconds
		return Math.floor(diffInTime / oneDay); // Convert to full days
	}

	const closeModal = async () => {
		setStripeModalDisplayed(false)
	}

	function handleSelect(ranges : RangeKeyDict){
		let selection = ranges["selection"]

		if (!selection.startDate || !selection.endDate) {
			console.log("Dates couldnt be gathered for select")
			return
		}

		let startDate = new Date(selection.startDate);
		let endDate = new Date(selection.endDate);

		console.log(startDate.toISOString())
		console.log(startDate.toLocaleDateString())

		var days = getDaysBetweenDates(startDate, endDate) + 1

		if (rentable) setPrice(days * rentable.price)

		setDateRange({
			startDate: startDate,
			endDate: endDate,
			key: 'selection',
		})
	}

	const rentNow = async () => {
		if(!isLoggedIn) {
			onOpenChange(true, "login")
			return
		}

		setStripeModalDisplayed(true)
	}


	return (
			<Container maxWidth="lg" sx={{ py: 4 }}>
				<Grid container spacing={4}>
					<Grid item xs={12} md={7.5}>
						<Box position="relative">
							<StyledImage
								src={rentable ? rentable.images[0].imageUrl : ""}
								alt={rentable?.name}
								onError={(e) => {
									const target = e.target as HTMLImageElement;
									target.src = "https://cdn-icons-png.flaticon.com/512/10701/10701484.png";
								}}
							/>
							<IconButton
								sx={{
									position: "absolute",
									right: 8,
									top: 8,
									backgroundColor: "rgba(255,255,255,0.8)"
								}}
								onClick={() => setImageModal(true)}
							>
								<FaExpandAlt />
							</IconButton>
						</Box>

						<Typography variant="h4" sx={{ mt: 3, mb: 2 }}>
							{rentable ? rentable.name : ""}
						</Typography>

						<Typography variant="body1" sx={{ mb: 2 }}>
							{rentable && (showFullDescription
								? rentable.description
								: `${rentable.description.slice(0, 150)}...`)}
							<Button
								onClick={() => setShowFullDescription(!showFullDescription)}
								sx={{ ml: 1 }}
								size="small"
							>
								{showFullDescription ? "Show Less" : "Read More"}
							</Button>
						</Typography>

						<Paper elevation={2} sx={{ p: 3, mt: 3, borderRadius: "12px" }}>
							<Typography variant="h6" sx={{ mb: 2 }}>
								Item Specifications
							</Typography>
							<Grid container spacing={2}>
								{rentable?.specifications.map((specification) => (
									<Grid item xs={12} sm={6} key={specification.key}>
										<Box sx={{ display: "flex", justifyContent: "space-between" }}>
											<Typography variant="body1" sx={{ textTransform: "capitalize" }}>
												{specification.key.replace(/([A-Z])/g, " $1").trim()}:
											</Typography>
											<Typography variant="body1" sx={{ fontWeight: "bold" }}>
												{specification.value}
											</Typography>
										</Box>
									</Grid>
								))}
							</Grid>
						</Paper>
					</Grid>

					<Grid item xs={12} md={4.5}>
						<Paper elevation={3} sx={{ p: 3, borderRadius: "12px" }}>
							<Typography variant="h6" sx={{ mb: 2 }}>
								{rentable ? rentable.price : "??"} DKK / night
							</Typography>

							<Stack spacing={2}>
								<DateRangePicker
									weekStartsOn={1}
									disabledDates={disabledDates}
									minDate={new Date()}
									ranges={[dateRange]}
									onChange={handleSelect}
									months={2}
									fixedHeight={true}
									staticRanges={[]}
									inputRanges={[]}
								/>

								<Typography variant="h6">
									Total: {price} DKK
								</Typography>

								<Button
									onClick={rentNow}
									variant="contained"
									color="primary"
									size="large"
									fullWidth
									sx={{
										mt: 2,
										py: 1.5,
										fontSize: "1.1rem",
										textTransform: "none"
									}}
								>
									Rent Now
								</Button>
							</Stack>
						</Paper>
					</Grid>
				</Grid>

				<Elements stripe={stripePromise}>
					<StripeCheckout
						price={price}
						dateRange={dateRange}
						rentable={rentable}
						closeModal={closeModal}
						modalOpen={stripeModalDisplayed}
						addDates={addDisabledDates}
					>

					</StripeCheckout>
				</Elements>

				<Modal
					open={imageModal}
					onClose={() => setImageModal(false)}
					sx={{
						display: "flex",
						alignItems: "center",
						justifyContent: "center"
					}}
				>
					<img
						src={rentable ? rentable.images[0].imageUrl : ""}
						onError={(e) => {
							const target = e.target as HTMLImageElement;
							target.src = "https://cdn-icons-png.flaticon.com/512/10701/10701484.png";
						}}
						alt={rentable?.name}
						style={{
							maxWidth: "90vw",
							maxHeight: "90vh",
							objectFit: "contain"
						}}
					/>
				</Modal>
			</Container>
	);
};

export default RentalProperty;