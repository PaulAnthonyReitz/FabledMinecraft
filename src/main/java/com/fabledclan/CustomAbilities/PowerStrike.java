package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerStrike extends StaminaAbility{
    public PowerStrike(String name, int requiredStaminaLevel, int staminaCost) {
        super(name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Apply the Power Strike effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 0, false, false));
    }
}
