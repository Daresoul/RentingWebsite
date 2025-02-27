package com.renting.rentingwebsite.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "rentals")
public class Rentals {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate start_at;

    private LocalDate end_at;

    private boolean paid;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Client user;

    @ManyToOne
    @JoinColumn(name = "rentable_id", referencedColumnName = "id")
    private Rentable machine;

    public Rentals(LocalDate start_at, LocalDate end_at, boolean paid, Client user, Rentable machine) {
        this.start_at = start_at;
        this.end_at = end_at;
        this.paid = paid;
        this.machine = machine;
        this.user = user;
    }

    public Rentals() {
        this.start_at = LocalDate.now();
        this.end_at = LocalDate.now();
        this.paid = false;
        this.machine = null;
        this.user = null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStart_at() {
        return start_at;
    }

    public void setStart_at(LocalDate start_at) {
        this.start_at = start_at;
    }

    public LocalDate getEnd_at() {
        return end_at;
    }

    public void setEnd_at(LocalDate end_at) {
        this.end_at = end_at;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Client getUser() {
        return user;
    }

    public void setUser(Client user) {
        this.user = user;
    }

    public Rentable getMachine() {
        return machine;
    }

    public void setMachine(Rentable machine) {
        this.machine = machine;
    }
}
