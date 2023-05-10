package com.fabledclan;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class EntityDamageListenerPlayers implements Listener {
    private final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private final Main plugin;

    public EntityDamageListenerPlayers(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damagedPlayer = (Player) event.getEntity();
            UUID playerUUID = damagedPlayer.getUniqueId();

            BossBar bossBar;
            if (bossBars.containsKey(playerUUID)) {
                bossBar = bossBars.get(playerUUID);
            } else {
                bossBar = Bukkit.createBossBar(damagedPlayer.getName(), BarColor.PURPLE, BarStyle.SOLID);
                bossBars.put(playerUUID, bossBar);
            }

            double maxHealth = damagedPlayer.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
            double currentHealth = damagedPlayer.getHealth() - event.getFinalDamage();
            currentHealth = Math.max(0, currentHealth); // Ensure health doesn't go below 0

            bossBar.setProgress(currentHealth / maxHealth);
            bossBar.setVisible(true);
            bossBar.addPlayer((Player) event.getDamager());

            // Hide the boss bar after 20 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    bossBar.setVisible(false);
                    bossBar.removePlayer((Player) event.getDamager());
                }
            }.runTaskLater(plugin, 20 * 20);
        }
    }
}
