import { loadStripe } from "@stripe/stripe-js";

// Ensure loadStripe is only called once
export const stripePromise = loadStripe("pk_test_1tPeufVbHquLBdyGpyIfQa2n");