package com.fabledclan.CustomAbilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Feather extends SpellAbility {
    public Feather(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Apply the anti-gravity effect
        PotionEffect slowFallingEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 20, 1, false, false,
                true);
        player.addPotionEffect(slowFallingEffect);

        // Notify the player
        player.sendMessage(ChatColor.GREEN + "Feather ability activated! Gravity disabled for 20 seconds.");

        // Warn the player when the effect is about to wear off
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            player.sendMessage(ChatColor.YELLOW + "Feather ability is about to wear off!");
        }, 20 * 18); // Warn 2 seconds before it wears off
    }
}