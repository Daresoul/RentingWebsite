import axios, {AxiosResponse} from "axios";
import {RentableDTOType} from "../types/rentableDTOType.ts";
import {ReturnType} from "./APIDataTypes.ts";
import {StripePaymentIntentType} from "../types/stripePaymentIntentType.ts";
import {Range} from "react-date-range";

const API_BASE_URL = "/api";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

function fetchData<T>(response: { status: number; statusText: string; data: T }): ReturnType<T> {
    return {
        status: response.status,
        statusText: response.statusText,
        data: response.data,
        ok(){
            return [200, 201].includes(response.status)
        }
    };
}

export const fetchRentables = async () => {
    const response: AxiosResponse<RentableDTOType[], any> = await apiClient.get("/rentable-item");
    return fetchData(response);
};

export const fetchRentable = async (name: String) => {
    const response: AxiosResponse<RentableDTOType, any> = await apiClient.get("/rentable-item/" + name);
    return fetchData(response);
};

export const createPaymentIntent = async (email: String, startDate: Date, endDate: Date, rentableId: number) => {
    const response: AxiosResponse<StripePaymentIntentType, any> = await apiClient.post("/stripe/payment-intent", {
        receiptEmail: email,
        startDate: startDate,
        endDate: endDate,
        rentable_id: rentableId,
    });
    return fetchData(response);
};

export const createUser = async (name: String, email: String) => {
    const response = await apiClient.post("/user/create", {
        name: name,
        email: email,
    })

    return fetchData(response);
}

export const createReservation = async (
  dateRange: Range,
  clientId: number,
  rentableId: number,
  paidOnline: boolean,
  paymentIntentId: String
  ) => {
    const response = await apiClient.post("/reservation/create", {
        startat: dateRange.startDate,
        endat: dateRange.endDate,
        client: clientId,
        rentable: rentableId,
        paidOnline: paidOnline,
        paymentIntentId: paymentIntentId
    });

    return fetchData(response);
}
