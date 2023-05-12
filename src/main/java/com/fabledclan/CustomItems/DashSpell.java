package com.fabledclan.CustomItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Ability;
import com.fabledclan.Main;

public class DashSpell extends CustomItem {
    public DashSpell() {
        super("dash_spell", false);
    }

    public ItemStack item() {
        ItemStack dashSpell = new ItemStack(Material.SUGAR, 1);
        ItemMeta meta = dashSpell.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "DASH");
        // meta.setCustomModelData(12358);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(Main.getPlugin(), Ability.getKey()), PersistentDataType.STRING, "dash");
        dashSpell.setItemMeta(meta);
        return dashSpell;
    }

    public Recipe recipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), getName()), getItem());
        recipe.addIngredient(1, Material.STICK);
        recipe.addIngredient(1, Material.SUGAR);
        return recipe;
    }
}
