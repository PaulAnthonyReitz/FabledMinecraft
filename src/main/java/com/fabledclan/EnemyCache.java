package com.fabledclan;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnemyCache {
    private final Main plugin;
    private final PlayerJoinListener playerJoinListener;
    private List<BaseComponent[]> enemyPages;
    private BukkitTask updateTask;

    public EnemyCache(Main plugin, PlayerJoinListener playerJoinListener) {
        this.plugin = plugin;
        this.playerJoinListener = playerJoinListener;
        this.enemyPages = new CopyOnWriteArrayList<>();

        // Start the periodic update task
        //startUpdateTask();
    }

    public List<BaseComponent[]> getEnemyPages() {
        return enemyPages;
    }

    public void startUpdateTask() {
        // Schedule the task to run asynchronously every 10 minutes
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateEnemyPages, 0L, 20L * 60L * 10L);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }

    public void updateEnemyPages() {
        List<BaseComponent[]> newEnemyPages = playerJoinListener.createEnemyPages();
        enemyPages.clear();
        enemyPages.addAll(newEnemyPages);
    }
}
