package com.fabledclan;

import com.fabledclan.Commands.CommandClass;
import com.fabledclan.Enemy.EnemyData;
import com.fabledclan.Player.PlayerStatsCache;
import com.fabledclan.Registers.*;
import com.fabledclan.TabScoreboard.ScheduleTab;
import com.fabledclan.Website.Website;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;
    private static Map<EntityType, EnemyData> enemyDataCache = new HashMap<>();
    private static PlayerStatsCache playerStatsCache;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("Plugin enabled!");

        saveDefaultConfig();
        DatabaseManager.initDatabase();

        initializeRegisters();
        CustomRecipes.addRecipes();
        initializeListeners();
        initializeCommands();

        populateEnemyDataCache();
        playerStatsCache = new PlayerStatsCache();
        ScheduleTab.updatePlayerListNames();
        Website.initWebsite();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Plugin disabled!");
        Website.stopWebsite();
    }

    private void initializeRegisters() {
        CustomBlockRegistry.initializeBlocks();
        CustomItemRegistry.initializeItems();
        AbilityRegistry.initializeAbilities();
    }

    private void initializeListeners() {
        EventRegistry.init();
        for (Listener listener : EventRegistry.getListeners()) {
            getServer().getPluginManager().registerEvents(listener, getPlugin());
        }
    }

    private void initializeCommands() {
        CommandRegistry.init();
        for (CommandClass command : CommandRegistry.getCommands()) {
            getCommand(command.getName()).setExecutor(command);
        }
    }

    public BarColor getBossBarColor(double currentHealth, double maxHealth) {
        double healthPercentage = (currentHealth / maxHealth) * 100;
        if (healthPercentage > 50) {
            return BarColor.GREEN;
        } else if (healthPercentage > 15) {
            return BarColor.YELLOW;
        } else {
            return BarColor.RED;
        }
    }

    public BarStyle getBossBarStyle() {
        return BarStyle.SOLID;
    }

    public FileConfiguration getPluginConfig() {
        return config;
    }

    public void savePluginConfig() {
        saveConfig();
    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();
    }

    private void populateEnemyDataCache() {
        for (EntityType entityType : EntityType.values()) {
            EnemyData enemyData = DatabaseManager.getEnemyData(entityType.name());
            if (enemyData != null) {
                enemyDataCache.put(entityType, enemyData);
            }
        }
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugins()[0];
    }

    public static Main getInstance() {
        return instance;
    }

    public static EnemyData getCachedEnemyData(EntityType entityType) {
        return enemyDataCache.get(entityType);
    }

    public static PlayerStatsCache getPlayerStatsCache() {
        return playerStatsCache;
    }
}