package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;

public class Heal extends SpellAbility {
    public Heal() {
        super("heal", 1, 50);
    }

    public void cast(Player player) {
        double healthScale = player.getHealthScale();
        double health = player.getHealth();
        if (health >= (20 * healthScale)) return;
        if (failedCastChecks(player)) return;
        player.setHealth(health + 8);
    }
    
}
