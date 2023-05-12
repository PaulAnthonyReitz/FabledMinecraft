package com.fabledclan.CustomBlocks;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.fabledclan.DatabaseManager;
import com.fabledclan.Main;

public abstract class CustomContainer extends CustomBlock {
    private static final String CONTAINER_KEY = "custom_container";
    private final Inventory inventory;

    public CustomContainer(String name, Material material) {
        super(name, material);
        this.inventory = makeInventory();
    }

    public void defaultPlace(BlockPlaceEvent event) {
        event.getBlock().getState().setMetadata(getContainerKey(), new FixedMetadataValue(Main.getPlugin(), getName()));
        DatabaseManager.insertCustomContainerBlock(event.getBlock().getLocation(), getName());
    }

    public void defaultBreak(BlockBreakEvent event) {
        DatabaseManager.deleteCustomContainerBlock(event.getBlock().getLocation());
        event.getBlock().removeMetadata(getContainerKey(), Main.getPlugin()); // removes the metadata from the block position (IMPORTANT)
        Boolean itemWillDrop = event.isDropItems();
        if (!itemWillDrop) return;
        event.setDropItems(false); // stops the default smithing table from dropping
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getItem()); // drops the ItemStack at the block location
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