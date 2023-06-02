package com.fabledclan.CustomItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;

public class TestWand extends CustomItem implements Listener {
    private final int DEFAULT_FRAME = 3580001;
    private final int NUM_FRAMES = 5;


    public TestWand() {
        super("testwand", Material.STICK, ChatColor.RESET + "Wand", false);
    }

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(DEFAULT_FRAME);
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), item);
        recipe.shape("I  ", " S ", "  I");
        recipe.setIngredient('I', Material.IRON_NUGGET);
        recipe.setIngredient('S', Material.STICK);
        return recipe;
    }

    // Increments the item's CustomModelData by 1 every second for NUM_FRAMES amount of times
    private void animate(Player p) {
        long time = 0L;
        for (int i = 0; i < NUM_FRAMES; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    int data = meta.getCustomModelData() - DEFAULT_FRAME;
                    data = (++data % NUM_FRAMES) + DEFAULT_FRAME;
                    meta.setCustomModelData(data);
                    item.setItemMeta(meta);
                }
            }.runTaskLater(Main.getPlugin(), time);
            time += 3L;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("cast");
            }
        }.runTaskLater(Main.getPlugin(), time);
    }

    // handles the user casting the wand
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        // checks if the player right clicked while holding a TestWand item
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!e.hasItem()) return;
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta.getCustomModelData() < DEFAULT_FRAME || item.getType() != Material.STICK) return;
        animate(e.getPlayer());
    }
}
