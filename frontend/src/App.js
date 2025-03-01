import './App.css';
import RentableList from "./components/RentableList";
import Rentable from "./components/Rentable";
import CompletePage from "./components/CompletePage";
import {Elements} from "@stripe/react-stripe-js";
import {stripePromise} from "./components/LoadStripe";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

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

export default App;
