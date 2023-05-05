package com.fabledclan;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UnenchantCommand implements CommandExecutor {

    private final Main plugin;

    public UnenchantCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null) {
            player.sendMessage("You must hold an item in your main hand to unenchant it.");
            return true;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey abilityKey = new NamespacedKey(plugin, "embedded_ability");

        if (dataContainer.has(abilityKey, PersistentDataType.STRING)) {
            String ability = dataContainer.get(abilityKey, PersistentDataType.STRING);
            dataContainer.remove(abilityKey);

            // Remove the enchantment line from the item's lore
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                lore.removeIf(line -> line.contains(ChatColor.GRAY + "Enchant: " + ability));
                itemMeta.setLore(lore);
            }

            itemInHand.setItemMeta(itemMeta);
            player.sendMessage("Removed the ability from your item.");
        } else {
            player.sendMessage("This item does not have an ability.");
        }

        return true;
    }
}
