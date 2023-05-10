package com.fabledclan;

import java.util.ArrayList;
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

import com.fabledclan.abilities.*;

public class AbilityUseListener implements Listener {

    private final Main plugin;
    private final Abilities abilities;
    private Map<UUID, Long> lastRightClickTime = new HashMap<>();
    // private final Map<UUID, BukkitTask> lightningStrikeTasks = new HashMap<>();
    private final ArrayList<Ability> abilityList;

    List<String> spellList = Arrays.asList("dash", "dark_vortex", "dragon_breath", "feather", "fireball", "ice_shard",
            "lightning_strike", "magic_missile", "party", "plague_swarm", "power_strike", "summon_giant", "undead_army",
            "vader_choke", "wrangle", "yeet_boat");

    public AbilityUseListener(Main plugin, Abilities abilities) {
        this.plugin = plugin;
        this.abilities = abilities;
        this.abilityList = initAbilities();
    }

    public List<String> getSpellList() {
        return spellList;
    }

    public ArrayList<Ability> initAbilities() {
        ArrayList<Ability> abilityList = new ArrayList<Ability>();

        // ADD ABILITIES HERE
        abilityList.add(new DragonBreath(plugin, abilities, "dragon_breath", 1, 50));
        abilityList.add(new SummonGiant(plugin, abilities, "summon_giant", 1, 50));
        abilityList.add(new PartySpell(plugin, abilities, "party", 1, 100));
        abilityList.add(new MagicMissile(plugin, abilities, "magic_missile", 1, 25));
        abilityList.add(new YeetBoat(plugin, abilities, "yeet_boat", 1, 30));
        abilityList.add(new Wrangle(plugin, abilities, "wrangle", 1, 50));
        abilityList.add(new Dash(plugin, abilities, "dash", 1, 25));
        abilityList.add(new IceShard(plugin, abilities, "ice_shard", 1, 20));
        abilityList.add(new PowerStrike(plugin, abilities, "power_strike", 1, 20));
        abilityList.add(new FireballSpell(plugin, abilities, "fireball", 1, 25));
        abilityList.add(new DarkVortex(plugin, abilities, "dark_vortex", 1, 75));
        abilityList.add(new PlagueSwarm(plugin, abilities, "plague_swarm", 1, 60));
        abilityList.add(new VaderChoke(plugin, abilities, "vader_choke", 1, 50));
        abilityList.add(new UndeadArmy(plugin, abilities, "undead_army", 1, 75));
        abilityList.add(new Feather(plugin, abilities, "feather", 1, 40));

        return abilityList;
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
                for (Ability a : this.abilityList) {
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

<<<<<<< HEAD
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
            BukkitTask sheepTask = new BukkitRunnable() {
                int sheepCounter = 0;
                Random random = new Random();
    
                @Override
                public void run() {
                    if (sheepCounter >= 10 || sheepCounter >= 30 * 4) {
                        cancel();
                        return;
                    }
    
                    Location sheepLocation = player.getLocation().add(Math.random() * 4 - 2, Math.random() * 4, Math.random() * 4 - 2);
                    Sheep sheep = (Sheep) player.getWorld().spawnEntity(sheepLocation, EntityType.SHEEP);
                    sheep.setHealth(1000);
                    sheep.setAI(false);
    
                    DyeColor[] colors = {DyeColor.RED, DyeColor.GREEN, DyeColor.BLUE};
                    DyeColor sheepColor = colors[sheepCounter % colors.length];
                    sheep.setColor(sheepColor);
    
                    sheepCounter++;
                }
            }.runTaskTimer(plugin, 5, 5); // 5 ticks = 0.25 seconds
    
            // Launch fireworks around the player continuously for 30 seconds
            BukkitTask fireworkTask = new BukkitRunnable() {
                int fireworkCounter = 0;
                Random random = new Random();
    
                @Override
                public void run() {
                    if (fireworkCounter >= 30 * 4) {
                        cancel();
                        return;
                    }
    
                    Location fireworkLocation = player.getLocation().add(Math.random() * 4 - 2, Math.random() * 4, Math.random() * 4 - 2);
                    Firework firework = (Firework) player.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.addEffect(FireworkEffect.builder()
                            .withColor(Color.RED)
                            .withColor(Color.GREEN)
                            .withColor(Color.BLUE)
                            .with(FireworkEffect.Type.BURST)
                            .trail(true)
                            .build());
                    fireworkMeta.setPower(1);
                    firework.setFireworkMeta(fireworkMeta);
    
                    fireworkCounter++;
                }
            }.runTaskTimer(plugin, 0, 5); // 5 ticks = 0.25 seconds
    
            // Schedule the removal of all entities and stopping the fireworks after 30 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                sheepTask.cancel();
                fireworkTask.cancel();
    
                for (Entity entity : player.getWorld().getEntities()) {
                    if (entity instanceof Sheep || entity instanceof Firework) {
                        entity.remove();
                    }
                }
            }, 30 * 20); // 20 ticks per second * 30 seconds
        } else {
            player.sendMessage(ChatColor.BLUE + "Need Magic Level " + requiredMagicLevel + " and " + manaCost + " mana points for Party Spell!");
        }
    }
    
    
    
    

    private DyeColor getNextColor(DyeColor currentColor) {
        DyeColor[] colors = DyeColor.values();
        int currentIndex = currentColor.ordinal();
        int nextIndex = (currentIndex + 1) % colors.length;
        return colors[nextIndex];
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
            RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 15, entity -> !entity.getUniqueId().equals(player.getUniqueId()));
            LivingEntity target = null;
    
            if (rayTraceResult != null && rayTraceResult.getHitEntity() instanceof LivingEntity) {
                target = (LivingEntity) rayTraceResult.getHitEntity();
            } else {
                Location hitLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(15));
                List<Entity> nearbyEntities = new ArrayList<>(hitLocation.getWorld().getNearbyEntities(hitLocation, 1, 1, 1, entity -> entity instanceof LivingEntity && !entity.getUniqueId().equals(player.getUniqueId())));

                
                if (!nearbyEntities.isEmpty()) {
                    target = (LivingEntity) nearbyEntities.get(0);
                }
            }
    
            if (target == null) {
                player.sendMessage(ChatColor.RED + "No valid target found for Vader Choke!");
                return;
            }
    
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
            final LivingEntity finalTarget = target;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (finalTarget.isValid()) {
                    finalTarget.teleport(targetLocation.subtract(0, 2, 0)); // Lower the target back to the ground
                    finalTarget.removePotionEffect(PotionEffectType.SLOW); // Remove the immobilization effect
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
=======
}
>>>>>>> 8ea463303ebb8f2a25262b1c53e953ca8933aa0a
