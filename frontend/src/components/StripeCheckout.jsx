import Box from "@mui/material/Box";
import FormControl from "@mui/material/FormControl";
import FormLabel from "@mui/material/FormLabel";
import RadioGroup from "@mui/material/RadioGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import Radio from "@mui/material/Radio";
import TextField from "@mui/material/TextField";
import { Button } from "@mui/material";
import { Elements } from "@stripe/react-stripe-js";
import CheckoutForm from "./StripeCheckoutForm";
import React, { useState } from "react";
import { stripePromise } from "./LoadStripe";

function StripeCheckout({startDate, endDate, rentableId}) {
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
    const fetchPaymentIntent = async () => {
        try {
            setLoading(true);
            console.log(rentableId)
            const response = await fetch("/api/stripe/payment-intent", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ receipt_email: formData.email, startDate: startDate, endDate: endDate, rentable_id: rentableId }),
            });

            const data = await response.json();
            setPaymentIntentId(data.paymentIntentId)
            setClientSecret(data.clientSecret);
            console.log("Payment intent created with", data)
            setLoading(false);
        } catch (error) {
            console.error("Error fetching PaymentIntent:", error);
            alert("Failed to initiate payment. Try again.");
            setLoading(false);
        }
    };

    const createUser = async () => {
        try {
            setLoading(true);
            const response = await fetch("/api/clients/create", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name: formData.name, email: formData.email }),
            });

            const data = await response.json();
            setClientId(data.id);
            console.log("User created with", data)
            setLoading(false);
        } catch (error) {
            console.error("Error creating user:", error);
            alert("Failed to create user. Try again.");
            setLoading(false);
        }
    };

    const paymentSuccess = async () => setStage(2)

    const handleInputChange = (event) => {
        setFormData({ ...formData, [event.target.name]: event.target.value });
    };

    const handlePaymentChange = (event) => {
        setPayNow(event.target.value === "true");
    };

    const submitUserInfo = async (event) => {
        event.preventDefault();

        if (!formData.name || !formData.phone || !formData.email) {
            alert("Please fill out all fields.");
            return;
        }

        setLoading(true);

        if (payNow) {
            await createUser();
            await fetchPaymentIntent();
            setStage(1)
        } else {
            setStage(2);
        }
        console.log(clientSecret, stage, payNow)
        setLoading(false);
    };

    const appearance = { theme: "stripe" };
    const options = { clientSecret, appearance };

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
                <Elements options={options} stripe={stripePromise} style={{width:'100%', height: '100%', minHeight: '500px'}}>
                    <CheckoutForm
                        onPaymentSuccess={paymentSuccess}
                        clientSecret={clientSecret}
                        clientData={ {email: formData.email, address: formData.address, phone: formData.phone, name: formData.name} }
                        clientId={clientId}
                        rentableId={rentableId}
                        paymentIntentId={paymentIntentId}
                        startat={startDate}
                        endat={endDate}
                    />
                </Elements>
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
