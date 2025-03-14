package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;

@Entity
public class RentableItemSpecificationKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_name", unique = true, nullable = false)
    private String keyName;

    public RentableItemSpecificationKey() {
        this.keyName = "";
    }

    public RentableItemSpecificationKey(String keyName) {
        this.keyName = keyName;
    }

    public Long getId() {
        return id;
    }

    public String getKeyName() {
        return keyName;
    }
}
