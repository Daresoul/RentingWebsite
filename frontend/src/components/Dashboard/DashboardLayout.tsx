import { Outlet, useNavigate } from "react-router-dom";
import { Box, CssBaseline, ThemeProvider, createTheme, Drawer, List, ListItem, ListItemIcon, ListItemText } from "@mui/material";
import { styled } from "@mui/system";
import { FiHome, FiPieChart, FiPackage, FiShoppingCart, FiUsers, FiSettings } from "react-icons/fi";

const darkTheme = createTheme({
	palette: {
		mode: "dark",
		primary: { main: "#90caf9" },
		secondary: { main: "#f48fb1" },
		background: {
			default: "#121212",
			paper: "#1e1e1e"
		}
	}
});

const drawerWidth = 240;

const MainContainer = styled(Box)(({ theme }) => ({
	display: "flex",
	minHeight: "100vh"
}));

const ContentWrapper = styled(Box)(({ theme }) => ({
	flexGrow: 1,
	padding: theme.spacing(3),
	backgroundColor: theme.palette.background.default
}));

const StyledDrawer = styled(Drawer)(({ theme }) => ({
	width: drawerWidth,
	flexShrink: 0,
	"& .MuiDrawer-paper": {
		width: drawerWidth,
		boxSizing: "border-box"
	}
}));

const menuItems = [
	{ text: "Dashboard", icon: <FiHome />, id: "dashboard" },
	{ text: "Analytics", icon: <FiPieChart />, id: "analytics" },
	{ text: "Products", icon: <FiPackage />, id: "products" },
	{ text: "Orders", icon: <FiShoppingCart />, id: "orders" },
	{ text: "Users", icon: <FiUsers />, id: "users" },
	{ text: "Settings", icon: <FiSettings />, id: "settings" }
];

const DashboardLayout = () => {
	const navigate = useNavigate();

	return (
		<ThemeProvider theme={darkTheme}>
			<CssBaseline />
			<MainContainer>
				<StyledDrawer variant="permanent">
					<List>
						{menuItems.map((item) => (
							<ListItem
								key={item.id}
								onClick={() => navigate(`/dashboard/${item.id === "dashboard" ? "" : item.id}`)}
							>
								<ListItemIcon>{item.icon}</ListItemIcon>
								<ListItemText primary={item.text} />
							</ListItem>
						))}
					</List>
				</StyledDrawer>

				<ContentWrapper>
					<Outlet />
				</ContentWrapper>
			</MainContainer>
		</ThemeProvider>
	);
};

export default DashboardLayout;