package com.fabledclan.TabScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.fabledclan.Main;
import com.fabledclan.Player.PlayerStats;
public class ScheduleTab {

    public static void updatePlayerListNames() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerStats stats =  Main.getPlayerStatsCache().get(player.getUniqueId());
                if (stats != null) {
                    String listName = ChatColor.GREEN + player.getName() + " - " 
                    + ChatColor.YELLOW + stats.getAttack() + "\u2694 "  // ⚔ represents Attack
                    + ChatColor.BLUE + stats.getDefense() + "\u26E8 "  // 🛡 represents Defense
                    + ChatColor.RED + stats.getMaxHealth() + "\u2764 "  // ❤ represents Max Health
                    + ChatColor.WHITE + stats.getMovementSpeed() + "M "  // M represents Movement Speed
                    + ChatColor.GOLD + stats.getLevel() + "LV "  // LV represents Level
                    + ChatColor.AQUA + stats.getExp();  // EXP represents EXP
                player.setPlayerListName(listName);
                
                
                
                }
            }
        }, 0L, 20L);
    }   
}
