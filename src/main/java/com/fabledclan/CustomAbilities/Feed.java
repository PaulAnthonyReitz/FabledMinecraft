package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;

public class Feed extends StaminaAbility { 
    public Feed() {
        super("feed", 1, 50);
    }

    public void cast(Player player) {
        int foodLevel = player.getFoodLevel();
        if (foodLevel == 20) return;
        if (failedCastChecks(player)) return;
        player.setFoodLevel(foodLevel + 8);
    }
}