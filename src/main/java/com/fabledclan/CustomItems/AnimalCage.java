package com.fabledclan.CustomItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;

import net.md_5.bungee.api.ChatColor;

public class AnimalCage extends CustomItem implements Listener {
    private NamespacedKey KEY;
    private final Map<UUID, Long> COOLDOWN_MAP = new HashMap<UUID, Long>();
    private final int COOLDOWN = 1000;

    public AnimalCage() {
        super("animal_cage", Material.MANGROVE_ROOTS, ChatColor.LIGHT_PURPLE + "ANIMAL CAGE", false, 
        new ArrayList<String>(Arrays.asList("Captures and stores animals")));
    }

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        this.KEY = new NamespacedKey(Main.getPlugin(), getName());
        data.set(KEY, PersistentDataType.STRING, "empty");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), item);
        recipe.shape("BBB", "BLB", "BBB");
        recipe.setIngredient('B', Material.IRON_BARS);
        recipe.setIngredient('L', Material.LEAD);
        return recipe;
    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent event) {
        // does basic checks to tell if the item is an animal cage
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(KEY, PersistentDataType.STRING)) return;
        String content = data.get(KEY, PersistentDataType.STRING);
        // checks if the cage is empty
        if (content.equals("empty")) {
            // gets the entity to capture
            Entity entity = event.getRightClicked();
            // checks if the entity is friendly/passive and not a monster or something like an armor stand or player
            if (entity.getSpawnCategory() == SpawnCategory.MISC || entity.getSpawnCategory() == SpawnCategory.MONSTER) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Can only capture passive mobs");
                return;
            }
            // clones the item to make a new one and decrement the old stack (handles if you hold multiple cages)
            ItemStack newItem = item.clone();
            newItem.setAmount(1);
            item.setAmount(item.getAmount() - 1);
            meta = newItem.getItemMeta();
            data = meta.getPersistentDataContainer();
            // sets the persistent data container to hold the uuid of the stored animal
            data.set(KEY, PersistentDataType.STRING, entity.getUniqueId().toString());
            meta.addEnchant(Enchantment.LUCK, 1, false);
            meta.setLore(new ArrayList<String>(Arrays.asList(entity.getCustomName().toString())));
            newItem.setItemMeta(meta);
            event.getPlayer().getInventory().addItem(newItem);
            // makes the animal persistent, invulnerable, silent, and invisible
            entity.setPersistent(true);
            entity.setInvulnerable(true);
            entity.setSilent(true);
            entity.setVisibleByDefault(false);
            COOLDOWN_MAP.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // basic checks to see if the event is someone placing an animal
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(KEY, PersistentDataType.STRING)) return;
        event.setCancelled(true);
        String content = data.get(KEY, PersistentDataType.STRING);
        if (content.equals("empty")) return;
        // also checks a stored cooldown map
        if (System.currentTimeMillis() - COOLDOWN_MAP.getOrDefault(event.getPlayer().getUniqueId(), 0L) < COOLDOWN) return;
        // loops through all the entities in the world to find the matching uuid
        UUID entityID = UUID.fromString(content);
        for (Entity entity : event.getPlayer().getWorld().getEntities()) {
            if (!entity.getUniqueId().equals(entityID)) continue;
            entity.teleport(event.getPlayer().getLocation());
            entity.setInvulnerable(false);
            entity.setPersistent(false);
            entity.setSilent(false);
            entity.setVisibleByDefault(true);
        }
        // resets the item
        data.set(KEY, PersistentDataType.STRING, "empty");
        meta.setLore(new ArrayList<String>(Arrays.asList("Captures and stores animals")));
        meta.removeEnchant(Enchantment.LUCK);
        item.setItemMeta(meta);
    }

    // stops the block from being placed
    @EventHandler
    public void onPlaceEvent(BlockPlaceEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = event.getItemInHand();
        if (item == null) return;
        if (!item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.STRING)) return;
        event.setCancelled(true);
    }
}
