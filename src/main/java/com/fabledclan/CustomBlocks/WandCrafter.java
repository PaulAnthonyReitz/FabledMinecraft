package com.fabledclan.CustomBlocks;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.DatabaseManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandCrafter extends CustomContainer {

    public WandCrafter() {
        super("wand_crafter");
    }

    public ItemStack item() {
        ItemStack wandCrafter = new ItemStack(Material.SMITHING_TABLE, 1);
        ItemMeta meta = wandCrafter.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(getPlugin(), getKey()), PersistentDataType.STRING, getName());
        meta.setDisplayName(ChatColor.RESET + "Wand Crafter");
        meta.setLore(new ArrayList<String>(Arrays.asList("Requires the power", "of Crying Obsidian")));
        wandCrafter.setItemMeta(meta);
        return wandCrafter;
    }

    public Recipe recipe(ItemStack item) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(getPlugin(), getName()), getItem());
        recipe.shape(" D ", "DCD", " D ");
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        return recipe;
    }

    public Inventory makeInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        return inventory;
    }

    public void placeEvent(BlockPlaceEvent event) {
        Block blockPlacedAgainst = event.getBlockAgainst();
        if (blockPlacedAgainst.getType() != Material.CRYING_OBSIDIAN) {
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "CAN NOT PLACE HERE"));
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        block.getState().setMetadata(getContainerKey(), new FixedMetadataValue(getPlugin(), getName()));

        DatabaseManager.insertCustomContainerBlock(block.getLocation(), getName());
    }

    public void breakEvent(BlockBreakEvent event) {
        DatabaseManager.deleteCustomContainerBlock(event.getBlock().getLocation());
    }

    public void interactEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().openInventory(getInventory());
    }
    
}
