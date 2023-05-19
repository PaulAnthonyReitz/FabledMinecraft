package com.fabledclan.CustomBlocks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.fabledclan.Main;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandCrafter extends CustomContainer implements Listener {
    private NamespacedKey key;
    private ItemStack arrowItem;
    private ItemStack nullItem;
    private final String INV_NAME = ChatColor.BOLD + "Wand Crafting";
    private final int[] SLOTS = { 0, 1, 2 };
    private ItemStack result = null;

    public WandCrafter() {
        super("wand_crafter", (ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "Wand Crafter"),
                Material.SMITHING_TABLE);
        new WandCrafterRecipes(); // inits the recipes for wand crafter
    }

    public Recipe recipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), getName()), getItem());
        recipe.shape(" S ", "SCS", " S ");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('C', Material.CRAFTING_TABLE);

        return recipe;
    }

    public Inventory makeInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, INV_NAME);
        arrowItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        nullItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta arrowMeta = arrowItem.getItemMeta();
        ItemMeta nullMeta = nullItem.getItemMeta();
        arrowMeta.setDisplayName(ChatColor.BOLD + "->");
        nullMeta.setDisplayName(ChatColor.BOLD + "NO VALID RECIPE");
        key = new NamespacedKey(Main.getPlugin(), getName());
        arrowMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, getName());
        nullMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, getName());
        arrowItem.setItemMeta(arrowMeta);
        nullItem.setItemMeta(nullMeta);
        inventory.setItem(4, nullItem);
        inventory.setItem(3, arrowItem);
        inventory.setMaxStackSize(1);
        return inventory;
    }

    public void placeEvent(BlockPlaceEvent event) {
        defaultPlace(event);
    }

    public void breakEvent(BlockBreakEvent event) {
        defaultBreak(event);
    }

    public void interactEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
        event.getPlayer().openInventory(makeInventory());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        // checks if we're viewing a WandCrafter inventory
        if (!event.getView().getTitle().equals(INV_NAME)) return;

        // Loops through the input slots and gives them back to the player if they exist
        for (int slot : SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item == null) continue;
            player.getInventory().addItem(item);
        }
    }

    // Handles drag events
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        // checks if we are in the WandCrafter inventory
        if (event.getInventory() == null) return;
        if (!event.getView().getTitle().equals(INV_NAME)) return;

        // Checks to see if a recipe is created
        Inventory inv = event.getInventory();
        checkRecipe(inv);
    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        // Checks if we are viewing a WandCrafter inventory
        if (!event.getView().getTitle().equals(INV_NAME)) return;

        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        // Makes sure we're clicking a valid inventory
        if (event.getClickedInventory() == null) return;

        // Checks if the item in the output slot exists
        if (inv.getItem(4) != null) {
            // If we try to shift click a similar item into the output slot i think?
            if (!inv.getItem(4).equals(event.getCurrentItem()) && event.getCurrentItem().isSimilar(inv.getItem(4))
                    && event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            // Ensures that you can't shift click items into the input (input can only have 1 item max stack)
            if (event.getClickedInventory().equals(event.getView().getBottomInventory()) && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                if (event.getCurrentItem() != null && event.getClick() != ClickType.DOUBLE_CLICK) {
                    // check if there is a slot open
                    if (inv.firstEmpty() == -1) {
                        event.setCancelled(true);
                        return;
                    }
                    ItemStack item = event.getCurrentItem().clone();
                    item.setAmount(1);
                    inv.setItem(inv.firstEmpty(), item);
                    event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                    event.setCancelled(true);
                    // checks the recipe again if you shift clicked the last item in
                    checkRecipe(inv);
                    return;
                }
            }
        }

        // If we're using the top inventory (not the player inventory)
        if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
            Boolean clearing = false;
            // If we click in the output slot and the output is an item
            if (event.getSlot() == 4 && inv.getItem(4).getType() != Material.BARRIER) {
                event.setCancelled(true);
                // if the cursor has an item in it, add that item to the player's inventory and
                // then add the output item to the cursor (ensures you don't put an item in the output)
                if (event.getCursor() != null) player.getInventory().addItem(event.getCursor());
                event.getView().setCursor(event.getCurrentItem());
                // if we crafted something set clear to true
                if (result != null) clearing = true;
            }
            // Checks if we clicked in input slot or if we're cleaing the inputs
            for (int i : SLOTS) {
                if (clearing) {
                    inv.clear(i);
                    continue;
                }
                // if we clicked an input slot then let the event go through
                if (event.getSlot() == i) {
                    event.setCancelled(false);
                    break;
                }
            }
        } else {
            // if we were in the player inventory let the event go through
            event.setCancelled(false);
        }
        // update to see if there's a valid recipe
        checkRecipe(inv);
    }


    // CHECK PERSISTENT DATA CONTAINER TO MAKE SURE YOU CAN'T CRAFT CUSTOM ITEMS
    private void checkRecipe(Inventory inv) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // checks if there are any empty slots in the input
                // if there are make sure the output is null and do nothing
                for (int i : SLOTS) {
                    if (inv.getItem(i) == null) {
                        result = null;
                        if (!inv.getItem(4).equals(nullItem)) inv.setItem(4, nullItem);
                        return;
                    }
                }
                // get a list of the inputs
                Material[] inputs = { inv.getItem(0).getType(), inv.getItem(1).getType(), inv.getItem(2).getType() };
                // loops through each recipe
                for (WandCrafterRecipes.Recipe recipe : WandCrafterRecipes.getRecipes()) {
                    Boolean valid = true;
                    // checks if the inputs directly copy the recipe
                    for (int i = 0; i < 3; i++) {
                        if (inputs[i] != recipe.getRecipe()[i]) {
                            valid = false;
                            break;
                        }
                    }
                    // if the recipe is valid it sets the result and the item in the inventory
                    if (valid) {
                        result = recipe.getResult();
                        inv.setItem(4, result);
                        return;
                    }
                }
                // if there were no valid recipes then it sets the last item to null
                inv.setItem(4, nullItem);
            }
        }.runTaskLater(Main.getPlugin(), 1L);
    }

}
