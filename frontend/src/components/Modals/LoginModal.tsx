import { Dialog } from '@base-ui-components/react/dialog';
import styles from './index.module.css';
import TextField from "@mui/material/TextField";
import React, {useEffect, useState} from "react";
import { useAuthStore } from "../../services/AuthStore.ts";
import {checkLogin, isErrorResponse, registerUser} from "../../services/api.ts";

interface AuthModalProps {
	open: boolean;
	onOpenChange: (open: boolean, modalType?: "login" | "register") => void;
	initialMode: "login" | "register";
}

function AuthModal({ open, onOpenChange, initialMode}: AuthModalProps) {
	const { login, setUser } = useAuthStore();
	const [mode, setMode] = useState<"login" | "register">(initialMode);
	const [formData, setFormData] = useState({
		name: "",
		email: "jane@example.com",
		password: "password",
		confirmPassword: "",
	});

	useEffect(() => {
		setMode(initialMode);
	}, [initialMode]);

	const [error, setError] = useState<string>("");

	const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		setFormData({ ...formData, [event.target.name]: event.target.value });
	};

	const handleRegister = async () => {
		if (mode === "register") {

			if (formData.password !== formData.confirmPassword) {
				setError("Passwords do not match");
				return;
			}

			const response = await registerUser(formData.name, formData.email, formData.password);

			if (isErrorResponse(response)) {
				setError(response.data.message);
				return;
			}

			login(response.data.token);
			setUser(response.data.user);
			onOpenChange(false, undefined);
		}
	};

	const handleLogin = async () => {
		setError("");

		if (mode === "login") {
			const response = await checkLogin(formData.email, formData.password);

			if (isErrorResponse(response)) {
				setError(response.data.message);
				return;
			}

			login(response.data.token);
			setUser(response.data.user);
			onOpenChange(false, undefined);
		}
	};

	return (
		<Dialog.Root open={open}>
			<Dialog.Portal keepMounted>
				<Dialog.Backdrop className={styles.Backdrop} />
				<Dialog.Popup className={styles.Popup}>
					<Dialog.Title className={styles.Title}>
						<b>{mode === "login" ? "Login" : "Register"}</b>
					</Dialog.Title>
					<div className={styles.Description}>
						{mode === "register" && (
							<TextField
								variant="filled"
								placeholder="Name"
								name="name"
								value={formData.name}
								onChange={handleInputChange}
								sx={{ input: { color: "whitesmoke", width: "100%", minWidth: "310px", marginTop: "10px" } }}
							/>
						)}
						<TextField
							variant="filled"
							placeholder="Email"
							name="email"
							value={formData.email}
							onChange={handleInputChange}
							sx={{ input: { color: "whitesmoke", width: "100%", minWidth: "310px", marginBottom: "10px" } }}
						/>
						<TextField
							type="password"
							variant="filled"
							placeholder="Password"
							name="password"
							value={formData.password}
							onChange={handleInputChange}
							sx={{ input: { color: "whitesmoke", width: "100%", minWidth: "310px" } }}
						/>
						{mode === "register" && (
							<TextField
								type="password"
								variant="filled"
								placeholder="Confirm Password"
								name="confirmPassword"
								value={formData.confirmPassword}
								onChange={handleInputChange}
								sx={{ input: { color: "whitesmoke", width: "100%", minWidth: "310px", marginTop: "10px" } }}
							/>
						)}
						<span style={{ color: "red" }}>{error}</span>
					</div>
					<div className={styles.Actions}>
						<button className={styles.Button} onClick={() => onOpenChange(false, undefined)}>Close</button>
						<button className={styles.Button} onClick={mode === "login" ? handleLogin : handleRegister}>
							{mode === "login" ? "Login" : "Register"}
						</button>
					</div>
					<div style={{ textAlign: "center", marginTop: "10px" }}>
						{mode === "login" ? (
							<span style={{ cursor: "pointer", color: "whitesmoke" }} onClick={() => setMode("register")}>
                                Don't have an account? <b>Register</b>
                            </span>
						) : (
							<span style={{ cursor: "pointer", color: "whitesmoke" }} onClick={() => setMode("login")}>
                                Already have an account? <b>Login</b>
                            </span>
						)}
					</div>
				</Dialog.Popup>
			</Dialog.Portal>
		</Dialog.Root>
	);
}

export default AuthModal;
