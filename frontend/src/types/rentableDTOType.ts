export interface RentableDTOType {
	id: number;
	name: string;
	urlName: string,
	description: string;
	type: string;
	price: number;
	dates: [Date]
}