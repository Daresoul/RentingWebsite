import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import {UserType} from "../types/database_entities/userType.ts";

interface AuthState {
	isLoggedIn: boolean;
	authToken: string | null;
	user: UserType | null;
	setUser: (user: UserType | null) => void;
	login: (token: string) => void;
	logout: () => void;
}

interface LoadingStore {
	isLoading: boolean;
	message: string;
	setLoading: (status: boolean, message: string) => void;
}

export const loadingStore = create<LoadingStore>()((set) => ({
	isLoading: false,
	message: "With message",
	setLoading: (status: boolean, message: string) => set({ isLoading: status, message: message })
}));


export const useAuthStore = create<AuthState>()(
	persist(
		(set) => ({
			isLoggedIn: false,
			authToken: null,
			user: null,
			setUser: (user: UserType | null) => set({ user }),
			login: (token: string) => {
				set({ isLoggedIn: true, authToken: token });
			},
			logout: () => {
				set({ isLoggedIn: false, authToken: null, user: null });
			},
		}),
		{
			name: "auth-storage",
			storage: createJSONStorage(() => localStorage),
		}
	)
);
