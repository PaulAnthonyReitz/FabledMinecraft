package com.fabledclan.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Enemy.EnemyBookCache;
import com.fabledclan.Listeners.PlayerJoinListener;

public class UpdateEnemyPagesCommand extends CommandClass {

    private final EnemyBookCache enemyCache = PlayerJoinListener.getEnemyCache();

    public UpdateEnemyPagesCommand() {
        super("updateenemypages");
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
