package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerStrike extends StaminaAbility{
    public PowerStrike() {
        super("power_strike", 1, 20);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Apply the Power Strike effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 0, false, false));
    }
}
