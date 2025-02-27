import './App.css';
import {
    BrowserRouter as Router,
    Routes,
    Route,
//    Link
} from 'react-router-dom';
import RentableList from "./components/RentableList";
import Rentable from "./components/Rentable";
import CompletePage from "./components/CompletePage";
import {Elements} from "@stripe/react-stripe-js";
import {loadStripe} from "@stripe/stripe-js";

const stripePromise = loadStripe("pk_test_1tPeufVbHquLBdyGpyIfQa2n");

function App() {
  return (
      <div className="App">
          <nav style={{width: '100%', height: '75px', backgroundColor: 'dimgray'}}>

          </nav>
          <Router>
              <Routes>
                  <Route path="/rentable/:name" element={<Rentable />} />
                  <Route path="/complete" element={<Elements stripe={stripePromise}><CompletePage /></Elements>} />
                  <Route path="/" element={<RentableList />} />
              </Routes>
          </Router>
          <footer style={{backgroundColor: 'dimgray'}}>
              Copyright
          </footer>
      </div>
  );
}

export default App;
