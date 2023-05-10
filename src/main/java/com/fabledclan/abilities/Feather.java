package com.fabledclan.abilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fabledclan.Abilities;
import com.fabledclan.SpellAbility;

public class Feather extends SpellAbility {
    public Feather(Plugin plugin, Abilities abilities, String name, int requiredMagicLevel, int manaCost) {
        super(plugin, abilities, name, requiredMagicLevel, manaCost);
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