package com.renting.rentingwebsite.enums;

public enum UserRole {
    ADMIN, MODERATOR, CUSTOMER;

    @Override
    public String toString() {
        return name();
    }
}
