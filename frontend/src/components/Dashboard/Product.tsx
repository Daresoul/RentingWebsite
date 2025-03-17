import React, { useState } from "react";
import { Box, TextField, Button, Typography, Paper, Table, TableBody, TableCell, TableContainer, TableRow, IconButton } from "@mui/material";
import {FiTrash2, FiPlus } from "react-icons/fi";

interface KeyValuePair {
	key: string,
	value: string
}


export const Product = () => {
	const [images, setImages] = useState<string[]>([]);
	const [specs, setSpecs] = useState<KeyValuePair[]>([]);
	const [title, setTitle] = useState<string>("");
	const [description, setDescription] = useState<string>("");
	const [pricePerDay, setPricePerDay] = useState<string>("");

	const handleImageUpload = (event : React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files == null) return

		const files = Array.from(event.target.files);
		const imageUrls = files.map(file => URL.createObjectURL(file));
		setImages(prev => [...prev, ...imageUrls]);
	};

	const addSpecRow = () => {
		setSpecs([...specs, { key: "", value: "" }]);
	};

	const updateSpec = (index: number, field: "key" | "value", value: string) => {
		const newSpecs = [...specs];
		newSpecs[index][field] = value;
		setSpecs(newSpecs);
	};

	const removeSpec = (index: number) => {
		setSpecs(specs.filter((_, i) => i !== index));
	};

	return (
		<Box>
			<Typography variant="h4" gutterBottom>Edit Product</Typography>

			<Box sx={{ mb: 4 }}>
				<TextField
					fullWidth
					label="Title"
					value={title}
					onChange={(e) => setTitle(e.target.value)}
					sx={{ mb: 2 }}
				/>

				<TextField
					fullWidth
					multiline
					rows={4}
					label="Description"
					value={description}
					onChange={(e) => setDescription(e.target.value)}
					sx={{ mb: 2 }}
				/>

				<TextField
					type="number"
					label="Price per Day"
					value={pricePerDay}
					onChange={(e) => setPricePerDay(e.target.value)}
					InputProps={{ startAdornment: "DKK" }}
					sx={{ mb: 2 }}
				/>
			</Box>

			<Box sx={{ mb: 4 }}>
				<Typography variant="h6" gutterBottom>Images</Typography>
				<input
					accept="image/*"
					type="file"
					multiple
					onChange={handleImageUpload}
					style={{ display: "none" }}
					id="image-upload"
				/>
				<label htmlFor="image-upload">
					<Button variant="contained" component="span">
						Upload Images
					</Button>
				</label>
				<Box sx={{ display: "flex", gap: 2, mt: 2, flexWrap: "wrap" }}>
					{images.map((image, index) => (
						<img
							key={index}
							src={image}
							alt={`Preview ${index}`}
							style={{ width: 100, height: 100, objectFit: "cover" }}
						/>
					))}
				</Box>
			</Box>

			<Box>
				<Typography variant="h6" gutterBottom>Specifications</Typography>
				<TableContainer component={Paper}>
					<Table>
						<TableBody>
							{specs.map((spec, index) => (
								<TableRow key={index}>
									<TableCell>
										<TextField
											fullWidth
											placeholder="Key"
											value={spec.key}
											onChange={(e) => updateSpec(index, "key", e.target.value)}
										/>
									</TableCell>
									<TableCell>
										<TextField
											fullWidth
											placeholder="Value"
											value={spec.value}
											onChange={(e) => updateSpec(index, "value", e.target.value)}
										/>
									</TableCell>
									<TableCell>
										<IconButton onClick={() => removeSpec(index)}>
											<FiTrash2 />
										</IconButton>
									</TableCell>
								</TableRow>
							))}
						</TableBody>
					</Table>
				</TableContainer>
				<Button
					startIcon={<FiPlus />}
					onClick={addSpecRow}
					sx={{ mt: 2 }}
				>
					Add Specification
				</Button>
			</Box>
		</Box>
	);
};