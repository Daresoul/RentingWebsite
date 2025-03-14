import {ImageDTO} from "./imageDTO.ts";
import {SpecificationDTO} from "./SpecificationDTO.ts";

export interface RentableDTOType {
	id: number;
	name: string;
	urlName: string,
	description: string;
	type: string;
	price: number;
	dates: Date[];
	images: ImageDTO[];
	specifications: SpecificationDTO[];
}