package com.fabledclan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.entity.ProjectileHitEvent;


public class AbilityUseListener implements Listener {

    private final Main plugin;
    private final Abilities abilities;
    private final Map<UUID, Long> lastDashTime = new HashMap<>();
    private final Map<UUID, Long> lastFireballTime = new HashMap<>();
    private final Map<UUID, Long> lastGiantTime = new HashMap<>();
    private HashMap<UUID, Long> featherAbilityCooldowns = new HashMap<>();
    private HashMap<UUID, Long> lightningStrikeCooldowns = new HashMap<>();
    private final HashMap<UUID, Long> undeadArmyCooldowns = new HashMap<>();
    private final HashMap<UUID, Long> vaderChokeCooldowns = new HashMap<>();
    private final Map<UUID, Long> yeetBoatCooldowns = new HashMap<>();
    private final Map<UUID, Long> wrangleCooldowns = new HashMap<>();
    private final long abilityCooldownMillis = 1000; // Adjust this to change the cooldown time (in milliseconds)
    private final Map<UUID, BukkitTask> lightningStrikeTasks = new HashMap<>();
    List<String> spellList = Arrays.asList("dash", "dark_vortex", "dragon_breath", "feather", "fireball", "ice_shard", "lightning_strike", "magic_missile", "party", "plague_swarm", "power_strike", "summon_giant", "undead_army", "vader_choke", "wrangle", "yeet_boat");

    public AbilityUseListener(Main plugin, Abilities abilities) {
        this.plugin = plugin;
        this.abilities = abilities;
    }
    public List<String> getSpellList() {
        return spellList;
    }
    

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return;
        }
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)){
            return; //only fire on right click
        }
        

        ItemMeta itemMeta = itemInHand.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey abilityKey = new NamespacedKey(plugin, "embedded_ability");

        if (dataContainer.has(abilityKey, PersistentDataType.STRING)) {
            String ability = dataContainer.get(abilityKey, PersistentDataType.STRING);

            if (ability != null) {
                // Use the ability
                switch (ability) {
                    case "dash":
                        // Perform the dash ability
                        performDashAbility(event.getPlayer());
                        break;
                    case "fireball":
                        // Perform the fireball ability
                        performFireballAbility(event.getPlayer());
                        break;
                    case "power_strike":
                        performPowerStrikeAbility(event.getPlayer());
                        break;
                    case "ice_shard":
                        performIceShardAbility(event.getPlayer());
                        break;
                    case "dark_vortex":
                        performDarkVortexAbility(event.getPlayer());
                        break;
                    case "plague_swarm":
                        performPlagueSwarmAbility(event.getPlayer());
                        break;
                    case "lightning_strike":
                        performLightningStrikeAbility(event.getPlayer());
                        break;
                    case "undead_army":
                        // Perform the Undead Army ability
                        performUndeadArmyAbility(event.getPlayer());
                        break;
                    // Add more cases for other abilities as needed
                    case "feather":
                    // Perform the Feather ability
                    performFeatherAbility(event.getPlayer());
                        break;
                    case "magic_missile":
                        performMagicMissileAbility(event.getPlayer());
                        break;
                    case "wrangle":
                        performWrangleAbility(event.getPlayer());
                    break;
                    case "yeet_boat":
                        performYeetBoatAbility(event.getPlayer());
                        break;
                    case "party":
                        performPartySpellAbility(event.getPlayer());
                        break;
                    case "summon_giant":
                        // Perform the Giant spell ability
                        performGiantSpellAbility(event.getPlayer());
                        break;
                    case "vader_choke":
                        // Perform the Giant spell ability
                        performVaderChokeAbility(event.getPlayer());
                        break;
                    case "dragon_breath":
                        // Perform the Giant spell ability
                        performDragonBreathAbility(event.getPlayer());
                        break;
                    default:
                        event.getPlayer().sendMessage("Unknown ability: " + ability);
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

    private void performDragonBreathAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 50;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Get the direction the player is facing
            Vector direction = player.getLocation().getDirection();
    
            // Calculate the starting point of the dragon breath
            Location startPoint = player.getEyeLocation().add(direction.multiply(2));
    
            // Spawn a dragon fireball at the starting point
            DragonFireball fireball = (DragonFireball) player.getWorld().spawnEntity(startPoint, EntityType.DRAGON_FIREBALL);
    
            // Set the shooter to the player to prevent self-damage
            fireball.setShooter(player);
    
            // Set the direction and velocity of the dragon fireball
            fireball.setDirection(direction);
            fireball.setVelocity(direction.multiply(2));
    
            // Set the explosion power of the dragon fireball
            fireball.setYield(0);
    
            // Set the fire duration of the dragon fireball
            fireball.setFireTicks(40);
    
            // Register an event listener to handle the dragon fireball collision
            plugin.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onProjectileHit(ProjectileHitEvent event) {
                    if (event.getEntity().equals(fireball)) {
                        // Check if the dragon fireball hit an entity
                        if (event.getHitEntity() != null) {
                            // Set the hit entity on fire
                            event.getHitEntity().setFireTicks(40);
                        }
    
                        // Remove the dragon fireball
                        fireball.remove();
                    }
                }
            }, plugin);
    
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Dragon Breath!");
        }
    }
    


    private void performGiantSpellAbility(Player player) {
        UUID playerId = player.getUniqueId();
        int requiredMagicLevel = 1;
        int manaCost = 50;
    
        Long lastTime = lastGiantTime.get(playerId);
        if (lastTime != null && System.currentTimeMillis() - lastTime < abilityCooldownMillis) {
            // The ability is on cooldown
            player.sendMessage(ChatColor.RED + "Giant spell is on cooldown. Please wait before using it again.");
            return;
        }
    
        Integer currentMana = abilities.getPlayerMana().get(playerId);
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(playerId, newMana);
    
            // Get the targeted location
            Location targetLocation = player.getTargetBlockExact(100).getLocation();
    
            // Summon the giant at the target location
            Giant giant = (Giant) targetLocation.getWorld().spawnEntity(targetLocation, EntityType.GIANT);
    
            // Set the giant's size to a larger scale
    
            // Increase the giant's movement speed
            AttributeInstance giantSpeed = giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (giantSpeed != null) {
                giantSpeed.setBaseValue(giantSpeed.getBaseValue());
            }
    
            // Set the giant's target to the player
            giant.setTarget(player);
    
            // Schedule the giant to be removed after 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!giant.isDead()) {
                    giant.remove();
                }
            }, 30 * 20);
    
            // Update the last used time
            lastGiantTime.put(playerId, System.currentTimeMillis());
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Giant spell!");
        }
    }
    
    private void performPartySpellAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 100;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Play party music at the player's location
            player.getWorld().playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 10.0f, 1.0f);
    
            // Spawn RGB sheep and set their color
            // ...
    
            // Launch fireworks around the player continuously for 30 seconds
            BukkitTask fireworkTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                // ...
            }, 0, 2); // 2 ticks = 0.1 seconds
    
            // Schedule the removal of all entities and stopping the fireworks after 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity instanceof Sheep || entity instanceof Firework) {
                        entity.remove();
                    }
                }
                fireworkTask.cancel();
            }, 20 * 30); // 20 ticks per second * 30 seconds
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Party Spell!");
        }
    }
    
    private void performMagicMissileAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 25;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Get the targeted entity
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 100);
            if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
                player.sendMessage(ChatColor.RED + "No target found for Magic Missile!");
                return;
            }
            LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
    
            // Calculate the direction from player to target
            Vector direction = target.getEyeLocation().subtract(player.getEyeLocation()).toVector().normalize();
    
            // Create and launch fireworks along the direction
            for (int i = 0; i < 20; i++) {
                Location spawnLocation = player.getEyeLocation().add(direction.multiply(i));
                Firework firework = (Firework) player.getWorld().spawnEntity(spawnLocation, EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(Type.BURST).trail(true).build());
                fireworkMeta.setPower(1);
                firework.setFireworkMeta(fireworkMeta);
    
                // Schedule the firework to explode after 1 second
                Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 20 * 1);
            }
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Magic Missile!");
        }
    }
    

    private void performYeetBoatAbility(Player player) {
        int requiredStaminaLevel = 1;
        int staminaCost = 30;
    
        Integer currentStamina = abilities.getPlayerStamina().get(player.getUniqueId());
    
        if (yeetBoatCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - yeetBoatCooldowns.get(player.getUniqueId()) < abilityCooldownMillis) {
            player.sendMessage(ChatColor.RED + "Yeet Boat is on cooldown!");
            return;
        }
    
        if (currentStamina != null && currentStamina >= staminaCost) {
            // Reduce the player's stamina by the required amount
            int newStamina = currentStamina - staminaCost;
            abilities.getPlayerStamina().put(player.getUniqueId(), newStamina);
    
            // Spawn a boat at the player's location
            Location boatLocation = player.getLocation();
            Boat boat = player.getWorld().spawn(boatLocation, Boat.class);
    
            // Add the player to the boat
            boat.addPassenger(player);
    
            // Set the boat's velocity for height and forward momentum
            Vector velocity = player.getEyeLocation().getDirection();
            velocity.setY(4.5); // Increase the Y-component for greater height
            velocity.multiply(3); // Multiply the vector for increased forward momentum
            boat.setVelocity(velocity);
    
            // Prevent fall damage for the player
            player.setFallDistance(0);
    
            // Schedule the removal of the boat after 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!boat.isDead()) {
                    boat.remove();
                }
            }, 20 * 30);
    
            // Set the cooldown
            yeetBoatCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    
            // Check for 360-degree spin
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                float rotationDiff = Math.abs(player.getLocation().getYaw() - boatLocation.getYaw());
                if (rotationDiff >= 350 && rotationDiff <= 370) {
                    int score = new Random().nextInt(100) + 1;
                    player.sendMessage(ChatColor.GREEN + "Radical! Your 360 spin scored " + score + " points!");
                }
            }, 20 * 1); // Check after 1 second
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Stamina Level " + requiredStaminaLevel + " and " + staminaCost + " stamina points for Yeet Boat!");
        }
    }
    
    private void performWrangleAbility(Player player) {
        int requiredStaminaLevel = 1;
        int staminaCost = 50;
    
        Integer currentStamina = abilities.getPlayerStamina().get(player.getUniqueId());
    
        if (wrangleCooldowns.containsKey(player.getUniqueId()) && System.currentTimeMillis() - wrangleCooldowns.get(player.getUniqueId()) < 1000) {
            player.sendMessage(ChatColor.RED + "Wrangle is on cooldown!");
            return;
        }
    
        if (currentStamina != null && currentStamina >= staminaCost) {
            // Reduce the player's stamina by the required amount
            int newStamina = currentStamina - staminaCost;
            abilities.getPlayerStamina().put(player.getUniqueId(), newStamina);
    
            // Get the targeted entity
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 100);
            if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity) || rayTraceResult.getHitEntity().equals(player)) {
                player.sendMessage(ChatColor.RED + "Wrangle only works on entities other than yourself!");
                return;
            }
            LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
    
            // Make the player mount the target entity
            // ...
    
            // Set the cooldown
            wrangleCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Stamina Level " + requiredStaminaLevel + " and " + staminaCost + " stamina points for Wrangle!");
        }
    }
    
    
    private void performDashAbility(Player player) {
        UUID playerId = player.getUniqueId();
        int requiredStaminaLevel = 1;
        int staminaCost = 25;
    
        Long lastTime = lastDashTime.get(playerId);
        if (lastTime != null && System.currentTimeMillis() - lastTime < abilityCooldownMillis) {
            // The ability is on cooldown
            player.sendMessage(ChatColor.RED + "Dash is on cooldown. Please wait before using it again.");
            return;
        }
    
        Integer currentStamina = abilities.getPlayerStamina().get(playerId);
    
        if (currentStamina != null && currentStamina >= staminaCost) {
            // Reduce the player's stamina by the required amount
            int newStamina = currentStamina - staminaCost;
            abilities.getPlayerStamina().put(playerId, newStamina);
    
            // Perform the dash ability
            Vector dashDirection = player.getLocation().getDirection().normalize().multiply(2); // Adjust the multiplier for desired dash strength
            player.setVelocity(player.getVelocity().add(dashDirection));
    
            // Update the last used time
            lastDashTime.put(playerId, System.currentTimeMillis());
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Stamina Level " + requiredStaminaLevel + " and " + staminaCost + " stamina points for dash!");
        }
    }

        private void performIceShardAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 20;

        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());

        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);

            // Launch the Ice Shard
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setMetadata("IceShard", new FixedMetadataValue(plugin, true));
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Ice Shard!");
        }
    }

        private void performPowerStrikeAbility(Player player) {
        int requiredStaminaLevel = 1;
        int staminaCost = 20;

        Integer currentStamina = abilities.getPlayerStamina().get(player.getUniqueId());

        if (currentStamina != null && currentStamina >= staminaCost) {
            // Reduce the player's stamina by the required amount
            int newStamina = currentStamina - staminaCost;
            abilities.getPlayerStamina().put(player.getUniqueId(), newStamina);

            // Apply the Power Strike effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 20, 0, false, false));
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Stamina Level " + requiredStaminaLevel + " and " + staminaCost + " stamina points for Power Strike!");
        }
    }

    
    
    private void performFireballAbility(Player player) {
        UUID playerId = player.getUniqueId();
        int requiredMagicLevel = 1;
        int manaCost = 25;
    
        Long lastTime = lastFireballTime.get(playerId);
        if (lastTime != null && System.currentTimeMillis() - lastTime < abilityCooldownMillis) {
            // The ability is on cooldown
            player.sendMessage(ChatColor.RED + "Fireball is on cooldown. Please wait before using it again.");
            return;
        }
    
        Integer currentMana = abilities.getPlayerMana().get(playerId);
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(playerId, newMana);
    
            // Perform the fireball ability
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setIsIncendiary(true);
            fireball.setYield(1.0F); // Adjust the explosion yield as needed (higher values create bigger explosions)
    
            // Store the player's magic level as metadata on the fireball
            fireball.setMetadata("magicLevel", new FixedMetadataValue(plugin, currentMana));
    
            // Update the last used time
            lastFireballTime.put(playerId, System.currentTimeMillis());
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for fireball!");
        }
    }

    private void performDarkVortexAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 75;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
    
        if (currentMana != null && currentMana >= manaCost) {
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            Location vortexLocation = player.getTargetBlock(null, 30).getLocation().add(0, 1, 0);
            BoundingBox vortexArea = BoundingBox.of(vortexLocation, 10, 10, 10);
    
            new BukkitRunnable() {
                int duration = 20 * 15; // 15 seconds
    
                @Override
                public void run() {
                    if (duration <= 0) {
                        cancel();
                    }
    
                    // Spawn particle effects to create a swirly visual
                    for (double i = 0; i < 2 * Math.PI; i += Math.PI / 16) {
                        double x = Math.cos(i) * 6;
                        double z = Math.sin(i) * 6;
                        vortexLocation.getWorld().spawnParticle(Particle.SPELL_MOB, vortexLocation.clone().add(x, 0, z), 1, 0, 0, 0, 0);
                    }
    
                    for (Entity entity : vortexLocation.getWorld().getNearbyEntities(vortexLocation, 10, 10, 10)) {
                        if (vortexArea.contains(entity.getLocation().toVector())) {
                            Vector direction = vortexLocation.toVector().subtract(entity.getLocation().toVector()).normalize();
                            entity.setVelocity(entity.getVelocity().add(direction.multiply(0.5)));
                            if (entity instanceof LivingEntity) {
                                ((LivingEntity) entity).damage(1, player);
                            }
                        }
                    }
    
                    duration--;
                }
            }.runTaskTimer(plugin, 0, 1);
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Dark Vortex!");
        }
    }
    

    private void performPlagueSwarmAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 60;

        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());

        if (currentMana != null && currentMana >= manaCost) {
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);

            for (int i = 0; i < 10; i++) {
                Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.BAT);
                bat.setMetadata("PlagueSwarm", new FixedMetadataValue(plugin, true));

                new BukkitRunnable() {
                    int duration = 20 * 5; // 5 seconds

                    @Override
                    public void run() {
                        if (duration <= 0 || bat.isDead()) {
                            bat.remove();
                            cancel();
                        }

                        Entity target = null;
                        double minDistance = Double.MAX_VALUE;

                        for (Entity entity : bat.getNearbyEntities(10, 10, 10)) {
                            if (entity instanceof LivingEntity && entity != player && !(entity instanceof Bat)) {
                                double distance = entity.getLocation().distanceSquared(bat.getLocation());
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    target = entity;
                                }
                            }
                        }

                        if (target != null) {
                            Vector direction = target.getLocation().toVector().subtract(bat.getLocation().toVector()).normalize();
                            bat.setVelocity(direction.multiply(0.5));

                            if (target.getLocation().distance(bat.getLocation()) < 2) {
                                if (target instanceof LivingEntity) {
                                    LivingEntity livingEntity = (LivingEntity) target;
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1));
                                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
                                }
                            }
                        }

                        duration--;
                    }
                }.runTaskTimer(plugin, 0, 1);
            }
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Plague Swarm!");
        }
    }

    private void performVaderChokeAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 50;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
        UUID playerId = player.getUniqueId();
    
        long currentTime = System.currentTimeMillis();
        long lastActivationTime = vaderChokeCooldowns.getOrDefault(playerId, 0L);
    
        if (currentTime - lastActivationTime < abilityCooldownMillis) {
            player.sendMessage(ChatColor.RED + "Vader Choke ability is on cooldown! Please wait.");
            return;
        }
    
        if (currentMana != null && currentMana >= manaCost) {
            // Get the targeted entity
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 10);
            if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity) || rayTraceResult.getHitEntity().getUniqueId().equals(playerId)) {
                player.sendMessage(ChatColor.RED + "No valid target found for Vader Choke!");
                return;
            }
            LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
    
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Apply the choke effect
            Location targetLocation = target.getLocation();
            targetLocation.setY(targetLocation.getY() + 2); // Lift the target off the ground by 2 blocks
            target.teleport(targetLocation);
    
            // Prevent the target from moving for 5 seconds
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
    
            // Schedule the release of the target after 5 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (target.isValid()) {
                    target.teleport(targetLocation.subtract(0, 2, 0)); // Lower the target back to the ground
                    target.removePotionEffect(PotionEffectType.SLOW); // Remove the immobilization effect
                }
            }, 5 * 20);
    
            // Update the last activation time for this player
            vaderChokeCooldowns.put(playerId, currentTime);
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Vader Choke!");
        }
    }
    
    

    private void performLightningStrikeAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 50;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
        UUID playerId = player.getUniqueId();
    
        long currentTime = System.currentTimeMillis();
        long lastActivationTime = lightningStrikeCooldowns.getOrDefault(playerId, 0L);
    
        if (currentTime - lastActivationTime < abilityCooldownMillis) {
            player.sendMessage(ChatColor.RED + "Lightning Strike ability is on cooldown! Please wait.");
            return;
        }
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Strike lightning at the targeted location
            Location targetLocation = player.getTargetBlock(null, 100).getLocation();
            player.getWorld().strikeLightning(targetLocation);
    
            // Notify the player
            player.sendMessage(ChatColor.GREEN + "Lightning Strike ability activated!");
    
            // Update the last activation time for this player
            lightningStrikeCooldowns.put(playerId, currentTime);
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Lightning Strike!");
        }
    }
    
    
    public class ChargeDurationTask extends BukkitRunnable {
        private final Player player;
        private Location lastLocation;
        private int duration;
    
        public ChargeDurationTask(Player player, Plugin plugin) {
            this.player = player;
            this.lastLocation = player.getLocation();
            this.duration = 0;
        }
    
        public int getDuration() {
            return duration;
        }
    
        @Override
        public void run() {
            if (player.isDead() || !player.isOnline()) {
                cancel();
                lightningStrikeTasks.remove(player.getUniqueId());
                return;
            }
    
            if (player.getLocation().distanceSquared(lastLocation) < 0.01) {
                duration += 3;
            } else {
                lastLocation = player.getLocation();
                duration = 0;
            }
    
            // Every 3 seconds, increase the lightning bolt radius by 1 block
            if (duration % 60 == 0) {
                player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
            }
        }
    }

    private void performUndeadArmyAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 75;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
        UUID playerId = player.getUniqueId();
    
        long currentTime = System.currentTimeMillis();
        long lastActivationTime = undeadArmyCooldowns.getOrDefault(playerId, 0L);
    
        if (currentTime - lastActivationTime < abilityCooldownMillis) {
            player.sendMessage(ChatColor.RED + "Undead Army ability is on cooldown! Please wait.");
            return;
        }
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Get the targeted entity
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 100);
            if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof LivingEntity)) {
                player.sendMessage(ChatColor.RED + "No target found for Undead Army!");
                return;
            }
            LivingEntity target = (LivingEntity) rayTraceResult.getHitEntity();
    
            // Play spooky wolf sound to all players in the area
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, SoundCategory.HOSTILE, 1.0F, 0.5F);
    
            // Spawn wolves and set their target
            for (int i = 0; i < 8; i++) {
                Location spawnLocation = player.getLocation().add(0, 1, 0);
                Wolf wolf = (Wolf) player.getWorld().spawnEntity(spawnLocation, EntityType.WOLF);
                wolf.setAngry(true); // Wolves are aggressive by default
                wolf.setTarget(target);
    
                // Schedule the wolf to be removed after 20 seconds
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!wolf.isDead()) {
                        wolf.remove();
                    }
                }, 20 * 20);
            }
    
            // Update the last activation time for this player
            undeadArmyCooldowns.put(playerId, currentTime);
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Undead Army!");
        }
    }
    
    

    private void performFeatherAbility(Player player) {
        int requiredMagicLevel = 1;
        int manaCost = 40;
    
        Integer currentMana = abilities.getPlayerMana().get(player.getUniqueId());
        UUID playerId = player.getUniqueId();
    
        long currentTime = System.currentTimeMillis();
        long lastActivationTime = featherAbilityCooldowns.getOrDefault(playerId, 0L);
    
        if (currentTime - lastActivationTime < abilityCooldownMillis) {
            player.sendMessage(ChatColor.RED + "Feather ability is on cooldown! Please wait.");
            return;
        }
    
        if (currentMana != null && currentMana >= manaCost) {
            // Reduce the player's magic energy by the required amount
            int newMana = currentMana - manaCost;
            abilities.getPlayerMana().put(player.getUniqueId(), newMana);
    
            // Apply the anti-gravity effect
            PotionEffect slowFallingEffect = new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 20, 1, false, false, true);
            player.addPotionEffect(slowFallingEffect);
    
            // Notify the player
            player.sendMessage(ChatColor.GREEN + "Feather ability activated! Gravity disabled for 20 seconds.");
    
            // Update the last activation time for this player
            featherAbilityCooldowns.put(playerId, currentTime);
    
            // Warn the player when the effect is about to wear off
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.YELLOW + "Feather ability is about to wear off!");
            }, 20 * 18); // Warn 2 seconds before it wears off
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Feather!");
        }
    }
    
    
    
    
    
  
}
