package com.fabledclan.CustomBlocks;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public abstract class CustomContainer extends CustomBlock {
    private static final String CONTAINER_KEY = "custom_container";
    private final Inventory inventory;

    public CustomContainer(String name) {
        super(name);
        this.inventory = makeInventory();
    }

    public abstract void interactEvent(PlayerInteractEvent event);

    public abstract Inventory makeInventory();

    public static String getContainerKey() {
        return CONTAINER_KEY;
    }

    public Inventory getInventory() {
        return inventory;
    }
}