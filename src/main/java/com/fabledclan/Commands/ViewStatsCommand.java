package com.fabledclan.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.bukkit.command.Command;

public class ViewStatsCommand extends CommandClass {

    public ViewStatsCommand() {
        super("viewstats");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        try {
            PreparedStatement statement = DatabaseManager.getConnection().prepareStatement(
                    "SELECT * FROM player_stats"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                double movementSpeed = resultSet.getDouble("movement_speed");
                double attack = resultSet.getDouble("attack");
                double defense = resultSet.getDouble("defense");
                double maxHealth = resultSet.getDouble("max_health");
                int exp = resultSet.getInt("exp");
                int level = resultSet.getInt("level");

                player.sendMessage("UUID: " + uuid);
                player.sendMessage("Movement Speed: " + movementSpeed);
                player.sendMessage("Attack: " + attack);
                player.sendMessage("Defense: " + defense);
                player.sendMessage("Max Health: " + maxHealth);
                player.sendMessage("Exp: " + exp);
                player.sendMessage("Level: " + level);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
