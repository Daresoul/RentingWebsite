import {UserType} from "./database_entities/userType.ts";

export interface AuthReturnType {
	token: string;
	user: UserType;
}