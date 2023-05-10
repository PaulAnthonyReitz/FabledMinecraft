package com.fabledclan.abilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fabledclan.Abilities;
import com.fabledclan.StaminaAbility;

public class PowerStrike extends StaminaAbility{
    public PowerStrike(Plugin plugin, Abilities abilities, String name, int requiredStaminaLevel, int staminaCost) {
        super(plugin, abilities, name, requiredStaminaLevel, staminaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Apply the Power Strike effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 0, false, false));
    }
}
