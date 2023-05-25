package com.fabledclan.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.fabledclan.DatabaseManager;
import com.fabledclan.CustomBlocks.ExperienceStorage;

public class ExperienceStorageInventory implements Listener {
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().contains(ExperienceStorage.getFirstItem())) return; // kinda janky way to test the inventory but works :shrug:
        event.setCancelled(true); // keeps the item in the inventory

        Inventory inventory = event.getClickedInventory();

        
        Player player = (Player) event.getWhoClicked();

        int storedTotalXP = DatabaseManager.getPlayerExperienceXPContainer(player.getUniqueId());
        if (storedTotalXP == -1) storedTotalXP = 0;
        int storedLevels = ExperienceStorage.getLevelFromTotalExperience(storedTotalXP);
        int storedRemainder = storedTotalXP - ExperienceStorage.totalExperience(storedLevels, 0);

        int playerLevel = player.getLevel();
        float f_playerRemainder = (ExperienceStorage.amountExpForLevel(playerLevel) * player.getExp());
        int playerRemainder = (int)f_playerRemainder;
        if (f_playerRemainder > 0.5 && playerRemainder == 0) playerRemainder = 1;
        int playerTotalXP = ExperienceStorage.totalExperience(playerLevel, playerRemainder);

        int playerXPChange = 0;
        int storedXPChange = 0;
        int xpToDeposit = 0;

        switch (event.getCurrentItem().getItemMeta().getDisplayName()) {
            case "TAKE ONE LEVEL":
                int xpToNextLevel = player.getExpToLevel();
                if (storedTotalXP >= xpToNextLevel) {
                    storedXPChange -= xpToNextLevel;
                    playerXPChange += xpToNextLevel;
                } else if (storedTotalXP > 0) {
                    storedXPChange -= storedTotalXP;
                    playerXPChange += storedTotalXP;
                } else {
                    return;
                }
                break;
            case "TAKE TEN LEVELS":
                int xpToTenLevels = player.getExpToLevel() + 
                (ExperienceStorage.totalExperience(playerLevel + 10, 0) - ExperienceStorage.totalExperience(playerLevel + 1, 0));
                if (storedTotalXP >= xpToTenLevels) {
                    storedXPChange -= xpToTenLevels;
                    playerXPChange += xpToTenLevels;
                } else if (storedTotalXP > 0) {
                    storedXPChange -= storedTotalXP;
                    playerXPChange += storedTotalXP;
                } else {
                    return;
                }
                break;
            case "TAKE ALL LEVELS":
                storedXPChange -= storedTotalXP;
                playerXPChange += storedTotalXP;
                break;
            case "DEPOSIT ONE LEVEL":
                if (playerRemainder != 0) {
                    xpToDeposit = playerRemainder;
                } else {
                    xpToDeposit = ExperienceStorage.totalExperience(playerLevel, 0) - ExperienceStorage.totalExperience(playerLevel - 1, 0);
                }
                if (playerTotalXP >= xpToDeposit && playerTotalXP > 0) {
                    storedXPChange += xpToDeposit;
                    playerXPChange -= xpToDeposit;
                } else if (playerTotalXP > 0) {
                    storedXPChange += playerTotalXP;
                    playerXPChange -= playerTotalXP;
                } else {
                    return;
                }
                break;
            case "DEPOSIT TEN LEVELS":
                if (playerRemainder != 0) {
                    xpToDeposit = playerRemainder + ExperienceStorage.totalExperience(playerLevel, 0) - ExperienceStorage.totalExperience(playerLevel - 9, 0);
                } else {
                    xpToDeposit = ExperienceStorage.totalExperience(playerLevel, 0) - ExperienceStorage.totalExperience(playerLevel - 10, 0);
                }
                if (playerTotalXP >= xpToDeposit && playerTotalXP > 0) {
                    storedXPChange += xpToDeposit;
                    playerXPChange -= xpToDeposit;
                } else if (playerTotalXP > 0) {
                    storedXPChange += playerTotalXP;
                    playerXPChange -= playerTotalXP;
                } else {
                    return;
                }
                break;
            case "DEPOSIT ALL LEVELS":
                storedXPChange += playerTotalXP;
                playerXPChange -= playerTotalXP;
                break;
            default:
                return;
        }

        int newStoredXP = storedTotalXP + storedXPChange;

        storedLevels = ExperienceStorage.getLevelFromTotalExperience(newStoredXP);
        storedRemainder = newStoredXP - ExperienceStorage.totalExperience(storedLevels, 0);

        ItemStack levelsItem = inventory.getItem(3);
        ItemStack xp = inventory.getItem(5);
        ItemMeta levelsMeta = levelsItem.getItemMeta();
        ItemMeta xpMeta = xp.getItemMeta();
        levelsMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "" + storedLevels);
        xpMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN
        + String.format("%.2f", ExperienceStorage.percentToNextLevel(storedLevels, storedRemainder) * 100) + "%");
        levelsItem.setItemMeta(levelsMeta);
        xp.setItemMeta(xpMeta);
        inventory.setItem(3, levelsItem);
        inventory.setItem(5, xp);

        player.giveExp(playerXPChange);

        if (DatabaseManager.getPlayerExperienceXPContainer(player.getUniqueId()) == -1) {
            DatabaseManager.insertPlayerExperience(player, newStoredXP);
        } else {
            DatabaseManager.updatePlayerExperience(player.getUniqueId(), newStoredXP);
        }
    }
}
