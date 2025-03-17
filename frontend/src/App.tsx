import './App.css'
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import RentableList from "./components/RentableList.tsx";
import Rentable from "./components/Rentable.tsx";
import MenuAppBar from "./MUIComponents/MenuAppBar.tsx";
import {useAuthStore} from "./services/AuthStore.ts";
import {useEffect, useState} from "react";
import LoginModal from "./components/Modals/LoginModal.tsx";
import Loading from "./components/Loading.tsx";
import {checkLoggedIn, isErrorResponse} from "./services/api.ts";
import DashboardLayout from "./components/Dashboard/DashboardLayout.tsx";
import {Product} from "./components/Dashboard/Product.tsx";

function App() {

  const { authToken, logout, setUser, login} = useAuthStore();

  const [isLoginModalOpen, setLoginModalOpen] = useState(false);
  const [modalType, setModalType] = useState<"login" | "register">("login");


  const handleOpenChange = (open: boolean, type?: ("login" | "register")) => {
    setLoginModalOpen(open);
    if(type) {
      setModalType(type);
    }
  };

  useEffect(() => {
    const checkLogin = async () => {
      if (authToken) {
        var response = await checkLoggedIn();

        if (isErrorResponse(response)) {
          logout()
          return;
        }

        login(authToken)
        setUser(response.data)

      } else {
        logout()
      }
    }

    checkLogin()
  }, []);



  const router = createBrowserRouter(
    [
      {
        path: "/",
        element: <RentableList />,
        children: []
      },
      {
        path: "/rentable/:name",
        element: <Rentable onOpenChange={handleOpenChange} />,
        children: []
      },
      {
        path: "/dashboard",
        element: <DashboardLayout />,
        children: [
          {
            path: "dashboard",
            element: <Product />
          },
          {
            path: "new-product",
            element: <Product />
          }
        ]
      }
    ],
  );


  return (
    <div className="App" style={{width: '100%', height:'100%'}}>
      <MenuAppBar onOpenChange={handleOpenChange}></MenuAppBar>
      <LoginModal open={isLoginModalOpen} onOpenChange={handleOpenChange} initialMode={modalType}></LoginModal>
      <RouterProvider router={router} />;
      <Loading></Loading>
    </div>
  );
}

export default App
