package com.fabledclan.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fabledclan.DatabaseManager;
import com.fabledclan.Main;
import com.fabledclan.Player.PlayerStats;
import com.fabledclan.Player.PlayerStatsCache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class LeaderboardCommand extends CommandClass {

    public LeaderboardCommand() {
        super("leaderboard");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("leaderboard")) {
            List<List<String>> topPlayers = getTopPlayersByLevel(10);
            StringBuilder leaderboard = new StringBuilder();
            leaderboard.append(ChatColor.BOLD).append("Top 10 Players by Level\n");
            leaderboard.append(ChatColor.RESET).append("-------------------------\n");

            for (int i = 0; i < topPlayers.size(); i++) {
                List<String> playerData = topPlayers.get(i);
                leaderboard.append(i + 1).append(". ").append(playerData.get(0))
                        .append(" (Level ").append(playerData.get(1))
                        .append(", Exp ").append(playerData.get(2)).append(")\n");
            }

            sender.sendMessage(leaderboard.toString());
            return true;
        }

        return false;
    }

    public static List<List<String>> getTopPlayersByLevel(int limit) {
        List<List<String>> topPlayers = new ArrayList<>();
    
        // Get the player cache
        PlayerStatsCache playerCache = Main.getPlayerStatsCache();
    
        // Sort the cache by level and exp, and limit the results
        playerCache.values().stream()
            .sorted(Comparator.comparing(PlayerStats::getLevel).thenComparing(PlayerStats::getExp).reversed())
            .limit(limit)
            .forEach(playerStats -> {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerStats.getUuid()));
                String playerName = offlinePlayer.getName();
                if (playerName != null) {
                    List<String> playerData = new ArrayList<>();
                    playerData.add(playerName);
                    playerData.add(String.valueOf(playerStats.getLevel()));
                    playerData.add(String.valueOf(playerStats.getExp()));
                    topPlayers.add(playerData);
                }
            });
    
        return topPlayers;
    }
    
    

    public static List<Player> getTopPlayersByLevelWebsite(int limit) {
        List<LeaderboardCommand.Player> topPlayers = new ArrayList<>();
    
        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                    "SELECT uuid, level, exp FROM player_stats ORDER BY level DESC, exp DESC LIMIT ?"
            );
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
    
            while (resultSet.next()) {
                String uuidString = resultSet.getString("uuid");
                UUID uuid = UUID.fromString(uuidString);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String playerName = offlinePlayer.getName();
                Player player = new Player();
                player.name = playerName;
                player.level = resultSet.getInt("level");
                player.exp = resultSet.getInt("exp");
                topPlayers.add(player);
            }
    
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return topPlayers;
    }
    
    public static class Player {
        String name;
        int level;
        int exp;
    }
    
}
