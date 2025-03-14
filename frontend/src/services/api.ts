import axios, {AxiosResponse} from "axios";
import {RentableDTOType} from "../types/rentableDTOType.ts";
import {ErrorResponse, ReturnType} from "./APIDataTypes.ts";
import {StripePaymentIntentType} from "../types/stripePaymentIntentType.ts";
import {Range} from "react-date-range";
import {AuthReturnType} from "../types/AuthReturnType.ts";
import { useAuthStore } from "../services/AuthStore";
import {ReservationType} from "../types/database_entities/reservationType.ts";
import {DateToISO} from "../UTILS.ts"

const API_BASE_URL = "/api";

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
});

apiClient.interceptors.request.use((config) => {
    const { authToken } = useAuthStore.getState();

    if (authToken) {
        config.headers.Authorization = `Bearer ${authToken}`;
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

function fetchData<T>(response: { status: number; statusText: string; data: T }): ReturnType<T> {
    return {
        status: response.status,
        statusText: response.statusText,
        data: response.data,
        ok: true
    };
}

function handleErrorInResponse<T>(error: any): ReturnType<T> {
    if (error.response) {
        return {
            status: error.response.status,
            statusText: error.response.statusText,
            data: error.response.data as ErrorResponse,
            ok: false
        } as ReturnType<T>;
    } else if (error.request) {
        console.error("No response from server:", error.request);
        return {
            status: 0,
            statusText: "No Response",
            data: {
                message: "No response from the server. Please try again.",
                status: 0,
                timestamp: new Date().toISOString()
            } as ErrorResponse,
            ok: false
        } as ReturnType<T>;
    } else {
        console.error("Error:", error.message);
        return {
            status: 0,
            statusText: "Unknown Error",
            data: {
                message: "An unexpected error occurred.",
                status: 0,
                timestamp: new Date().toISOString()
            } as ErrorResponse,
            ok: false
        } as ReturnType<T>;
    }
}

export function isErrorResponse(response: any): response is ReturnType<ErrorResponse> {
    return response.ok === false;
}


export const checkLogin = async (email: String, password: String) => {
    try {
        const response: AxiosResponse<AuthReturnType, any> = await apiClient.post("/auth/login", {
            email: email,
            password: password,
        });

        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }
}

export const registerUser = async (name: String, email: String, password: String) => {
    try {
        const response : AxiosResponse<AuthReturnType, any>  = await apiClient.post("/user/create", {
            name: name,
            email: email,
            password: password
        })

        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }

}

export const fetchRentables = async () => {
    try {
        const response: AxiosResponse<RentableDTOType[], any> = await apiClient.get("/rentable-item");
        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }
};

export const fetchRentable = async (name: String) => {
    try {
        const response: AxiosResponse<RentableDTOType, any> = await apiClient.get("/rentable-item/" + name);
        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }
};

export const createPaymentIntent = async (startDate: Date, endDate: Date, rentableId: number) => {
    const { authToken } = useAuthStore.getState();

    console.log("authToken:", authToken);

    try {
        const response: AxiosResponse<StripePaymentIntentType, any> = await apiClient.post("/stripe/payment-intent", {
            startDate: DateToISO(startDate),
            endDate: DateToISO(endDate),
            rentable_id: rentableId,
        });
        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }
};

export const validateReservation = async (
  dateRange: Range,
  rentableId: number,
  ) => {
    try {
        const response : AxiosResponse<ReservationType, any> = await apiClient.post("/reservation/validate", {
            startat: dateRange.startDate,
            endat: dateRange.endDate,
            rentable: rentableId,
        });

        return fetchData(response);
    } catch (error: any) {
        return handleErrorInResponse<ErrorResponse>(error);
    }
}
