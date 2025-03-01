import { useStripe, useElements, CardElement } from "@stripe/react-stripe-js";
import { useState } from "react";

const CheckoutForm = ({ onPaymentSuccess, startat, endat, clientSecret, clientData, clientId, rentableId, paymentIntentId}) => {
    const stripe = useStripe();
    const elements = useElements();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const createRental = async () => {

        try {
            const response = await fetch("/api/rental/create", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    startat: "",
                    endat: "",
                    client: clientId,
                    rentable: rentableId,
                    paidOnline: true,
                    paymentIntentId: paymentIntentId
                }),
            });

            const data = await response.json();
            console.log(data)
            if (data.status === 200 || data.status === 201) {
                return true
            }
            else {
                setError(data.error)
                setLoading(false);
                return false
            }
        } catch (error) {
            setError(error);
            setLoading(false);
            return false
        }
    }

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!stripe || !elements) {
            setError("Stripe is not ready.");
            return;
        }
        setLoading(true);
        setError("");



        var rentalCreationSuccess = await createRental();
        console.log("the hell?")

        if (!rentalCreationSuccess) {
            return;
        }

        console.log("here?")

        const cardElement = elements.getElement(CardElement);

        const { paymentIntent, error } = await stripe.confirmCardPayment(
            clientSecret,
            {
                payment_method: { card: cardElement },
            }
        );

        if (error) {
            setError(error.message);
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
