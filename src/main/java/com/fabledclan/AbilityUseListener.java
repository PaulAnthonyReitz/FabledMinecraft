package com.fabledclan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
// import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.entity.ProjectileHitEvent;

public class AbilityUseListener implements Listener {

    private final Main plugin;
    private Map<UUID, Long> lastRightClickTime = new HashMap<>();
    // private final Map<UUID, BukkitTask> lightningStrikeTasks = new HashMap<>();

    List<String> spellList = Arrays.asList("dash", "dark_vortex", "dragon_breath", "feather", "fireball", "ice_shard",
            "lightning_strike", "magic_missile", "party", "plague_swarm", "power_strike", "summon_giant", "undead_army",
            "vader_choke", "wrangle", "yeet_boat");

    public AbilityUseListener(Main plugin, Abilities abilities) {
        this.plugin = plugin;
        Ability.setPlugin(plugin); // sets the static field for all abilities
        Ability.setAbilities(abilities);
    }

    public List<String> getSpellList() {
        return spellList;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Action eventAction = event.getAction();

        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return;
        }
        if (!(eventAction == Action.RIGHT_CLICK_AIR) && !(eventAction == Action.RIGHT_CLICK_BLOCK)) {
            return; // only fire on right click
        }

        // Simple debounce logic, not perfect
        long time = System.currentTimeMillis();
        long previous = lastRightClickTime.getOrDefault(playerID, 0L);
        if (time - previous <= 200) {
            return;
        }
        lastRightClickTime.put(playerID, time);

        if (event.getHand() == EquipmentSlot.HAND) {
            ItemMeta itemMeta = itemInHand.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            NamespacedKey abilityKey = new NamespacedKey(plugin, "embedded_ability");

            if (dataContainer.has(abilityKey, PersistentDataType.STRING)) {
                String ability = dataContainer.get(abilityKey, PersistentDataType.STRING);
                for (Ability a : AbilityRegistry.getAbilities()) {
                    if (!a.getName().equals(ability))
                        continue;
                    a.cast(player);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("IceShard")) {
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity hitEntity = (LivingEntity) event.getHitEntity();
                hitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 0, false, true));
            }
        }
    }

    // HERE LIES LIGHTNING STRIKE ABILITY TO BE REFACTORED INTO A CLASS

    // private void performLightningStrikeAbility(Player player) {
    //     int requiredMagicLevel = 1;
    //     int manaCost = 50;
    
    //     Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
    //     UUID playerId = player.getUniqueId();
    
    //     long currentTime = System.currentTimeMillis();
    //     long lastActivationTime = lightningStrikeCooldowns.getOrDefault(playerId,
    //     0L);
    
    //     if (currentTime - lastActivationTime < abilityCooldownMillis) {
    //     player.sendMessage(ChatColor.RED + "Lightning Strike ability is on cooldown!
    //     Please wait.");
    //     return;
    //     }
    
    //     if (currentMana != null && currentMana >= manaCost) {
    //     // Reduce the player's magic energy by the required amount
    //     int newMana = currentMana - manaCost;
    //     abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
    //     // Strike lightning at the targeted location
    //     Location targetLocation = player.getTargetBlock(null, 100).getLocation();
    //     player.getWorld().strikeLightning(targetLocation);
    
    //     // Notify the player
    //     player.sendMessage(ChatColor.GREEN + "Lightning Strike ability activated!");
    
    //     // Update the last activation time for this player
    //     lightningStrikeCooldowns.put(playerId, currentTime);
    //     } else {
    //     player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel
    //     + " and " + manaCost + " mana points for Lightning Strike!");
    //     }
    //     }
    
    //     public class ChargeDurationTask extends BukkitRunnable {
    //     private final Player player;
    //     private Location lastLocation;
    //     private int duration;
    
    //     public ChargeDurationTask(Player player, Plugin plugin) {
    //     this.player = player;
    //     this.lastLocation = player.getLocation();
    //     this.duration = 0;
    //     }
    
    //     public int getDuration() {
    //     return duration;
    //     }
    
    //     @Override
    //     public void run() {
    //     if (player.isDead() || !player.isOnline()) {
    //     cancel();
    //     lightningStrikeTasks.remove(player.getUniqueId());
    //     return;
    //     }
    
    //     if (player.getLocation().distanceSquared(lastLocation) < 0.01) {
    //     duration += 3;
    //     } else {
    //     lastLocation = player.getLocation();
    //     duration = 0;
    //     }
    
    //     // Every 3 seconds, increase the lightning bolt radius by 1 block
    //     if (duration % 60 == 0) {
    //     player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
    //     }
    //     }
    //     }

}
