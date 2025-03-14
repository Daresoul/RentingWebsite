import './App.css'
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import RentableList from "./components/RentableList.tsx";
import Rentable from "./components/Rentable.tsx";
import MenuAppBar from "./MUIComponents/MenuAppBar.tsx";
import {useAuthStore} from "./services/AuthStore.ts";
import {useEffect, useState} from "react";
import LoginModal from "./components/Modals/LoginModal.tsx";
import Loading from "./components/Loading.tsx";

function App() {

  const { login } = useAuthStore();

  const [isLoginModalOpen, setLoginModalOpen] = useState(false);
  const [modalType, setModalType] = useState<"login" | "register">("login");


  const handleOpenChange = (open: boolean, type?: ("login" | "register")) => {
    setLoginModalOpen(open);
    if(type) {
      setModalType(type);
    }
  };

  useEffect(() => {
    const token = sessionStorage.getItem("authToken");
    if (token) {
      login(token);
    }
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
    ],
    {
      future: {
        v7_relativeSplatPath: true,
        v7_startTransition: true,
      },
    }
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
