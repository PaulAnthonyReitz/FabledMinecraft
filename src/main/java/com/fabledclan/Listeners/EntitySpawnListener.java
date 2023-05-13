package com.fabledclan.Listeners;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.fabledclan.Main;

import java.util.Random;

public class EntitySpawnListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creature) {
            Creature creature = (Creature) event.getEntity();

            // Set custom name, health, and NM status for the spawned mob
            setCustomMobNameAndHealthAndNM(creature);
        }
    }

    private void setCustomMobNameAndHealthAndNM(LivingEntity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER, 1);
    
        // Check for NM status and adjust level
        boolean isNM = false;
        int nmChance = random.nextInt(10000); // Use 10000 for more precise percentages
    
        if (nmChance < 400) { // 4% chance
            enemyLevel += 2;
            isNM = true;
        } else if (nmChance < 700) { // 3% chance
            enemyLevel += 5;
            isNM = true;
        } else if (nmChance < 900) { // 2% chance
            enemyLevel += 10;
            isNM = true;
        } else if (nmChance < 1000) { // 1% chance
            enemyLevel += 15;
            isNM = true;
        } else if (nmChance < 1005) { // 0.05% chance
            enemyLevel += 20;
            isNM = true;
        }
    

        // Set NM status in PersistentDataContainer
        NamespacedKey nmKey = new NamespacedKey(Main.getPlugin(), "nm");
        dataContainer.set(nmKey, PersistentDataType.INTEGER, isNM ? 1 : 0);

        int roundedHealth = (int) Math.round(entity.getHealth());
        String entityType = entity.getType().toString();

        String heartColor = "\u00A75"; // Purple, change the color as needed

        String healthInfo = "[\u00A76" + enemyLevel + "\u00A7f] " + entityType + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764";

        entity.setCustomName(healthInfo);
        entity.setCustomNameVisible(true);

        // Add custom effects and generate random names for NM mobs
        if (isNM) {
            // Apply effects and generate a random name
            // Replace the following line with your own custom effects and name generation logic
            String nmName = NameGenerator.generateRandomName();
                        // Set NM name in PersistentDataContainer
                        NamespacedKey nmNameKey = new NamespacedKey(Main.getPlugin(), "NMName");
                        dataContainer.set(nmNameKey, PersistentDataType.STRING, nmName);
            
            entity.setCustomName(nmName);
            System.out.println("NM Spawned: " +nmName);
            
        }
    }

public static class NameGenerator {
    private static final String[] PREFIXES = {
        "Fred", "Bob", "Paul", "Jim", "Ritz", "Cold", "Bean", "Dailey", "Grim", "Sly", 
        "Bold", "Clever", "Dark", "Fierce", "Mighty", "Stout", "Wise", "Fearless", 
        "Brave", "Fiery", "Mattie", "Scruff", "Elder", "Young", "Swift", "Ruthless", 
        "Humble", "Proud", "Silent", "Loud"
    };
    
    private static final String[] MIDDLES = {
        "the", "of", "the Mighty", "the Fierce", "the Dark", "the Bold", "the Fearless", 
        "the Brave", "the Fiery", "the Silent", "the Wise", "the Ruthless", "the Swift", 
        "the Humble", "the Proud", "the Silent", "the Loud"
    };
    
    private static final String[] SUFFIXES = {
        "Destroyer", "Builder", "Wanderer", "Seeker", "Keeper", "Guardian", "Bringer", 
        "Crusher", "Wielder", "Weaver", "Walker", "Bearer", "Conqueror", "Protector", 
        "Challenger", "Defender", "Rider", "Warrior", "Hunter", "Sorcerer"
    };
    

    public static String generateRandomName() {
        Random random = new Random();
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        String middle = MIDDLES[random.nextInt(MIDDLES.length)];
        String suffix = SUFFIXES[random.nextInt(SUFFIXES.length)];

        return prefix + " " + middle + " " + suffix;
    }
}

}
