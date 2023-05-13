package com.fabledclan.CustomAbilities;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;

public class IceShard extends SpellAbility{
    public IceShard(String name, int requiredMagicLevel, int manaCost) {
        super(name, requiredMagicLevel, manaCost);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        // Launch the Ice Shard
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setMetadata("IceShard", new FixedMetadataValue(getPlugin(), true));
    }
}
    
