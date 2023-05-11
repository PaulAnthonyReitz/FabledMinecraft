package com.fabledclan.recipes;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class FakeDiamond extends Recipe {
    public FakeDiamond(Plugin plugin) {
        ItemStack fake_diamond = new ItemStack(Material.DIAMOND, 1);
        ItemMeta fake_diamond_meta = fake_diamond.getItemMeta();
        fake_diamond_meta.setDisplayName("Fake Diamond");
        fake_diamond_meta.setLore(List.of("Looks real!"));
        fake_diamond.setItemMeta(fake_diamond_meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "fake_diamond"), fake_diamond);
        recipe.shape(" X ", "XXX", " X ");
        recipe.setIngredient('X', Material.DIRT);
        setShapedRecipe(recipe);
    }
}
