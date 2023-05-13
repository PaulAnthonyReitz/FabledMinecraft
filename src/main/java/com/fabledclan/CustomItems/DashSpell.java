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

import com.fabledclan.Main;
import com.fabledclan.CustomAbilities.Ability;

public class DashSpell extends CustomItem {
    public DashSpell() {
        super("dash_spell", Material.SUGAR, (ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "DASH"), false);
    }

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(Main.getPlugin(), Ability.getKey()), PersistentDataType.STRING, "dash");
        item.setItemMeta(meta);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(Main.getPlugin(), getName()), item);
        recipe.addIngredient(1, Material.STICK);
        recipe.addIngredient(1, Material.SUGAR);
        return recipe;
    }
}
