package com.fabledclan.CustomItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;

public class TestWand extends CustomItem implements Listener {
    private final int MODEL_DATA = 3580001;
    final int INV_SLOT = 9;

    public TestWand() {
        super("testwand", Material.BOW, ChatColor.RESET + "Wand", false);
    }

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(MODEL_DATA);
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), item);
        recipe.shape("I  ", " S ", "  I");
        recipe.setIngredient('I', Material.IRON_NUGGET);
        recipe.setIngredient('S', Material.STICK);
        return recipe;
    }

    private void cast() {
        
    }

    // adds and removes an arrow in 5 ticks, stores and replaces the item that was in the slot of there was one
    private void wand_held(Player p) {
        Inventory inv = p.getInventory();
        if (inv.contains(Material.ARROW)) return;
        ItemStack item_swapped = inv.getItem(INV_SLOT);
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        meta.setCustomModelData(3580001);
        meta.setDisplayName("");
        arrow.setItemMeta(meta);
        inv.setItem(INV_SLOT, arrow);
        new BukkitRunnable() {
            @Override
            public void run() {
                inv.clear(INV_SLOT);
                if (item_swapped != null) {
                    inv.setItem(INV_SLOT, item_swapped);
                }
            }
        }.runTaskLater(Main.getPlugin(), (long)5);
    }

    // handles the user casting the wand
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        // checks if the player right clicked while holding a TestWand item
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!e.hasItem()) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return;
        if (meta.getCustomModelData() != MODEL_DATA || item.getType() != Material.BOW) return;
        wand_held(e.getPlayer());
    }

    // cancels the shooting event
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent e) {
        // ignores bows without custom model data
        if (!e.getBow().getItemMeta().hasCustomModelData()) return;
        // checks for wand custom model data on the bow
        if (e.getBow().getItemMeta().getCustomModelData() != MODEL_DATA) return;

        // stops the event
        e.setCancelled(true);
        cast();
    }
}
