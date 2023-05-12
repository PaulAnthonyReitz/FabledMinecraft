package com.fabledclan;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class PrepareCraftItemListener implements Listener {
    @EventHandler
    public void onPrepareCraftItem(PrepareItemCraftEvent event) {
        // makes crafting with custom items impossible
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack item : items) {
            if (item == null) continue;
            if (item.getItemMeta().hasCustomModelData()) event.getInventory().setResult(null);
        }
    }
}
