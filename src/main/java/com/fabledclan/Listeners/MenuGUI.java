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
    private static final int INVENTORY_SIZE = 9;

    public enum StatType {
        ATTACK("Attack", ChatColor.RED) {
            @Override
            public int getCurrentLevel(PlayerStats playerStats) {
                return (int) playerStats.getAttack();
            }
    
            @Override
            public void upgradeLevel(PlayerStats playerStats) {
                playerStats.setAttack(getCurrentLevel(playerStats) + 1);
            }
        },
        DEFENSE("Defense", ChatColor.BLUE) {
            @Override
            public int getCurrentLevel(PlayerStats playerStats) {
                return (int) playerStats.getDefense();
            }
    
            @Override
            public void upgradeLevel(PlayerStats playerStats) {
                playerStats.setDefense(getCurrentLevel(playerStats) + 1);
            }
        },
        MAX_HEALTH("Max Health", ChatColor.LIGHT_PURPLE) {
            @Override
            public int getCurrentLevel(PlayerStats playerStats) {
                return (int) playerStats.getMaxHealth();
            }
    
            @Override
            public void upgradeLevel(PlayerStats playerStats) {
                playerStats.setMaxHealth(getCurrentLevel(playerStats) + 2);
            }
        },
        MOVEMENT_SPEED("Movement Speed", ChatColor.GRAY) {
            @Override
            public int getCurrentLevel(PlayerStats playerStats) {
                return (int) playerStats.getMovementSpeed();
            }
    
            @Override
            public void upgradeLevel(PlayerStats playerStats) {
                playerStats.setMovementSpeed(getCurrentLevel(playerStats) + 0.025);
            }
        };
    
        private final String name;
        private final ChatColor color;
    
        StatType(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }
    
        public String getName() {
            return name;
        }
    
        public ChatColor getColor() {
            return color;
        }
    
        public abstract int getCurrentLevel(PlayerStats playerStats);
    
        public abstract void upgradeLevel(PlayerStats playerStats);
    
        public int getUpgradeCost(int currentLevel) {
            switch (this) {
                case ATTACK:
                case DEFENSE:
                    return currentLevel * 50;
                case MAX_HEALTH:
                    return currentLevel * 100;
                case MOVEMENT_SPEED:
                    return currentLevel * 1000;
                default:
                    throw new IllegalArgumentException("Invalid stat type.");
            }
        }
    }


    public static void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(player, INVENTORY_SIZE, ChatColor.GREEN + "Upgrade Menu");

        PlayerStats playerStats = Main.getPlayerStatsCache().get(player.getUniqueId());

        gui.setItem(1, createUpgradeItem(StatType.ATTACK, Material.DIAMOND_SWORD, player, playerStats));
        gui.setItem(3, createUpgradeItem(StatType.DEFENSE, Material.DIAMOND_CHESTPLATE, player, playerStats));
        gui.setItem(5, createUpgradeItem(StatType.MAX_HEALTH, Material.EGG, player, playerStats));
        gui.setItem(7, createUpgradeItem(StatType.MOVEMENT_SPEED, Material.FEATHER, player, playerStats));

        player.openInventory(gui);
    }

    private static ItemStack createUpgradeItem(StatType statType, Material material, Player player, PlayerStats playerStats) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(statType.getColor() + statType.getName() + " Upgrade");

        int currentLevel = statType.getCurrentLevel(playerStats);
        int upgradeCost = statType.getUpgradeCost(currentLevel);

        String lore = playerStats.getExp() >= upgradeCost
                ? ChatColor.GREEN + "Current Level: " + currentLevel + " Cost: " + upgradeCost
                : ChatColor.RED + "Current Level: " + currentLevel + " Cost: " + upgradeCost;
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Upgrade Menu")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getItemMeta() != null) {
                String itemName = clickedItem.getItemMeta().getDisplayName();

                for (StatType statType : StatType.values()) {
                    if (itemName.equals(statType.getColor() + statType.getName() + " Upgrade")) {
                        upgradeSkill(player, statType);
                        break;
                    }
                }
            }
        }
    }

    private void upgradeSkill(Player player, StatType statType) {
        PlayerStats playerStats = Main.getPlayerStatsCache().get(player.getUniqueId());
        int playerExp = playerStats.getExp();
        int currentLevel = statType.getCurrentLevel(playerStats);
        int upgradeCost = statType.getUpgradeCost(currentLevel);

        if (playerExp >= upgradeCost) {
            int updatedPlayerExp = playerExp - upgradeCost;

            statType.upgradeLevel(playerStats);
            playerStats.setExp(updatedPlayerExp);

            player.sendMessage(ChatColor.GREEN + statType.getName() + " upgraded! New value: " + statType.getCurrentLevel(playerStats));
            openGUI(player);
        } else {
            player.sendMessage(ChatColor.RED + "You do not have enough EXP! Current EXP: " + playerExp + " Needed: " + upgradeCost);
            player.closeInventory();
        }
    }
}

