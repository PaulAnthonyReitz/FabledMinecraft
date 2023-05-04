package com.fabledclan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Leaderboard implements CommandExecutor {

    private final Main plugin;

    public Leaderboard(Main plugin) {
        this.plugin = plugin;
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

    private List<List<String>> getTopPlayersByLevel(int limit) {
        List<List<String>> topPlayers = new ArrayList<>();

        try {
            PreparedStatement statement = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "SELECT uuid, level, exp FROM player_stats ORDER BY level DESC, exp DESC LIMIT ?"
            );
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String uuidString = resultSet.getString("uuid");
                UUID uuid = UUID.fromString(uuidString);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String playerName = offlinePlayer.getName();
                List<String> playerData = new ArrayList<>();
                playerData.add(playerName);
                playerData.add(String.valueOf(resultSet.getInt("level")));
                playerData.add(String.valueOf(resultSet.getInt("exp")));
                topPlayers.add(playerData);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topPlayers;
    }
}
