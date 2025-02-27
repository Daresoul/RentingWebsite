package com.renting.rentingwebsite.entities;

import com.renting.rentingwebsite.URLUtils;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rentable")
public class Rentable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String urlName;

    private String type;

    private String description;

    private String product_name;

    private double price;

    @OneToMany(mappedBy = "machine")
    private List<Rentals> rentals;

    public Rentable(String name, String type, String description, String product_name, double price) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.product_name = product_name;
        this.price = price;
        this.urlName = URLUtils.Urlify(name);
        this.rentals = new ArrayList<>();
    }

    public Rentable() {
        this.name = "default";
        this.type = "default";
        this.description = "default desc";
        this.product_name = "default";
        this.price = 0;
        this.urlName = URLUtils.Urlify(this.name);
        this.rentals = new ArrayList<>();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Rentals> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rentals> rentals) {
        this.rentals = rentals;
    }

    public String getUrl_name() {
        return urlName;
    }

    public void setUrl_name(String url_name) {
        this.urlName = url_name;
    }
}
