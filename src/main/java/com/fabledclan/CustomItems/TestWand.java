package com.fabledclan.CustomItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
    private final int FRAME_0 = 3580001;
    private final int FRAME_1 = 3580002;
    private final int FRAME_2 = 3580003;
    private final int FRAME_3 = 3580004;
    private final int FRAME_4 = 3580005;


    public TestWand() {
        super("testwand", Material.STICK, ChatColor.RESET + "Wand", false);
    }

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(FRAME_0);
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), item);
        recipe.shape("I  ", " S ", "  I");
        recipe.setIngredient('I', Material.IRON_NUGGET);
        recipe.setIngredient('S', Material.STICK);
        return recipe;
    }

    private void animate() {
        for (int i = 0; i < 4; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println("test");
                }
            }.runTaskLater(Main.getPlugin(), 20L);
        }
    }

    // handles the user casting the wand
    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        // checks if the player right clicked while holding a TestWand item
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!e.hasItem()) return;
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) {
            System.out.println("doesn't have model data");
            return;
        }
        System.out.println(meta.getCustomModelData());
        if (meta.getCustomModelData() < FRAME_0 || item.getType() != Material.STICK) return;
        System.out.println("right click");
        animate();
    }
}
