import {useEffect, memo} from "react";
import { Backdrop, CircularProgress, Typography } from "@mui/material";
import { styled as muiStyled } from "@mui/material/styles";
import { loadingStore } from "../services/AuthStore.ts";

const StyledBackdrop = muiStyled(Backdrop)(({ theme }) => ({
	zIndex: theme.zIndex.drawer + 1,
	backgroundColor: "rgba(0, 0, 0, 0.7)",
	display: "flex",
	flexDirection: "column",
	alignItems: "center",
	justifyContent: "center",
	gap: "20px"
}));

const LoadingContainer = muiStyled("div")({
	position: "relative",
	display: "flex",
	flexDirection: "column",
	alignItems: "center",
	padding: "32px",
	borderRadius: "8px",
	backgroundColor: "rgba(255, 255, 255, 0.1)",
	backdropFilter: "blur(8px)"
});

const GlobalLoadingModal = memo(() => {
	const { isLoading, message, setLoading } = loadingStore()

	useEffect(() => {
		setLoading(isLoading, message)
	}, [isLoading]);

	return (
		<StyledBackdrop
			open={isLoading}
	aria-label="loading-overlay"
		>
		<LoadingContainer>
	<CircularProgress size={60} thickness={4} color="primary" />
		{message && (
			<Typography
				variant="h6"
	component="div"
	sx={{ color: "#fff", mt: 2, textAlign: "center" }}
>
	{message}
	</Typography>
)}
	</LoadingContainer>
	</StyledBackdrop>
);
});


export default GlobalLoadingModal;