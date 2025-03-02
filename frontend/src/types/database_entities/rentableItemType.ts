import {ReservationType} from "./reservationType.ts";

export interface RentableItemType {
	id: number;
	name: string;
	urlName: string;
	type: string;
	description: string;
	productName: string;
	displayPrice: number;
	price: number;
	reservations: ReservationType[];
}