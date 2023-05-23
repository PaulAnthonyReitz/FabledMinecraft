package com.fabledclan.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Main;
import com.fabledclan.Player.PlayerStats;
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
        PlayerStats stats = Main.getPlayerStatsCache().get(player.getUniqueId());

        if (stats != null) {
            player.sendMessage("UUID: " + player.getUniqueId());
            player.sendMessage("Movement Speed: " + stats.getMovementSpeed());
            player.sendMessage("Attack: " + stats.getAttack());
            player.sendMessage("Defense: " + stats.getDefense());
            player.sendMessage("Max Health: " + stats.getMaxHealth());
            player.sendMessage("Exp: " + stats.getExp());
            player.sendMessage("Level: " + stats.getLevel());
        } else {
            player.sendMessage("No stats found for this player.");
        }

        return true;
    }
}
