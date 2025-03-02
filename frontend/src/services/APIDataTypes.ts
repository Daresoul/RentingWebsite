export interface ReturnType<T> {
	status: number;
	statusText: string;
	data: T;
	ok: () => boolean;
}