package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;

@Entity
public class RentableItemSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rentable_item_id", nullable = false)
    private RentableItem rentableItem;

    @ManyToOne
    @JoinColumn(name = "specification_key_id", nullable = false)
    private RentableItemSpecificationKey specificationKey;

    @Column(nullable = false)
    private String value;

    public RentableItemSpecification() {
        rentableItem = null;
        specificationKey = null;
        value = null;
    }

    public RentableItemSpecification(RentableItem rentableItem, RentableItemSpecificationKey specificationKey, String value) {
        this.rentableItem = rentableItem;
        this.specificationKey = specificationKey;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public RentableItem getRentableItem() {
        return rentableItem;
    }

    public RentableItemSpecificationKey getSpecificationKey() {
        return specificationKey;
    }

    public String getValue() {
        return value;
    }
}
