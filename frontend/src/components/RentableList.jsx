import React from 'react';
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid2';
import {useEffect, useState} from "react";

function RentableList() {
    const [rentables, setRentables] = useState([]);

    useEffect(() => {
        const fetchClients = async () => {
            try {
                const response = await fetch('/api/rentable');
                const body = await response.json();
                console.log(body);
                setRentables(body);
            } catch (error) {
                console.error('Error fetching clients:', error);
            }
        };

        fetchClients();
    }, [])


    return (
        <div className="App">
            <div className="App-intro">
                <h2>Rentable objects</h2>
                <Grid container spacing={-1}>
                    {rentables.map(rentable =>
                        <Paper elevation={1} style={{ width: '400px', margin: '20px', padding: '20px', textAlign: 'center' }}>
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
    );
}

export default RentableList;
