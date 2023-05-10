package com.fabledclan.abilities;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Fireball;

import com.fabledclan.Abilities;
import com.fabledclan.SpellAbility;

public class FireballSpell extends SpellAbility{
    public FireballSpell(Plugin plugin, Abilities abilities, String name, int requiredMagicLevel, int manaCost) {
        super(plugin, abilities, name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Perform the fireball ability
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(true);
        fireball.setYield(1.0F); // Adjust the explosion yield as needed (higher values create bigger explosions)
        
        // Store the player's magic level as metadata on the fireball
        fireball.setMetadata("magicLevel", new FixedMetadataValue(getPlugin(), getAbilities().getPlayerMana(player)));
    } 
}
