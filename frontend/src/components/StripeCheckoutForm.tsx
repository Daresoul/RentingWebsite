import { useStripe, useElements, CardElement } from "@stripe/react-stripe-js";
import React, { useState } from "react";
import {Range} from "react-date-range";
import {createReservation} from "../services/api.ts";

interface StripeCheckoutFormProps {
    onPaymentSuccess: () => void;
    dateRange: Range;
    clientSecret: string;
    clientData: {
        email: string,
        address: string,
        phone: string,
        name: string
    };
    clientId: number;
    rentableId: number;
    paymentIntentId: string;
}

const CheckoutForm = ({ onPaymentSuccess, dateRange, clientSecret, clientId, rentableId, paymentIntentId} : StripeCheckoutFormProps) => {
    const stripe = useStripe();
    const elements = useElements();
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<String>("");

    const handleCreateRental = async () => {
        var response = await createReservation(dateRange, clientId, rentableId, true, paymentIntentId)

        if (!response.ok()) {
            return false
        }

        return true
    }

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!stripe || !elements) {
            setError("Stripe is not ready.");
            return;
        }
        setLoading(true);
        setError("");



        var rentalCreationSuccess = await handleCreateRental();

        if (!rentalCreationSuccess) {
            setError("Unexpected error occurred, please try again later.");
            return;
        }

        const cardElement = elements.getElement(CardElement);

        if (!cardElement) {
            console.error("Could not find the card element!");
            setError("Unexpected error occured, please reload the page and try again.");
            return;
        }

        const { paymentIntent, error } = await stripe.confirmCardPayment(
            clientSecret,
            {
                payment_method: { card: cardElement },
            }
        );

        if (error) {
            setError(error.message || "");
        } else if (paymentIntent.status === "succeeded") {
            console.log("✅ Payment successful!");
            onPaymentSuccess();
        }

        setLoading(false);
    };

    const cardStyle = {
        hidePostalCode: true, // ✅ Disable ZIP code input
        style: {
            base: {
                fontSize: "16px",
                color: "#424770",
                "::placeholder": {
                    color: "#aab7c4",
                },
            },
            invalid: {
                color: "#9e2146",
            },
        },
    };


    return (
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", alignItems: "center", width: "100%", height: "100%", padding: "20px"}}>
            <div style={{
                width: "100%",
                maxWidth: "400px",
                padding: "15px",
                border: "1px solid #ccc",
                borderRadius: "8px",
                boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
                marginBottom: "15px",
                backgroundColor: "#fff",
            }}>
                <CardElement options={cardStyle} />
            </div>
            <button type="submit" disabled={loading} style={{
                padding: "10px 20px",
                fontSize: "16px",
                borderRadius: "5px",
                backgroundColor: "#007bff",
                color: "#fff",
                border: "none",
                cursor: "pointer",
                transition: "background 0.3s ease",
            }}>
                {loading ? "Processing..." : "Pay Now"}
            </button>
            {error && <div style={{ color: "red", marginTop: "10px" }}>{error}</div>}
        </form>
    );
};

export default CheckoutForm;
