package com.fabledclan;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class SetAttributesCommand implements CommandExecutor {

    private final Main plugin;
    public SetAttributesCommand(Main plugin) {
        this.plugin = plugin;
    }
    

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            sender.sendMessage("You don't have permission to use this command.");
            return false;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /setattributes <damage> <speed>");
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            sender.sendMessage("You must hold an item in your main hand to modify its attributes.");
            return false;
        }

        double damage;
        double speed;

        try {
            damage = Double.parseDouble(args[0]);
            speed = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid input. Please enter numbers for damage and speed.");
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        // Remove existing attribute modifiers
        itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
        itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);

        // Add new attribute modifiers
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", speed - 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));

        item.setItemMeta(itemMeta);
        sender.sendMessage("Item attributes updated!");

        return true;
    }
}
