package com.fabledclan;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateEnemyPagesCommand implements CommandExecutor {

    private final EnemyCache enemyCache;

    public UpdateEnemyPagesCommand(EnemyCache enemyCache) {
        this.enemyCache = enemyCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("fabledclan.updateenemypages")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        enemyCache.updateEnemyPages();
        player.sendMessage("Enemy pages cache has been updated.");
        return true;
    }
}
