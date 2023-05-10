package com.fabledclan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class Main extends JavaPlugin {

    private DatabaseManager databaseManager;
    private FileConfiguration config;
    private static Main instance;
    private PlayerJoinListener playerJoinListener;
    private Map<EntityType, EnemyData> enemyDataCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("Plugin enabled!");

        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        playerJoinListener = new PlayerJoinListener(this, databaseManager);

        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        PlayerInteractListener playerInteractListener = new PlayerInteractListener(this, playerJoinListener);
        getServer().getPluginManager().registerEvents(playerInteractListener, this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        this.getCommand("lock").setExecutor(new LockCommand(this));
        getCommand("unlock").setExecutor(new UnlockLockCommand(this));
        getCommand("removelock").setExecutor(new RemoveLockCommand(this));
        getCommand("leaderboard").setExecutor(new Leaderboard(this));
        getCommand("book").setExecutor(new BookCommand(this));
        getCommand("settings").setExecutor(new SettingsCommand(this));

        MenuGUI menuGUI = new MenuGUI(databaseManager);
        getServer().getPluginManager().registerEvents(menuGUI, this);
        getCommand("menu").setExecutor(new MenuCommand(menuGUI));

        getCommand("viewstats").setExecutor(new ViewStatsCommand(this));
        getCommand("viewenemies").setExecutor(new ViewEnemiesCommand(this));
        getCommand("setattributes").setExecutor(new SetAttributesCommand());
        getCommand("updateenemypages").setExecutor(new UpdateEnemyPagesCommand(playerJoinListener.getEnemyCache()));

        // Populate the cache when the server starts
        populateEnemyDataCache();
        Abilities abilities = new Abilities(this);
        SummonCommand summonCommand = new SummonCommand(this, abilities);
        getCommand("summon").setExecutor(summonCommand);
        getServer().getPluginManager().registerEvents(abilities, this);
        getServer().getPluginManager().registerEvents(summonCommand, this);
        abilities.startTasks();

        AbilityUseListener abilityUseListener = new AbilityUseListener(this, abilities);

        getServer().getPluginManager().registerEvents(new LockInteractionListener(this), this);
        this.getCommand("enchant").setExecutor(new EnchantCommand(this));
        getServer().getPluginManager().registerEvents(abilityUseListener, this);
        this.getCommand("unenchant").setExecutor(new UnenchantCommand(this));
        this.getServer().getPluginManager().registerEvents(abilityUseListener, this);
        this.getCommand("spells").setExecutor(new SpellsCommand(abilityUseListener));

        getCommand("home").setExecutor(new HomeCommand(this));

    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Plugin disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
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

    public PlayerJoinListener getPlayerJoinListener() {
        return playerJoinListener;
    }

    private void populateEnemyDataCache() {
        for (EntityType entityType : EntityType.values()) {
            EnemyData enemyData = getDatabaseManager().getEnemyData(entityType.name());
            if (enemyData != null) {
                enemyDataCache.put(entityType, enemyData);
            }
        }
    }

    public EnemyData getCachedEnemyData(EntityType entityType) {
        return enemyDataCache.get(entityType);
    }

}
