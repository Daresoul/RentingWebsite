import {useEffect, useState} from 'react';
import 'react-date-range/dist/styles.css';
import 'react-date-range/dist/theme/default.css';
import {DateRangePicker, RangeKeyDict, Range} from 'react-date-range';
import StripeCheckout from "./StripeCheckout.tsx";
import {fetchRentable} from "../services/api.ts";
import {RentableDTOType} from "../types/rentableDTOType.ts";

function Rentable() {


    const [rentable, setRentable] = useState<RentableDTOType | null>(null);

    const [dateRange, setDateRange] = useState<Range>({
        startDate: new Date(),
        endDate: new Date(),
        key: 'selection',
    });

    const [disabledDates, setDisabledDates] = useState<Date[]>([])

    useEffect(() => {
        const constFetchRental = async () => {
            var response = await fetchRentable(window.location.href.split("/")[4])

            if (response.status != 200) {
                console.error(response)
                return;
            }

            var data = response.data;

            let dates: Date[] = []
            for(let i = 0; i < data.dates.length; i++) {
                dates.push(new Date(data.dates[i]))
            }
            setDisabledDates(dates)
            setRentable(data)

        };

        constFetchRental();
    }, [])

    function handleSelect(ranges : RangeKeyDict){
        let selection = ranges["selection"]

        if (!selection.startDate || !selection.endDate) {
            console.log("Dates couldnt be gathered for select")
            return
        }

        let startDate = new Date(selection.startDate)
        let endDate = new Date(selection.endDate)

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
                    <h2>{ rentable ? rentable.name : "Loading..." }</h2>
                    <h4 style={{textAlign: "left"}}>{ rentable ? rentable.description : "Loading..." }</h4>
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
                { rentable ? (
                <StripeCheckout
                dateRange={dateRange}
                rentableId={rentable.id}
                ></StripeCheckout>) : "Loading..."
                }
            </div>
        </div>
    )
}

export default Rentable