package com.fabledclan.CustomBlocks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;
import com.fabledclan.CustomAbilities.Ability;

public class WandCrafterRecipes {
    private static Recipe[] RECIPES = null;

    public WandCrafterRecipes() {
        init();
    }

    public void init() {
        if (RECIPES != null) return;
        Recipe[] ret = {
            new Recipe("dash", false, Material.SUGAR),
            new Recipe("dragon_breath", true, Material.DRAGON_BREATH),
            new Recipe("dark_vortex", true, Material.ENDER_PEARL),
            new Recipe("feather", true, Material.FEATHER),
            new Recipe("feed", false, Material.COOKED_BEEF),
            new Recipe("fireball", true, Material.FIRE_CHARGE),
            new Recipe("heal", true, Material.GOLDEN_APPLE),
            new Recipe("ice_shard", true, Material.SNOWBALL),
            new Recipe("magic_missile", true, Material.FIREWORK_ROCKET),
            new Recipe("party", true, Material.GLASS),
            new Recipe("plague_swarm", true, Material.ROTTEN_FLESH),
            new Recipe("power_strike", false, Material.IRON_SWORD),
            new Recipe("summon_giant", true, Material.IRON_BLOCK),
            new Recipe("undead_army", true, Material.BONE),
            new Recipe("vader_choke", true, Material.REDSTONE),
            new Recipe("wrangle", false, Material.LEAD),
            new Recipe("yeet_boat", false, Material.OAK_BOAT)
        };
        RECIPES = ret;
    }

    public static Recipe[] getRecipes() {
        return RECIPES;
    }

    public class Recipe {
        private final Material[] RECIPE;
        private final ItemStack RESULT;

        public Recipe(String abilityName, Boolean isManaSpell, Material material) {
            RECIPE = recipe(isManaSpell, material);
            RESULT = result(abilityName);
        }

        public Material[] recipe(Boolean manaSpell, Material material) {
            if (manaSpell) {
                Material[] ret = {Material.STICK, Material.REDSTONE, material};
                return ret;
            } else {
                Material[] ret = {Material.STICK, Material.SUGAR, material};
                return ret;
            }
        }

        public ItemStack result(String abilityName) {
            ItemStack result = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = result.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(Main.getPlugin(), Ability.getKey()), PersistentDataType.STRING, abilityName);
            meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.BLUE + abilityName.toUpperCase());
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            result.setItemMeta(meta);
            return result;
        }

        public Material[] getRecipe() {
            return RECIPE;
        }

        public ItemStack getResult() {
            return RESULT;
        }
    }
}

