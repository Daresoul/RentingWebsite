import React, {useEffect, useState} from 'react';
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file
import { DateRangePicker } from 'react-date-range';
import StripeCheckout from "./StripeCheckout";

function Rentable() {


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

        fetchRental();
    }, [])

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

    console.log(rentable)

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
                <StripeCheckout
                startDate={dateRange.startDate}
                endDate={dateRange.endDate}
                rentableId={rentable.id}
                ></StripeCheckout>
            </div>
        </div>
    )
}

export default Rentable