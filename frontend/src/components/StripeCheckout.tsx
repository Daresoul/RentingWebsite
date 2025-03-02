import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import Radio from "@mui/material/Radio";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import { Elements } from "@stripe/react-stripe-js";
import CheckoutForm from "./StripeCheckoutForm.tsx";
import React, {useState} from "react";
import { stripePromise } from "./LoadStripe.tsx";
import { Range } from 'react-date-range';
import {createPaymentIntent, createUser} from "../services/api.ts";
import { StripeElementsOptions } from "@stripe/stripe-js";


interface StripeCheckoutProps {
    dateRange: Range;
    rentableId: number;
}

function StripeCheckout({ dateRange, rentableId }: StripeCheckoutProps) {
    const [clientSecret, setClientSecret] = useState("");
    const [paymentIntentId, setPaymentIntentId] = useState("");
    const [clientId, setClientId] = useState(-1);
    const [payNow, setPayNow] = useState(false);
    const [stage, setStage] = useState(0); // 0 = form, 1 = payment screen
    const [loading, setLoading] = useState(false);

    const [formData, setFormData] = useState({
        name: "",
        phone: "",
        email: "",
        address: "",
    });

    // Fetch PaymentIntent only when user clicks "Continue to Payment"
    const handleFetchingPaymentIntent = async () => {

        if (!dateRange.startDate || !dateRange.endDate) {
            console.log("Coulnt fetch the start and end date in", dateRange);
            return;
        }

        setLoading(true);

        var response = await createPaymentIntent(formData.email, dateRange.startDate, dateRange.endDate, rentableId)

        if (!response.ok()) {
            console.log("Couldnt fetch the payment intent from stripe.", response)
            setLoading(false);
            return;
        }

        setPaymentIntentId(response.data.paymentIntentId)
        setClientSecret(response.data.clientSecret);

        setLoading(false);
    };

    const handleCreateUser = async () => {
        setLoading(true);
        var response = await createUser(formData.name, formData.email)

        if (!response.ok()) {
            console.log("Couldnt create user.", response)
            setLoading(false);
            return false
        }

        setClientId(response.data.id);

        setLoading(false);
        return true
    };

    const paymentSuccess = async () => setStage(2)

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [event.target.name]: event.target.value });
    };

    const handlePaymentChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setPayNow(event.target.value === "true");
    };

    const submitUserInfo = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!formData.name || !formData.phone || !formData.email) {
            alert("Please fill out all fields.");
            return;
        }

        setLoading(true);

        if (payNow) {
            await handleCreateUser();
            await handleFetchingPaymentIntent();
            setStage(1)
        } else {
            setStage(2);
        }
        console.log(clientSecret, stage, payNow)
        setLoading(false);
    };

    const appearance: StripeElementsOptions["appearance"] = { theme: "stripe" };

    const options: StripeElementsOptions = {
        clientSecret,
        appearance,
    };

    return (
        <div style={{width:'100%', height:"100%", minHeight: '500px'}}>
            {/* Step 1: User fills out form */}
            {stage === 0 && (
                <Box component="form"
                     sx={{ "& .MuiTextField-root": { m: 1, width: "25ch" } }}
                     noValidate autoComplete="off"
                     onSubmit={submitUserInfo}
                >
                    <FormControl>
                        <FormLabel>Payment</FormLabel>
                        <RadioGroup row name="payment-option" onChange={handlePaymentChange}>
                            <FormControlLabel value="false" control={<Radio />} label="I'll pay when I pick it up" />
                            <FormControlLabel value="true" control={<Radio />} label="I'll pay now" />
                        </RadioGroup>

                        <TextField variant="filled" placeholder="Name" name="name" value={formData.name} onChange={handleInputChange} />
                        <TextField variant="filled" placeholder="Phone number" name="phone" value={formData.phone} onChange={handleInputChange} />
                        <TextField variant="filled" placeholder="Email" name="email" value={formData.email} onChange={handleInputChange} />
                        <TextField variant="filled" placeholder="Address" name="address" value={formData.address} onChange={handleInputChange} />

                        <Button type="submit" disabled={loading}>
                            {loading ? "Processing..." : "Continue to Payment"}
                        </Button>
                    </FormControl>
                </Box>
            )}

            {/* Step 2: Process payment */}
            {payNow && clientId && stage === 1 && clientSecret && (
              <div style={{ width: "100%", height: "100%", minHeight: "500px" }}>
                  <Elements options={options} stripe={stripePromise}>
                      <CheckoutForm
                        onPaymentSuccess={paymentSuccess}
                        clientSecret={clientSecret}
                        clientData={{
                            email: formData.email,
                            address: formData.address,
                            phone: formData.phone,
                            name: formData.name
                        }}
                        clientId={clientId}
                        rentableId={rentableId}
                        paymentIntentId={paymentIntentId}
                        dateRange={dateRange}
                      />
                  </Elements>
              </div>

            )}

            {/* Step 3: Success message */}
            {stage === 2 && (
                <div style={{ textAlign: "center", marginTop: "20px" }}>
                    <h2>✅ Reservation Complete!</h2>
                    <p>Thank you for your booking. A confirmation email has been sent.</p>
                </div>
            )}
        </div>
    )
}

export default StripeCheckout;
