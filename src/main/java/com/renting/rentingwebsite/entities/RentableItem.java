package com.renting.rentingwebsite.entities;

import com.renting.rentingwebsite.URLUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rentable_item")
public class RentableItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url_name")
    private String urlName;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "display_price")
    private double displayPrice;

    @Column(name = "price")
    private long price;

    @OneToMany(mappedBy = "rentableItem")
    private List<Reservation> reservations;

    public RentableItem(String name, String type, String description, String productName, long price) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.productName = productName;
        this.displayPrice = price / 100.0;
        this.price = price;
        this.urlName = URLUtils.Urlify(name);
        this.reservations = new ArrayList<>();
    }

    public RentableItem() {
        this.name = "default";
        this.type = "default";
        this.description = "default desc";
        this.productName = "default";
        this.price = 0;
        this.displayPrice = 0;
        this.urlName = URLUtils.Urlify(this.name);
        this.reservations = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public long getPrice() {
        return price;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
