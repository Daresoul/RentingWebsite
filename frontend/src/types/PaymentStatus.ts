export interface PaymentStatus {
	paid: PaymentIntentStatus,
	retry: boolean
}

export enum PaymentIntentStatus {
	COULDNOTPROCESS = "COULDNOTPROCESS",
	REQUIRES_PAYMENT_METHOD = "REQUIRES_PAYMENT_METHOD",
	REQUIRES_CONFIRMATION = "REQUIRES_CONFIRMATION",
	REQUIRES_ACTION = "REQUIRES_ACTION",
	PROCESSING = "PROCESSING",
	REQUIRES_CAPTURE = "REQUIRES_CAPTURE",
	SUCCEEDED = "SUCCEEDED",
	CANCELED = "CANCELED",
	REFUNDED = "REFUNDED"
}