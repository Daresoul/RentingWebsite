export interface StripePaymentIntentType {
	paymentIntentId: string;
	clientSecret: string;
	paymentMethodTypes: string[];
	amount: number;
	cost: number;
}