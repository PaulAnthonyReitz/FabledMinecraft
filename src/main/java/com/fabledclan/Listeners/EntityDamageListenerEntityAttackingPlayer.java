package com.fabledclan.Listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;
import com.fabledclan.Enemy.EnemyData;

public class EntityDamageListenerEntityAttackingPlayer implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Creature)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Creature creature = (Creature) event.getDamager();

        // Get the mob level
        PersistentDataContainer dataContainer = creature.getPersistentDataContainer();
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);

        // Get the mob's attack value
        EnemyData enemyData = Main.getCachedEnemyData(creature.getType());
        int mobAttack = enemyData != null ? enemyData.baseAttack + enemyLevel * enemyData.attackScale : 1;

        // Get the player's defense value
        int playerDefense = Main.getPlayerStatsCache().get(player.getUniqueId()).getDefense();

        // Calculate the final damage after accounting for mob attack and player defense
        int finalDamage = Math.max(1, mobAttack - playerDefense);

        // Apply the final damage to the player
        event.setDamage(finalDamage);
    }
}
