package com.fabledclan;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.fabledclan.Listeners.PlayerJoinListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EnemyCache {
    private List<BaseComponent[]> enemyPages = new CopyOnWriteArrayList<>();
    private BukkitTask updateTask;

    public List<BaseComponent[]> getEnemyPages() {
        return enemyPages;
    }

    public void startUpdateTask() {
        // Schedule the task to run asynchronously every 10 minutes
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getPlugin(), this::updateEnemyPages, 0L, 20L * 60L * 10L);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }

    public void updateEnemyPages() {
        List<BaseComponent[]> newEnemyPages = PlayerJoinListener.createEnemyPages();
        enemyPages.clear();
        enemyPages.addAll(newEnemyPages);
    }
}
