package com.fabledclan;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewEnemiesCommand implements CommandExecutor {

    public ViewEnemiesCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

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

                player.sendMessage("Entity Name: " + entityName);
                player.sendMessage("HP: " + hp);
                player.sendMessage("HP Scale: " + hpScale);
                player.sendMessage("Base Attack: " + baseAttack);
                player.sendMessage("Attack Scale: " + attackScale);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }
}
