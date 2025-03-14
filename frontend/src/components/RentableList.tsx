import {useEffect, useState} from "react";
import { Box, Button, Card, CardContent, CardMedia, Grid, Typography } from "@mui/material";
import { styled } from "@mui/system";
import { useNavigate } from "react-router-dom";
import {fetchRentables, isErrorResponse} from "../services/api.ts";
import {RentableDTOType} from "../types/rentableDTOType.ts";

const ProductCard = styled(Card)(() => ({
    height: "100%",
    display: "flex",
    flexDirection: "column",
    transition: "transform 0.2s ease-in-out",
    "&:hover": {
        transform: "translateY(-4px)",
        boxShadow: "rgba(0, 0, 0, 0.2) 0px 2px 4px",
    },
}));

const StyledProductCard = (props : any) => <ProductCard {...props} />;


const ProductImage = styled(CardMedia)({
    paddingTop: "56.25%", // 16:9 aspect ratio
    position: "relative",
    "&.MuiCardMedia-root": {
        backgroundSize: "cover"
    }
});

const Description = styled(Typography)({
    display: "-webkit-box",
    WebkitLineClamp: 2,
    WebkitBoxOrient: "vertical",
    overflow: "hidden",
    textOverflow: "ellipsis",
    marginBottom: 12
});

const ProductGrid = () => {

    const navigate = useNavigate();

    const [rentables, setRentables] = useState<RentableDTOType[]>([]);

    const viewRentables = (rentable: RentableDTOType) => {
        navigate(`/rentable/${rentable.urlName}`);
    };

    useEffect(() => {
        const gainData = async () => {
            const response = await fetchRentables()

            if (isErrorResponse(response)) {
                return;
            }

            console.log(response)

            setRentables(response.data)
        }

        gainData()
    }, [])

    return (
      <Box sx={{ flexGrow: 1, p: 3 }}>
          <Grid container spacing={3} sx={{ pb: 4 }}>
              {rentables && rentables.map((rentable) => (
                <Grid
                  item
                  xs={12}
                  sm={6}
                  md={4}
                  lg={3}
                  key={rentable.id}
                  sx={{
                      display: "flex",
                      flexDirection: "column"
                  }}
                >
                    <StyledProductCard elevation={3}>
                        <ProductImage
                          image={rentable.images[0].imageUrl}
                          title={rentable.name}
                          onError={(e) => {
                              const target = e.target as HTMLImageElement;
                              target.onerror = null;
                              target.src = "https://images.unsplash.com/photo-1563089145-599997674d42";
                          }}
                        />
                        <CardContent sx={{ flexGrow: 1 }}>
                            <Typography gutterBottom variant="h6" component="h2">
                                {rentable.name}
                            </Typography>
                            <Description variant="subtitle1" color="text.secondary">
                                {rentable.description}
                            </Description>
                            <p style={{ color: "lightgray"}}>
                                {rentable.specifications.map((spec, number) => (
                                  <span>{spec.key} - {spec.value} {rentable.specifications.length - 1 != number && "|"} </span>
                                ))}
                            </p>
                            <Button
                              variant="contained"
                              fullWidth
                              onClick={() => viewRentables(rentable)}
                              sx={{ mt: 2 }}
                            >
                                Learn more
                            </Button>
                        </CardContent>
                    </StyledProductCard>
                </Grid>
              ))}
          </Grid>
      </Box>
    );
};

export default ProductGrid;