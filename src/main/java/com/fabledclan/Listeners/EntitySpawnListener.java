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
import com.fabledclan.Enemy.CalculateEnemyLevel;

import java.util.Random;

public class EntitySpawnListener implements Listener {

    private final Random random = new Random();
    private CalculateEnemyLevel cel = new CalculateEnemyLevel();

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creature) {
            Creature creature = (Creature) event.getEntity();
            cel.setEnemyLevel(creature);

            // Set custom name, health, and NM status for the spawned mob
            setCustomMobNameAndHealthAndNM(creature);
        }
    }

    private void setCustomMobNameAndHealthAndNM(LivingEntity entity) {
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
        NamespacedKey levelKey = new NamespacedKey(Main.getPlugin(), "enemy_level");
        int enemyLevel = dataContainer.getOrDefault(levelKey, PersistentDataType.INTEGER,1);
        cel.setEnemyMaxHP(entity);
    
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
            
            entity.setCustomName("[\u00A76" + enemyLevel + "\u00A7f] " + nmName + " HP: " + heartColor + roundedHealth + " " + heartColor + "\u2764");
            
        }
    }

public static class NameGenerator {
    private static final String[] PREFIXES = {
        "Fred", "Bob", "Paul", "Jim", "Ritz", "Cold", "Bean", "Dailey", "Grim", "Sly", 
        "Bold", "Clever", "Dark", "Fierce", "Mighty", "Stout", "Wise", "Fearless", 
        "Brave", "Fiery", "Mattie", "Scruff", "Elder", "Young", "Swift", "Ruthless", 
        "Humble", "Proud", "Silent", "Loud", "4k", "Atomik", "Blvckrvft", "ColdMF", "Dizze", "Exxxtacy",
        "Pumpkin", "Qwinn", "Plugz", "Dave", "Splitgator", "Corn", "Dana", "Haughtsauce","Anoetic",
        "Melon", "BigBuddyNick", "Cyde", "DNW", "Endy", "Groudon", "Preston", "Tikka", "Sephy", "Satoshi",
        "Avo", "Cheezey", "Randy", "Katana", "Silent", "LilMexx", "PlainOlbry", "SoggyBhalls", "MissTatts",
        "Mayo", "Dejay", "Rukus","Pork","RitzDaCat","Skrub","Vallec","ZachMaxed","Groudon",
        // custom names
        "Blaze", "Storm", "Steel", "Blade", "Shadow", "Neon", "Laser", "Cyber", "Vortex", "Flux",
        "Quantum", "Omega", "Alpha", "Prime", "Ultra", "Hyper", "Nitro", "Turbo", "Electro", "Chrono",
        // Silly names
        "Bumble", "Pickle", "Squiggle", "Jiggles", "Wiggles", "Doodle", "Noodle", "Sprinkle", "Giggles", "Bubbles",
        "Pudding", "Snuggles", "Cuddles", "Waffles", "Pancake", "Muffin", "Socks", "Mittens", "Banana", "Donut",
        // Knight-related names
        "Lancelot", "Gawain", "Percival", "Galahad", "Tristan", "Bedivere", "Kay", "Gareth", "Geraint", "Bors",
        "Lamorak", "Gaheris", "Pelleas", "Agravain", "Lionel", "Ector", "Dagonet", "Lucan", "Palomedes", "Safir",
        // Spooky names
        "Wraith", "Reaper", "Vampire", "Zombie", "Phantom", "Specter", "Ghoul", "Witch", "Necro", "Bones",
        "Skull", "Grim", "Doom", "Raven", "Crow", "Rune", "Hex", "Curse", "Voodoo", "Jinx"
    };


    private static final String[] MIDDLES = {
        "the", "of", "the Mighty", "the Fierce", "the Dark", "the Bold", "the Fearless", 
        "the Brave", "the Fiery", "the Silent", "the Wise", "the Ruthless", "the Swift", 
        "the Humble", "the Proud", "the Silent", "the Loud", "the Sleepy", "the Stinky", 
        "the Content Creator", "the Old", "the Silly",
        "the Unstoppable", "the Invincible", "the Unbreakable", "the Untouchable", "the Unfathomable",
        "the Unrelenting", "the Unyielding", "the Indomitable", "the Dauntless", "the Valiant",
        // Silly middles
        "the Giggly", "the Wiggly", "the Jiggly", "the Bubbly", "the Cuddly", "the Snuggly",
        "the Fluffy", "the Goofy", "the Wacky", "the Zany", "the Quirky", "the Derpy",
        // Knight-related middles
        "the Chivalrous", "the Gallant", "the Noble", "the Valorous", "the Courageous", "the Honorable",
        "the Loyal", "the Just", "the Righteous", "the Virtuous", "the Brave", "the Heroic",
        // Spooky middles
        "the Haunted", "the Cursed", "the Wicked", "the Eerie", "the Macabre", "the Sinister",
        "the Ominous", "the Dreadful", "the Diabolical", "the Demonic", "the Ghastly", "the Uncanny"
    };
    
    private static final String[] SUFFIXES = {
        "Destroyer", "Builder", "Wanderer", "Seeker", "Keeper", "Guardian", "Bringer", 
        "Crusher", "Wielder", "Weaver", "Walker", "Bearer", "Conqueror", "Protector", 
        "Challenger", "Defender", "Rider", "Warrior", "Hunter", "Sorcerer", "Lover", 
        "Chef", "Dancer", "Office-Worker", "Technomancer", "Hustler", "Goose", "NPC",
        "Maverick", "Prodigy", "Legacy", "Paragon", "Legend", "Icon", "Titan", "Supremacy", "Dominion", "Sovereign",
        "Ethereal", "Celestial", "Cosmic", "Astral", "Mystic", "Arcane", "Elemental", "Primal", "Omega", "Alpha",
        // Silly suffixes
        "Goofball", "Oddball", "Weirdo", "Silly-Pants", "Fuzzball", "Cheese-Head", "Noodle-Arms", "Spaghetti-Legs",
        "Wigglebottom", "Doodlebug", "Pickle-Nose", "Gigglepuss", "Snickerdoodle", "Jelly-Belly", "Candy-Cane",
        // Knight-related suffixes
        "the Lionhearted", "the Dragonslayer", "the Oathkeeper", "the Shieldbearer", "the Swordmaster", "the Lancemaster",
        "the Axemaster", "the Macemaster", "the Crusader", "the Templar", "the Paladin", "the Cavalier",
        // Spooky suffixes
        "the Undying", "the Soulless", "the Damned", "the Accursed", "the Tormented", "the Forsaken",
        "the Shadowmancer", "the Deathlord", "the Doomsayer", "the Plaguebearer", "the Necrolyte", "the Boneshaper"
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
