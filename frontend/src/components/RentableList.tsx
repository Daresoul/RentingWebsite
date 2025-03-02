import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid2';
import {useEffect, useState} from "react";
import {fetchRentables} from "../services/api.ts";
import {RentableDTOType} from "../types/rentableDTOType.ts";
import { useNavigate } from "react-router-dom";

function RentableList() {
    const navigate = useNavigate();

    const [rentables, setRentables] = useState<RentableDTOType[]>([]);


    useEffect(() => {
        const gainData = async () => {
            const response = await fetchRentables()

            if (!response.ok()) {
                console.log('Error fetching Rentables from API');
                console.error(response.data);
            }

            setRentables(response.data)
        }

        gainData()
    }, [])

    const viewRentables = (rentable: RentableDTOType) => {
        navigate(`/rentable/${rentable.urlName}`);
    }


    return (
        <div className="App">
            <div className="App-intro">
                <h2>Rentable objects</h2>
                <Grid container spacing={-1}>
                    {rentables.map(rentable =>
                        <Paper
                          key={rentable.id}
                          elevation={1}
                          style={{ width: '400px', margin: '20px', padding: '20px', textAlign: 'center' }}
                          onClick={() => viewRentables(rentable)}
                        >
                            Name:{rentable.name}
                            <hr />
                            {rentable.description}
                            <hr />
                            {rentable.price}
                        </Paper>
                    )}
                </Grid>
            </div>
        </div>
    )
}

export default RentableList;
