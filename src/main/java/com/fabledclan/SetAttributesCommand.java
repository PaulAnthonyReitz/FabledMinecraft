import org.bukkit.Material;
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
        // ... (same as before)

        ItemStack item = player.getInventory().getItemInMainHand();


        // Remove existing attribute modifiers
        if (isWeapon(item.getType())) {
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
        } else if (isArmor(item.getType())) {
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        }

        // Add new attribute modifiers
        if (isWeapon(item.getType())) {
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", speed - 4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        } else if (isArmor(item.getType())) {
            itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "generic.armor", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        }

        item.setItemMeta(itemMeta);
        sender.sendMessage("Item attributes updated!");

        return true;
    }

    private boolean isWeapon(Material material) {
        return material == Material.WOODEN_SWORD || material == Material.STONE_SWORD || material == Material.IRON_SWORD || material == Material.GOLDEN_SWORD || material == Material.DIAMOND_SWORD || material == Material.NETHERITE_SWORD;
    }

    private boolean isArmor(Material material) {
        return material.name().endsWith("_HELMET") || material.name().endsWith("_CHESTPLATE") || material.name().endsWith("_LEGGINGS") || material.name().endsWith("_BOOTS");
    }
}
