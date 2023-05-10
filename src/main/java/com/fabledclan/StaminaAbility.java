package com.fabledclan;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// Another abstract class built on top of Ability class for spells that use Stamina
// Almost identical to SpellAbility but uses stamina instead of mana

public abstract class StaminaAbility extends Ability {
    private int requiredStaminaLevel;
    private int staminaCost;

    public StaminaAbility(Plugin plugin, Abilities abilities, String name, int rsl, int sc) {
        super(plugin, abilities, name);
        this.requiredStaminaLevel = rsl;
        this.staminaCost = sc;
    }

    // checks if the player has enough stamina to cast
    private Boolean playerHasStamina(Player player) {
        Abilities abilities = getAbilities();
        int currentStamina = abilities.getPlayerStamina(player);
        UUID playerID = player.getUniqueId();
        if (currentStamina < this.staminaCost) {
            if (alertMessageReady(playerID)) {
                player.sendMessage(ChatColor.BLUE + String.format("Need stamina level %d and %d stamina points for %s", getRequiredStaminaLevel(), getStaminaCost(), getName()));
            }
            return false;
        }
        return true;
    }

    // removes the stamina cost from the players stamina
    private void removePlayerStamina(Player player) {
        Abilities abilities = getAbilities();
        int currentStamina = abilities.getPlayerStamina(player);
        abilities.setPlayerStamina(player, currentStamina - getStaminaCost());
    }

    private int getRequiredStaminaLevel() {
        return requiredStaminaLevel;
    }

    private int getStaminaCost() {
        return this.staminaCost;
    }

    // checks if the player can cast the spell
    public Boolean failedCastChecks(Player player) {
        if (isOnCooldown(player) || !playerHasStamina(player)) return true;
        if (getStaminaCost() != 0) removePlayerStamina(player);
        addPlayerToCooldowns(player);
        return false;
    }
}
