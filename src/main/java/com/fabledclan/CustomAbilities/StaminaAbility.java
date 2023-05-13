package com.fabledclan.CustomAbilities;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.fabledclan.Listeners.Abilities;

// Another abstract class built on top of Ability class for spells that use Stamina
// Almost identical to SpellAbility but uses stamina instead of mana

public abstract class StaminaAbility extends Ability {
    private int requiredStaminaLevel;
    private int staminaCost;

    public StaminaAbility(String name, int rsl, int sc) {
        super(name);
        this.requiredStaminaLevel = rsl;
        this.staminaCost = sc;
    }

    // checks if the player has enough stamina to cast
    private Boolean playerHasStamina(Player player) {
        int currentStamina = Abilities.getPlayerStamina(player);
        UUID playerID = player.getUniqueId();
        if (currentStamina < staminaCost) {
            if (alertMessageReady(playerID)) {
                player.sendMessage(ChatColor.BLUE + String.format("Need stamina level %d and %d stamina points for %s", getRequiredStaminaLevel(), getStaminaCost(), getName()));
            }
            return false;
        }
        return true;
    }

    // removes the stamina cost from the players stamina
    private void removePlayerStamina(Player player) {
        int currentStamina = Abilities.getPlayerStamina(player);
        Abilities.setPlayerStamina(player, currentStamina - getStaminaCost());
    }

    private int getRequiredStaminaLevel() {
        return requiredStaminaLevel;
    }

    private int getStaminaCost() {
        return staminaCost;
    }

    // checks if the player can cast the spell
    public Boolean failedCastChecks(Player player) {
        if (isOnCooldown(player) || !playerHasStamina(player)) return true;
        if (getStaminaCost() != 0) removePlayerStamina(player);
        addPlayerToCooldowns(player);
        return false;
    }
}
