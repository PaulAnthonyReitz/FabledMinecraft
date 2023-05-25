package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.fabledclan.Listeners.Abilities;

import org.bukkit.entity.Fireball;

public class FireballSpell extends SpellAbility{
    public FireballSpell() {
        super("fireball", 1, 25);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Perform the fireball ability
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setIsIncendiary(true);
        fireball.setYield(1.0F); // Adjust the explosion yield as needed (higher values create bigger explosions)
        
        // Store the player's magic level as metadata on the fireball
        fireball.setMetadata("magicLevel", new FixedMetadataValue(getPlugin(), Abilities.getPlayerMana(player)));
    } 
}
