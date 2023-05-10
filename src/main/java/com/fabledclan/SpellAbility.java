package com.fabledclan;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// This is another abstract class built on top of the Ability class for mana abilities.
// This class adds a required magic level and mana cost, as well as some extra methods.

public abstract class SpellAbility extends Ability {
    private int requiredMagicLevel;
    private int manaCost;

    public SpellAbility(Plugin plugin, Abilities abilities, String name, int rml, int mc) {
        super(plugin, abilities, name);
        this.requiredMagicLevel = rml;
        this.manaCost = mc;
    }

    // checks if the player has enough mana to cast
    private Boolean playerHasMana(Player player) {
        Abilities abilities = getAbilities();
        int currentMana = abilities.getPlayerMana(player);
        UUID playerID = player.getUniqueId();
        if (currentMana < manaCost) {
            if (alertMessageReady(playerID)) {
                player.sendMessage(ChatColor.BLUE + String.format("Need magic level %d and %d mana points for %s",
                        getRequriedMagicLevel(), getManaCost(), getName()));
            }
            return false;
        }
        return true;
    }

    // removes the mana cost from the players mana
    private void removePlayerMana(Player player) {
        Abilities abilities = getAbilities();
        int currentMana = abilities.getPlayerMana(player);
        abilities.setPlayerMana(player, currentMana - getManaCost());
    }

    private int getRequriedMagicLevel() {
        return requiredMagicLevel;
    }

    private int getManaCost() {
        return manaCost;
    }

    // checks if the player can cast the spell
    public Boolean failedCastChecks(Player player) {
        if (isOnCooldown(player) || !playerHasMana(player)) return true;
        if (getManaCost() != 0) removePlayerMana(player);
        addPlayerToCooldowns(player);
        return false;
    }
    
}
