package com.fabledclan.CustomItems;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import com.fabledclan.Main;
import com.fabledclan.CustomAbilities.Ability;

import net.md_5.bungee.api.ChatColor;

public class Wand extends CustomItem implements Listener {
    private NamespacedKey ID_KEY;
    private NamespacedKey INV_KEY;
    private String INV_NAME = ChatColor.BOLD + "WAND INVENTORY";

    public Wand() {
        super("wand", Material.STICK, ChatColor.BOLD + "" + ChatColor.GOLD + "WAND", false,
        new ArrayList<String>(Arrays.asList("Can hold multiple spells", "Shift + RClick to open")));
    }    

    public Recipe recipe() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        this.ID_KEY = new NamespacedKey(Main.getPlugin(), "wand");
        this.INV_KEY = new NamespacedKey(Main.getPlugin(), "inventory");
        data.set(ID_KEY, PersistentDataType.INTEGER, 0);
        item.setItemMeta(meta);
        ShapedRecipe recipe = new ShapedRecipe(ID_KEY, item);
        recipe.shape("  N", " S ", "N  ");
        recipe.setIngredient('N', Material.GOLD_NUGGET);
        recipe.setIngredient('S', Material.STICK);
        return recipe;
    }

    private Inventory constructInventory(ItemStack wand) {
        // init inventory
        Inventory inv = Bukkit.createInventory(null, 9, INV_NAME);
        inv.setMaxStackSize(1);

        ItemMeta wand_meta = wand.getItemMeta();
        PersistentDataContainer wand_data = wand_meta.getPersistentDataContainer();

        // deserialize inventory key from the wand and add items
        if (wand_data.has(INV_KEY, PersistentDataType.STRING)) {
            ArrayList<ItemStack> items = deserialize(wand_data.get(INV_KEY, PersistentDataType.STRING));
            for (ItemStack item : items) {
                if (item == null) continue;
                inv.addItem(item);
            }
        }
        return inv;
    }

    @EventHandler
    public void onShiftUse(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        // check if the player is sneak right clicking and is holding an item
        if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) || !e.getPlayer().isSneaking()) return;
        if (!e.hasItem()) return;

        // check if the item has the "wand" key in persistent data container
        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (!data.has(ID_KEY, PersistentDataType.INTEGER)) return;

        // check if the ID has been initialized
        if (data.get(ID_KEY, PersistentDataType.INTEGER) == 0) {
            // initialize a new ID
            Random rand = new Random(System.currentTimeMillis());
            int id = rand.nextInt(1, 999999);

            // set ID
            data.set(ID_KEY, PersistentDataType.INTEGER, id);
            item.setItemMeta(meta);
        }

        p.openInventory(constructInventory(item));
        
    }

    // only allows you to move spell items while the inventory is open
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(INV_NAME)) return;

        Inventory top = e.getView().getTopInventory();
        ItemStack currentItem = e.getCurrentItem();
        
        e.setCancelled(true);

        NamespacedKey spellKey = new NamespacedKey(Main.getPlugin(), Ability.getKey());

        for (ItemStack item : top.getContents()) {
            if (item == null) continue;
            if (!item.getItemMeta().getPersistentDataContainer().has(spellKey, PersistentDataType.STRING)) {
                return;
            }
        }

        if (currentItem != null && !currentItem.getItemMeta().getPersistentDataContainer().has(spellKey, PersistentDataType.STRING)) return;

        e.setCancelled(false);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        // returns if not using Wand Inventory
        if (!e.getView().getTitle().equals(INV_NAME)) return;

        Inventory inv = e.getInventory();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

        // returns if the player wasn't holding an item
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // returns if the player wasn't holding a wand
        if (!data.has(ID_KEY, PersistentDataType.INTEGER)) return;

        // returns if the inventory is empty
        if (inv.isEmpty()) {
            data.remove(INV_KEY);
            item.setItemMeta(meta);
            return;
        }

        ArrayList<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(inv.getStorageContents()));
        
        data.set(INV_KEY, PersistentDataType.STRING, serialize(items));
        item.setItemMeta(meta);
    }

    private String serialize(ArrayList<ItemStack> items) {
        String encodedData = "";
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

            os.writeInt(items.size());

            for (ItemStack item : items) {
                os.writeObject(item);
            }

            os.flush();

            encodedData = Base64.getEncoder().encodeToString(io.toByteArray());

            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return encodedData;
    }

    private ArrayList<ItemStack> deserialize(String encoded_items) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        byte[] decoded_items = Base64.getDecoder().decode(encoded_items);

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(decoded_items);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            int numItems = is.readInt();

            for (int i = 0; i < numItems; i++) {
                items.add((ItemStack) is.readObject());
            }

            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return items;
    }
}
