package com.fabledclan.CustomStructures;

import org.bukkit.Location;

public abstract class CustomStructure {
    private static final String KEY = "custom_structure"; // key given to structure blocks as metadata
    private final String NAME;

    public CustomStructure(String name) {
        this.NAME = name;
    }

    public abstract Boolean validate(Location center); // abstract method that checks all block in the structure to check if it is valid

    public String getName() {
        return NAME;
    }

    public static String getKey() {
        return KEY;
    }
}
