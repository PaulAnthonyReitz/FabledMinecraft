package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fabledclan.CustomItems.*;

public class CustomItemRegistry {
    private static ArrayList<CustomItem> ITEMS = null;

    public static void initializeItems() {
        if (ITEMS != null) return;
        ArrayList<CustomItem> items = new ArrayList<CustomItem>(Arrays.asList(
            // ADD ITEMS HERE:
            new DashSpell(),
            new AnimalCage()
        ));
        ITEMS = items;
    }

    public static ArrayList<CustomItem> getItems() {
        return ITEMS;
    }
}
