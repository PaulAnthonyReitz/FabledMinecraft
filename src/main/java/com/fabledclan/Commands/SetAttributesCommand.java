package com.fabledclan.Commands;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class SetAttributesCommand extends CommandClass {
    public SetAttributesCommand() {
        super("setattributes");
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
            sender.sendMessage("Usage: /setattributes <damage/armor> <speed>");
            return false;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage("You must hold an item in your main hand to modify its attributes.");
            return false;
        }

        try {
            double value = Double.parseDouble(args[0]);
            double speed = Double.parseDouble(args[1]);

            ItemMeta itemMeta = item.getItemMeta();

            // Remove existing attribute modifiers
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);

            Material itemType = item.getType();
            boolean isWeapon = itemType.name().endsWith("_SWORD") || itemType.name().endsWith("_AXE");
            boolean isArmor = itemType.name().endsWith("_HELMET") || itemType.name().endsWith("_CHESTPLATE") || itemType.name().endsWith("_LEGGINGS") || itemType.name().endsWith("_BOOTS");

            if (isWeapon) {
                // Add new attribute modifiers for weapons
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", value, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", speed - 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            } else if (isArmor) {
                // Add new attribute modifiers for armor
                itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", value, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.valueOf(itemType.name().replace("_", ""))));
            } else {
                sender.sendMessage("The item in your main hand is neither a weapon nor armor.");
                return false;
            }

            item.setItemMeta(itemMeta);
            sender.sendMessage("Item attributes updated!");
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid input. Please enter numbers for damage/armor and speed.");
            return false;
        }

        return true;
    }
}
