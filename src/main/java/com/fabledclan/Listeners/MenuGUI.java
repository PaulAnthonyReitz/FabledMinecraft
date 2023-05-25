package com.fabledclan.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.fabledclan.Main;
import com.fabledclan.Player.PlayerStats;

import java.util.Arrays;

public class MenuGUI implements Listener {
    public static void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, 9, ChatColor.GREEN + "Upgrade Menu");

        ItemStack attack = createItem("Attack Upgrade", ChatColor.RED, Material.DIAMOND_SWORD, player, "attack");

        ItemStack defense = createItem("Defense Upgrade", ChatColor.BLUE, Material.DIAMOND_CHESTPLATE, player, "defense");

        ItemStack maxHealth = createItem("Max Health Upgrade", ChatColor.LIGHT_PURPLE, Material.EGG, player, "max_health");

        ItemStack movementSpeed = createItem("Movement Speed Upgrade", ChatColor.GRAY, Material.FEATHER, player, "movement_speed");

        gui.setItem(1, attack);
        gui.setItem(3, defense);
        gui.setItem(5, maxHealth);
        gui.setItem(7, movementSpeed);

        player.openInventory(gui);
    }

    private static ItemStack createItem(String name, ChatColor color, Material material, Player player, String stat) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + name);

        PlayerStats playerStats = Main.getPlayerStatsCache().get(player.getUniqueId());
        int playerExp = playerStats.getExp();
        int currentLevel = getCurrentLevel(stat, playerStats);
        int upgradeCost = getUpgradeCost(stat, currentLevel);

        String lore = playerExp >= upgradeCost
                ? ChatColor.GREEN + "Current EXP: " + playerExp + " Cost: " + upgradeCost
                : ChatColor.RED + "Current EXP: " + playerExp + " Cost: " + upgradeCost;
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    private static int getCurrentLevel(String stat, PlayerStats playerStats) {
        switch (stat) {
            case "attack":
                return (int) playerStats.getAttack();
            case "defense":
                return (int) playerStats.getDefense();
            case "movement_speed":
                return (int) playerStats.getMovementSpeed();
            case "max_health":
                return (int) playerStats.getMaxHealth();
            default:
                throw new IllegalArgumentException("Invalid stat provided.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Upgrade Menu")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                String itemName = clickedItem.getItemMeta().getDisplayName();

                // Implement the upgrade logic here
                if (itemName.equals(ChatColor.RED + "Attack Upgrade")) {
                    // Upgrade attack
                    upgradeSkill(player, "attack");
                } else if (itemName.equals(ChatColor.BLUE + "Defense Upgrade")) {
                    // Upgrade defense
                    upgradeSkill(player, "defense");
                } else if (itemName.equals(ChatColor.LIGHT_PURPLE + "Max Health Upgrade")) {
                    // Upgrade max health
                    upgradeSkill(player, "max_health");
                } else if (itemName.equals(ChatColor.GRAY + "Movement Speed Upgrade")) {
                    // Upgrade movement speed
                    upgradeSkill(player, "movement_speed");
                }
            }
        }
    }

    private static int getUpgradeCost(String stat, int currentLevel) {
        int upgradeCost;
    
        switch (stat) {
            case "attack":
            case "defense":
                upgradeCost = currentLevel * 50;
                break;
            case "max_health":
                upgradeCost = currentLevel * 100;
                break;
            case "movement_speed":
                upgradeCost = currentLevel * 1000;
                break;
            default:
                throw new IllegalArgumentException("Invalid stat provided.");
        }
        
    
        return upgradeCost;
    }

    private void upgradeSkill(Player player, String stat) {

        // Get the player's current EXP and stats from the database
        PlayerStats playerStats = Main.getPlayerStatsCache().get(player.getUniqueId());
        int playerExp = playerStats.getExp(); // Use the player's EXP from the database
    
        int currentLevel;
        int upgradeCost;
    
        switch (stat.toLowerCase()) {
            case "attack":
                currentLevel = (int) playerStats.getAttack();
                break;
            case "defense":
                currentLevel = (int) playerStats.getDefense();
                break;
            case "movement_speed":
                currentLevel = (int) playerStats.getMovementSpeed();
                break;
            case "max_health":
                currentLevel = (int) playerStats.getMaxHealth();
                break;
            default:
                player.sendMessage(ChatColor.RED + "Invalid stat provided.");
                return;
        }
    
        // Calculate the upgrade cost
        upgradeCost = getUpgradeCost(stat, currentLevel);
    
        if (playerExp >= upgradeCost) {
            // Deduct EXP from the player
            int updatedPlayerExp = playerExp - upgradeCost;
    
            // Update the player's stat level and EXP (using your database manager)
            switch (stat.toLowerCase()) {
                case "attack":
                    Main.getPlayerStatsCache().get(player.getUniqueId()).setAttack(currentLevel+1);
                    break;
                case "defense":
                    Main.getPlayerStatsCache().get(player.getUniqueId()).setDefense(currentLevel+1);
                    break;
                case "movement_speed":
                    Main.getPlayerStatsCache().get(player.getUniqueId()).setMovementSpeed(currentLevel+.025);
                    break;
                case "max_health":
                    Main.getPlayerStatsCache().get(player.getUniqueId()).setMaxHealth(currentLevel+2);
                    break;
            }
            
            Main.getPlayerStatsCache().get(player.getUniqueId()).setExp(updatedPlayerExp);
            player.sendMessage(ChatColor.GREEN + stat + " upgraded! New value: " + (currentLevel + 1));
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.GREEN + "You do not have enough EXP! " + playerExp + " needed: " + upgradeCost);
            player.closeInventory();
        }
    }
    
    
    
}

