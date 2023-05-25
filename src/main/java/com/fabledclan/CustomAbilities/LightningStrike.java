package com.fabledclan.CustomAbilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class LightningStrike extends SpellAbility {
    public LightningStrike(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;

        // Get the targeted location
        RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), 100);
        if (rayTraceResult == null) {
            player.sendMessage(ChatColor.RED + "No target found for Lightning Strike!");
            return;
        }

        // Strike the targeted location with lightning
        player.getWorld().strikeLightning(rayTraceResult.getHitBlock().getLocation());
    }
}
