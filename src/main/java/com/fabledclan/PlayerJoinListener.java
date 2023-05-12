package com.fabledclan;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.fabledclan.DatabaseManager.PlayerStats;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.sql.PreparedStatement;

public class PlayerJoinListener implements Listener {

    private EnemyCache enemyCache;
    private Random random = new Random();

    private List<Flavor> glytchFlavors = Arrays.asList(
        new Flavor("Cracked Sauce", Color.fromRGB(255, 0, 0), ChatColor.RED, new PotionEffect(PotionEffectType.SPEED, 600, 1)),
        new Flavor("Nerdies", Color.fromRGB(150, 0, 255), ChatColor.DARK_PURPLE, new PotionEffect(PotionEffectType.JUMP, 600, 1)),
        new Flavor("Maui Punch", Color.fromRGB(255, 105, 180), ChatColor.LIGHT_PURPLE, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1)),
        new Flavor("Smurf Juice", Color.fromRGB(0, 0, 255), ChatColor.BLUE, new PotionEffect(PotionEffectType.REGENERATION, 600, 1)),
        new Flavor("Limit Test", Color.fromRGB(255, 255, 0), ChatColor.YELLOW, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1)),
        new Flavor("Cosmic Candy", Color.fromRGB(255, 0, 255), ChatColor.LIGHT_PURPLE, new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 1)),
        new Flavor("Greenades", Color.fromRGB(0, 255, 0), ChatColor.GREEN, new PotionEffect(PotionEffectType.HEALTH_BOOST, 600, 1)),
        new Flavor("Cherry Bomb", Color.fromRGB(255, 0, 0), ChatColor.RED, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1)),
        new Flavor("Fabled FBomb", Color.fromRGB(255, 165, 0), ChatColor.GOLD, new PotionEffect(PotionEffectType.ABSORPTION, 600, 1))
    );
    

    public PlayerJoinListener(Main plugin) {
        this.enemyCache = new EnemyCache(plugin, this);
    }

    private void applyPlayerStats(Player player) {
        PlayerStats playerStats = DatabaseManager.getPlayerStats(player.getUniqueId());
        double movementspeed = (playerStats.getMovementSpeed() * .0125) + .1;
        int health = playerStats.getMaxHealth();
        player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(movementspeed);
        player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(health);               
        // Set other attributes if needed
    }

    public EnemyCache getEnemyCache() {
        return enemyCache;
    }
    

    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DatabaseManager.createNewPlayerStats(player.getUniqueId());
        DatabaseManager.applyPlayerConfig(player.getUniqueId());
        applyPlayerStats(player);

        if (DatabaseManager.shouldGiveCustomBook(player.getUniqueId())) {
            giveCustomBook(player);
        }

        int bounty = DatabaseManager.getBounty(player.getUniqueId());
        if (bounty > 0) {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "A murderer named " +player.getName() + " has joined the server! Bounty on their head: " + ChatColor.GOLD + bounty);
        }
        //ENERY DRINKS
        if (DatabaseManager.shouldGiveGlytchPotion(player.getUniqueId())) {
            Flavor randomFlavor = glytchFlavors.get(random.nextInt(glytchFlavors.size()));
        
            ItemStack glytchEnergy = new ItemStack(Material.POTION);
            PotionMeta potionMeta = (PotionMeta) glytchEnergy.getItemMeta();
            potionMeta.setColor(randomFlavor.color);
            potionMeta.setDisplayName(randomFlavor.chatColor + "Glytch " + randomFlavor.name);
            potionMeta.addCustomEffect(randomFlavor.effect, true);
            glytchEnergy.setItemMeta(potionMeta);
            player.getInventory().addItem(glytchEnergy);
        }
        // Apply a speed potion effect to the player for 10 seconds



    }

    class Flavor {
        String name;
        Color color;
        ChatColor chatColor;
        PotionEffect effect;
    
        public Flavor(String name, Color color, ChatColor chatColor, PotionEffect effect) {
            this.name = name;
            this.color = color;
            this.chatColor = chatColor;
            this.effect = effect;
        }
    }
        
    public List<BaseComponent[]> createEnemyPages() {
        List<BaseComponent[]> enemyPages = new ArrayList<>();
    
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                    "SELECT * FROM enemies"
            );
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String entityName = resultSet.getString("entity_name");
                double hp = resultSet.getDouble("hp");
                double hpScale = resultSet.getDouble("hp_scale");
                double baseAttack = resultSet.getDouble("base_attack");
                double attackScale = resultSet.getDouble("attack_scale");
    
                BaseComponent[] enemyPage = new ComponentBuilder()
                        .append("Enemy: ").color(ChatColor.RED)
                        .append(entityName).color(ChatColor.BLACK).bold(true)
                        .append("\n")
                        .append("HP: ").color(ChatColor.RED)
                        .append(String.valueOf(hp)).color(ChatColor.BLACK)
                        .append("\n")
                        .append("HP Scale: ").color(ChatColor.RED)
                        .append(String.valueOf(hpScale)).color(ChatColor.BLACK)
                        .append("\n")
                        .append("Base Attack: ").color(ChatColor.RED)
                        .append(String.valueOf(baseAttack)).color(ChatColor.BLACK)
                        .append("\n")
                        .append("Attack Scale: ").color(ChatColor.RED)
                        .append(String.valueOf(attackScale)).color(ChatColor.BLACK)
                        .create();
    
                enemyPages.add(enemyPage);
            }
    
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return enemyPages;
    }

    public BaseComponent[] createSecondPage(Player player) {
        // Retrieve player stats and build the second page of the book
        PlayerStats playerStats = DatabaseManager.getPlayerStats(player.getUniqueId());
    
        TextComponent secondPage = new TextComponent();
        secondPage.addExtra("Movement Speed: " + playerStats.getMovementSpeed() + "\n");
        secondPage.addExtra("Attack: " + playerStats.getAttack() + "\n");
        secondPage.addExtra("Defense: " + playerStats.getDefense() + "\n");
        secondPage.addExtra("Max Health: " + playerStats.getMaxHealth() + "\n");
        secondPage.addExtra("Exp: " + playerStats.getExp() + "\n");
        secondPage.addExtra("Level: " + playerStats.getLevel() + "\n");
        secondPage.addExtra("Magic: " + playerStats.getMagic() + "\n");
        secondPage.addExtra("Stamina: " + playerStats.getStamina() + "\n\n");
    
        // Create a clickable link for the /menu command
        TextComponent link = new TextComponent("Click here to upgrade your stats!");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/menu"));
        link.setColor(ChatColor.BLUE);
        link.setUnderlined(true);
        secondPage.addExtra(link);
    
        return new BaseComponent[]{secondPage};
    }

    private void giveCustomBook(Player player) {
        ItemStack fabledBook = null;
        int fabledBookSlot = -1;
    
        // Search for an existing Fabled Book in the player's inventory
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.WRITTEN_BOOK && item.hasItemMeta()) {
                BookMeta bookMeta = (BookMeta) item.getItemMeta();
                if (bookMeta.hasTitle() && bookMeta.getTitle().equals("Welcome to Fabled Clan")) {
                    fabledBook = item;
                    fabledBookSlot = i;
                    break;
                }
            }
        }
    
        // If the Fabled Book is not found, create a new one
        if (fabledBook == null) {
            fabledBook = new ItemStack(Material.WRITTEN_BOOK);
        }
    
        // Update the Fabled Book's content
        BookMeta bookMeta = (BookMeta) fabledBook.getItemMeta();
        bookMeta.setTitle("Welcome to Fabled Clan");
        bookMeta.setAuthor("Fabled Clan");
        List<BaseComponent[]> pages = new ArrayList<>();
        pages.add(new BaseComponent[]{new TextComponent("Welcome to Fabled Clan")});
        //player stats and URL
        pages.add(createSecondPage(player));
        //ENEMY PAGES
        pages.addAll(enemyCache.getEnemyPages());

        bookMeta.spigot().setPages(pages);
        fabledBook.setItemMeta(bookMeta);

        // Add or update the Fabled Book in the player's inventory
        if (fabledBookSlot == -1) {
            player.getInventory().addItem(fabledBook);
        } else {
            player.getInventory().setItem(fabledBookSlot, fabledBook);
        }
    } 
}