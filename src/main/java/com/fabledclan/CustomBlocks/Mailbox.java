package com.fabledclan.CustomBlocks;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Mailbox extends CustomContainer {
    private static final String MAILBOX_NAME = "mailbox";
    private static final String MAILBOX_DISPLAY_NAME = ChatColor.GOLD + "Mailbox";

    public Mailbox() {
        super(MAILBOX_NAME, MAILBOX_DISPLAY_NAME, Material.CHEST);
    }

    @Override
    public void interactEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        checkMail(player);
        player.openInventory(getInventory());
    }

    @Override
    public Inventory makeInventory() {
        return Bukkit.createInventory(null, 9, MAILBOX_DISPLAY_NAME);
    }

    private void checkMail(Player player) {
        // TODO: Implement this method to check the SQLite database for mail.
        // If mail is found, add it to the inventory and remove the entry from the database.
    }

    @Override
    public Recipe recipe() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recipe'");
    }

    @Override
    public void placeEvent(BlockPlaceEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'placeEvent'");
    }

    @Override
    public void breakEvent(BlockBreakEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'breakEvent'");
    }
}
