package com.fabledclan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.fabledclan.CustomBlocks.CustomBlock;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public class Main extends JavaPlugin {
    private FileConfiguration config;
    private static Main instance;
    private PlayerJoinListener playerJoinListener;
    private Map<EntityType, EnemyData> enemyDataCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getLogger().info("Plugin enabled!");

        saveDefaultConfig();
        DatabaseManager.setPlugin(this);
        playerJoinListener = new PlayerJoinListener(this);

        CustomBlock.setPlugin(this);
        CustomBlockRegistry.initializeBlocks(this);
        CustomItemRegistry.initializeItems();
        AbilityRegistry.initializeAbilities();
        CustomRecipes.addRecipes();
        
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        getServer().getPluginManager().registerEvents(new EntityDamageListenerMobs(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListenerPlayers(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        PlayerInteractListener playerInteractListener = new PlayerInteractListener(this, playerJoinListener);
        getServer().getPluginManager().registerEvents(playerInteractListener, this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        this.getCommand("lock").setExecutor(new LockCommand());
        getCommand("unlock").setExecutor(new UnlockLockCommand(this));
        getCommand("removelock").setExecutor(new RemoveLockCommand());
        getCommand("leaderboard").setExecutor(new Leaderboard());
        getCommand("book").setExecutor(new BookCommand(this));
        getCommand("settings").setExecutor(new SettingsCommand());

        MenuGUI menuGUI = new MenuGUI();
        getServer().getPluginManager().registerEvents(menuGUI, this);
        getCommand("menu").setExecutor(new MenuCommand(menuGUI));

        getCommand("viewstats").setExecutor(new ViewStatsCommand());
        getCommand("viewenemies").setExecutor(new ViewEnemiesCommand());
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

        getServer().getPluginManager().registerEvents(new LockInteractionListener(), this);
        this.getCommand("enchant").setExecutor(new EnchantCommand(this));
        getServer().getPluginManager().registerEvents(abilityUseListener, this);
        this.getCommand("unenchant").setExecutor(new UnenchantCommand(this));
        this.getServer().getPluginManager().registerEvents(abilityUseListener, this);
        this.getCommand("spells").setExecutor(new SpellsCommand(abilityUseListener));

        getCommand("home").setExecutor(new HomeCommand(this));

    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugins()[0];
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Plugin disabled!");
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
            EnemyData enemyData = DatabaseManager.getEnemyData(entityType.name());
            if (enemyData != null) {
                enemyDataCache.put(entityType, enemyData);
            }
        }
    }

    public EnemyData getCachedEnemyData(EntityType entityType) {
        return enemyDataCache.get(entityType);
    }

}
