import './App.css'
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import RentableList from "./components/RentableList.tsx";
import Rentable from "./components/Rentable.tsx";

function App() {

  const router = createBrowserRouter(
    [
      {
        path: "/",
        element: <RentableList />,
        children: []
      },
      {
        path: "/rentable/:name",
        element: <Rentable />,
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
    <div className="App">
      <nav style={{width: '100%', height: '75px', backgroundColor: 'dimgray'}}>

      </nav>
      <RouterProvider router={router} />;
      <footer style={{backgroundColor: 'dimgray'}}>
        Copyright
      </footer>
    </div>
  );
}

export default App
