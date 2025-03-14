export interface ReturnType<T> {
	status: number;
	statusText: string;
	data: T;
	ok: boolean;
}

export interface ErrorResponse {
	status: number;
	message: string;
	timestamp: string;

}