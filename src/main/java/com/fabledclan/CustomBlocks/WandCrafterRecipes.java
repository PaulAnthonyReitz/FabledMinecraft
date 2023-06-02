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
    private static WandRecipe[] RECIPES = null;

    public WandCrafterRecipes() {
        init();
    }

    public void init() {
        if (RECIPES != null) return;
        WandRecipe[] ret = {
            new WandRecipe("dash", false, Material.SUGAR),
            new WandRecipe("dragon_breath", true, Material.DRAGON_BREATH),
            new WandRecipe("dark_vortex", true, Material.ENDER_PEARL),
            new WandRecipe("feather", true, Material.FEATHER),
            new WandRecipe("feed", false, Material.COOKED_BEEF),
            new WandRecipe("fireball", true, Material.FIRE_CHARGE),
            new WandRecipe("heal", true, Material.GOLDEN_APPLE),
            new WandRecipe("ice_shard", true, Material.SNOWBALL),
            new WandRecipe("magic_missile", true, Material.FIREWORK_ROCKET),
            new WandRecipe("party", true, Material.GLASS),
            new WandRecipe("plague_swarm", true, Material.ROTTEN_FLESH),
            new WandRecipe("power_strike", false, Material.IRON_SWORD),
            new WandRecipe("summon_giant", true, Material.IRON_BLOCK),
            new WandRecipe("undead_army", true, Material.BONE),
            new WandRecipe("vader_choke", true, Material.REDSTONE),
            new WandRecipe("wrangle", false, Material.LEAD),
            new WandRecipe("yeet_boat", false, Material.OAK_BOAT)
        };
        RECIPES = ret;
    }

    public static WandRecipe[] getRecipes() {
        return RECIPES;
    }

    public class WandRecipe {
        private final Material[] RECIPE;
        private final ItemStack RESULT;

        public WandRecipe(String abilityName, Boolean isManaSpell, Material material) {
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

