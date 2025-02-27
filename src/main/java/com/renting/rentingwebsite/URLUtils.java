package com.renting.rentingwebsite;

public class URLUtils {

    public static String Urlify(String name) {
        name = name.replace(" ", "-");


        return name;
    }
}
