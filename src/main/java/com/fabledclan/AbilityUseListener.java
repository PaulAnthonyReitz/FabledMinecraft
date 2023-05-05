package com.fabledclan;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


import com.fabledclan.DatabaseManager.PlayerStats;

public class AbilityUseListener implements Listener {

    private final Main plugin;
    private final Abilities abilities;
    private final DatabaseManager databaseManager;

    public AbilityUseListener(Main plugin, Abilities abilities, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.abilities = abilities;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey abilityKey = new NamespacedKey(plugin, "embedded_ability");

        if (dataContainer.has(abilityKey, PersistentDataType.STRING)) {
            String ability = dataContainer.get(abilityKey, PersistentDataType.STRING);

            if (ability != null) {
                // Use the ability
                switch (ability) {
                    case "dash":
                        // Perform the dash ability
                        performDashAbility(event.getPlayer());
                        break;
                    case "fireball":
                        // Perform the fireball ability
                        performFireballAbility(event.getPlayer());
                        break;
                    // Add more cases for other abilities as needed
                    default:
                        event.getPlayer().sendMessage("Unknown ability: " + ability);
                        break;
                }
            }
        }
    }

    private void performDashAbility(Player player) {
        UUID playerId = player.getUniqueId();
        int requiredStaminaLevel = 1;
        int staminaCost = 25;
    
        Integer currentStamina = abilities.getPlayerStamina().get(playerId);
    
        if (currentStamina != null && currentStamina >= staminaCost) {
            // Reduce the player's stamina by the required amount
            int newStamina = currentStamina - staminaCost;
            abilities.getPlayerStamina().put(playerId, newStamina);
    
            // Perform the dash ability
            Vector dashDirection = player.getLocation().getDirection().normalize().multiply(2); // Adjust the multiplier for desired dash strength
            player.setVelocity(player.getVelocity().add(dashDirection));
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Stamina Level " + requiredStaminaLevel + " and " + staminaCost + " stamina points for dash!");
        }
    }
    
    private void performFireballAbility(Player player) {
        UUID playerId = player.getUniqueId();
        int requiredMagicLevel = 1;
        int manaCost = 25;
    
        Integer currentMana = abilities.getPlayerMana().get(playerId);
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(playerId, newMana);
    
            // Perform the fireball ability
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setIsIncendiary(true);
            fireball.setYield(1.0F); // Adjust the explosion yield as needed (higher values create bigger explosions)
    
            // Store the player's magic level as metadata on the fireball
            fireball.setMetadata("magicLevel", new FixedMetadataValue(plugin, currentMana));
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for fireball!");
        }
    }
    
    
    
    
}
