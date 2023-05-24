package com.fabledclan.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;

public class EnchantCommand extends CommandClass {

    public EnchantCommand() {
        super("enchant");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Please provide an ability name.");
            return false;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null) {
            player.sendMessage("You must hold an item in your main hand to enchant it.");
            return true;
        }

        String ability = args[0];
        ItemMeta itemMeta = itemInHand.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey abilityKey = new NamespacedKey(Main.getPlugin(), "embedded_ability");

        // Add enchantment line to the item's lore
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            // Remove any existing enchantment line
            lore.removeIf(line -> line.startsWith(ChatColor.GRAY + "Enchant: "));
        }
        lore.add(ChatColor.GRAY + "Enchant: " + ability);
        itemMeta.setLore(lore);

        dataContainer.set(abilityKey, PersistentDataType.STRING, ability);
        itemInHand.setItemMeta(itemMeta);

        player.sendMessage("Enchanted your item with the " + ability + " ability.");
        return true;
    }
}
