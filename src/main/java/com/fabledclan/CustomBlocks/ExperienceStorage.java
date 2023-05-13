package com.fabledclan.CustomBlocks;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.fabledclan.DatabaseManager;
import com.fabledclan.Main;

public class ExperienceStorage extends CustomContainer {
    private static final String INVENTORY_NAME = ChatColor.DARK_GREEN + "XP CONTAINER";
    private static ItemStack firstItem;

    public ExperienceStorage() {
        super("experience_storage", (ChatColor.BOLD + "" + ChatColor.GREEN + "XP Storage Container")
        , Material.LODESTONE, new ArrayList<String>(Arrays.asList("Stores experience like an ender chest")));
    }

    public Recipe recipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), getItem());
        recipe.shape("SLS", "EPE", "SLS");
        recipe.setIngredient('L', Material.LIME_STAINED_GLASS);
        recipe.setIngredient('E', Material.EMERALD);
        recipe.setIngredient('P', Material.ENDER_PEARL);
        recipe.setIngredient('S', Material.SMOOTH_STONE);
        return recipe;
    }

    public Inventory makeInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, INVENTORY_NAME);

        ItemStack takeOne = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta takeOneMeta = takeOne.getItemMeta();
        ItemStack takeTen = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta takeTenMeta = takeTen.getItemMeta();
        ItemStack takeAll = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta takeAllMeta = takeAll.getItemMeta();
        ItemStack depoOne = new ItemStack(Material.HOPPER, 1);
        ItemMeta depoOneMeta = depoOne.getItemMeta();
        ItemStack depoTen = new ItemStack(Material.HOPPER, 1);
        ItemMeta depoTenMeta = depoTen.getItemMeta();
        ItemStack depoAll = new ItemStack(Material.HOPPER, 1);
        ItemMeta depoAllMeta = depoAll.getItemMeta();

        ItemStack levels = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta levelsMeta = levels.getItemMeta();
        ItemStack xp = new ItemStack(Material.EMERALD, 1);
        ItemMeta xpMeta = xp.getItemMeta();

        ItemStack empty = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta emptyMeta = empty.getItemMeta();

        takeOneMeta.setDisplayName(ChatColor.RESET + "TAKE ONE LEVEL");
        takeTenMeta.setDisplayName(ChatColor.RESET + "TAKE TEN LEVELS");
        takeAllMeta.setDisplayName(ChatColor.RESET + "TAKE ALL LEVELS");
        depoOneMeta.setDisplayName(ChatColor.RESET + "DEPOSIT ONE LEVEL");
        depoTenMeta.setDisplayName(ChatColor.RESET + "DEPOSIT TEN LEVELS");
        depoAllMeta.setDisplayName(ChatColor.RESET + "DEPOSIT ALL LEVELS");
        emptyMeta.setDisplayName(" ");

        levelsMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "0");
        xpMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "0");

        takeOne.setItemMeta(takeOneMeta);
        takeTen.setItemMeta(takeTenMeta);
        takeAll.setItemMeta(takeAllMeta);
        depoOne.setItemMeta(depoOneMeta);
        depoTen.setItemMeta(depoTenMeta);
        depoAll.setItemMeta(depoAllMeta);
        empty.setItemMeta(emptyMeta);
        levels.setItemMeta(levelsMeta);
        xp.setItemMeta(xpMeta);

        ItemStack[] menuItems = {takeOne, takeTen, takeAll, levels, empty, xp, depoAll, depoTen, depoOne};
        inventory.setContents(menuItems);

        setFirstItem(takeOne);

        return inventory;
    }

    public void placeEvent(BlockPlaceEvent event) {
        defaultPlace(event);
    }

    public void breakEvent(BlockBreakEvent event) {
        defaultBreak(event);
    }

    public void interactEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
        Inventory inventory = makeInventory();
        int containerXP = DatabaseManager.getPlayerExperience(event.getPlayer().getUniqueId());
        if (containerXP != -1) {
            int level = getLevelFromTotalExperience(containerXP);
            int remainder = containerXP - totalExperience(level, 0);
            ItemStack levels = inventory.getItem(3);
            ItemStack xp = inventory.getItem(5);
            ItemMeta levelsMeta = levels.getItemMeta();
            ItemMeta xpMeta = xp.getItemMeta();
            levelsMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + level);
            xpMeta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + 
            String.format("%.2f", ExperienceStorage.percentToNextLevel(level, remainder) * 100) + "%");
            levels.setItemMeta(levelsMeta);
            xp.setItemMeta(xpMeta);
            inventory.setItem(3, levels);
            inventory.setItem(5, xp);
        }
        event.getPlayer().openInventory(inventory);
    }

    public static String getInventoryName() {
        return INVENTORY_NAME;
    }

    private static void setFirstItem(ItemStack item) {
        firstItem = item;
    }

    public static ItemStack getFirstItem() {
        return firstItem;
    }

    // Math formulas pulled from: https://minecraft.fandom.com/wiki/Experience
    public static int getLevelFromTotalExperience(int totalExperience) {
        int level = 0;
        if (totalExperience < 353) {
            level = (int)(Math.sqrt(totalExperience + 9)) - 3;
        } else if (totalExperience < 1508) {
            level = (int)((81.0/10.0) + Math.sqrt((2.0/5.0) * ((double)totalExperience - (7839.0/40.0))));
        } else {
            level = (int)((325.0/18.0) + (Math.sqrt((2.0/9.0) * ((double)totalExperience - (54215.0/72.0)))));
        }
        return level;
    }

    public static int totalExperience(int level, int remainder) {
        int ret = remainder;
        float f_level = (float) level;
        if (level < 17) { 
            ret += (int)((f_level * f_level) + (6.0 * f_level));
        } else if (level < 32) {
            ret += (int)((2.5 * f_level * f_level) - (40.5 * f_level) + 360.0);
        } else {
            ret += (int)((4.5 * f_level * f_level) - (162.5 * f_level) + 2220.0);
        }
        return ret;
    }

    public static float percentToNextLevel(int level, int remainder) {
        float out = 0;
        out = ((float)remainder) / ((float)amountExpForLevel(level + 1));
        return out;
    }

    public static int amountExpForLevel(int level) {
        int out = 0;
        if (level < 17) {
            out = 2 * level + 7;
        } else if (level < 32) {
            out = 5 * level - 38;
        } else {
            out = 9 * level - 158;
        }
        return out;
    }
}
