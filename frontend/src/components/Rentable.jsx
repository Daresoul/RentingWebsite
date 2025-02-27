import React, {useEffect, useState} from 'react';
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file
import { DateRangePicker } from 'react-date-range';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import {Button} from "@mui/material";
import {loadStripe} from '@stripe/stripe-js';
import CheckoutForm from "./StripeCheckoutForm";
import { Elements } from "@stripe/react-stripe-js";

const stripePromise = loadStripe("pk_test_1tPeufVbHquLBdyGpyIfQa2n");

function Rentable() {

    const [clientSecret, setClientSecret] = useState("");
    const [dpmCheckerLink, setDpmCheckerLink] = useState("");

    const [rentable, setRentable] = useState({});

    const [dateRange, setDateRange] = useState({
        startDate: new Date(),
        endDate: new Date(),
        key: 'selection',
    });

    const [disabledDates, setDisabledDates] = useState([])

    useEffect(() => {
        const fetchRental = async () => {
            try {
                const response = await fetch('/api/rentable/' + window.location.href.split("/")[4]);
                const body = await response.json()
                console.log(body)
                let dates = []
                for(let i = 0; i < body.dates.length; i++) {
                    dates.push(new Date(body.dates[i]))
                }
                setDisabledDates(dates)
                setRentable(body)
            } catch (error) {
                console.error('Error fetching clients:', error);
            }
        };

        const fetchPaymentIntent = async () => {
            fetch("/api/stripe/payment-intent", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ amount: 1000, receipt_email: "ABC@ABC.com" }),
            })
            .then((res) => res.json())
            .then((data) => {
                console.log(data.client_secret)
                setClientSecret(data.client_secret);
                // [DEV] For demo purposes only
                setDpmCheckerLink(data.dpmCheckerLink);
            });
        }

        fetchRental();
        fetchPaymentIntent();
    }, [])

    const appearance = {
        theme: 'stripe',
    };
    const options = {
        clientSecret,
        appearance,
    };
    function handleSelect(ranges){
        let x = ranges.selection
        let startDate = new Date(x.startDate)
        let endDate = new Date(x.endDate)

        setDateRange({
            startDate: startDate,
            endDate: endDate,
            key: 'selection',
        })
    }

    return (
        <div>
            <div style={{height: '100%', display: 'flex', flexDirection: 'row'}}>
                <div style={{display: "flex", width: '50%', flexDirection: 'column'}}>
                    <div style={{backgroundColor: 'blue', height: '500px'}}>
                        <img
                            src="https://th.bing.com/th/id/OIP.GPFEY6kfgxbsja6gmrW6rwAAAA?w=255&h=180&c=7&r=0&o=5&dpr=1.1&pid=1.7"
                            alt="abc"
                            style={{marginTop: "auto", marginBottom: "auto"}}
                        />
                    </div>
                </div>
                <div style={{backgroundColor: 'lightgray', width: '50%'}}>
                    <h2>{rentable.name}</h2>
                    <h4 style={{textAlign: "left"}}>{rentable.description}</h4>
                </div>
            </div>
            <div style={{width: '100%', backgroundColor: 'orange', display: 'flex', flexDirection: 'row'}}>
                <div>
                    <DateRangePicker
                        weekStartsOn={1}
                        disabledDates={disabledDates}
                        minDate={new Date()}
                        ranges={[dateRange]}
                        onChange={handleSelect}
                        months={2}
                        fixedHeight={true}
                        staticRanges={[]}
                        inputRanges={[]}
                    />
                </div>
                <div>
                        <Box
                            component="form"
                            sx={{ '& .MuiTextField-root': { m: 1, width: '25ch' } }}
                            noValidate
                            autoComplete="off"
                        >
                            <FormControl>
                                <FormLabel id="demo-row-radio-buttons-group-label">Payment</FormLabel>
                                <RadioGroup
                                    row
                                    aria-labelledby="demo-row-radio-buttons-group-label"
                                    name="row-radio-buttons-group"
                                >
                                    <FormControlLabel value={false} control={<Radio />} label="Ill pay when i pick it up" />
                                    <FormControlLabel value={true} control={<Radio />} label="Ill pay now" />
                                </RadioGroup>

                                <TextField
                                    variant="filled"
                                    placeholder="Name"
                                /> <br/>
                                <TextField
                                    variant="filled"
                                    placeholder="Phone number"
                                /> <br/>
                                <TextField
                                    variant="filled"
                                    placeholder="Email"
                                />
                                <Button>
                                    Place reservation
                                </Button>
                            </FormControl>
                        </Box>
                </div>
                <div>
                    <div className="App">
                        {clientSecret && (
                            <Elements options={options} stripe={stripePromise}>
                                    <CheckoutForm dpmCheckerLink={dpmCheckerLink}/>
                            </Elements>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Rentable