package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "rentable_item_images", uniqueConstraints = @UniqueConstraint(columnNames = {"rentable_item_id", "show_index"}))
public class RentableItemImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_name", unique = true)
    private String imageName;

    @Column(name = "show_index", nullable = false)
    private long showIndex;

    @ManyToOne
    @JoinColumn(name = "rentable_item_id")
    private RentableItem rentableItem;

    public Long getId() {
        return id;
    }

    public String getImageName() {
        return imageName;
    }

    public RentableItem getRentableItem() {
        return rentableItem;
    }

    public long getShowIndex() {
        return showIndex;
    }

    public RentableItemImage() {
        this.imageName = "";
        this.rentableItem = null;
        this.showIndex = 0;
    }

    public RentableItemImage(String imageName, RentableItem rentableItem, long showIndex) {
        this.imageName = imageName;
        this.rentableItem = rentableItem;
        this.showIndex = showIndex;
    }
}
