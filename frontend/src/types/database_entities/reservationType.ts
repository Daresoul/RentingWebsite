import {UserType} from "./userType.ts";
import {RentableItemType} from "./rentableItemType.ts";

export interface ReservationType {
	id: number;
	startAt: Date;
	endAt: Date;
	rentalPaidOnline: boolean;
	paid: boolean;
	paymentIntentId: String;
	user: UserType;
	rentableItem: RentableItemType;
}