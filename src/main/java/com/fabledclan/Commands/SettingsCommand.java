package com.fabledclan.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.DatabaseManager;

public class SettingsCommand extends CommandClass {

    public SettingsCommand() {
        super("settings");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage("Usage: /settings <start_book | start_glytch> <true | false>");
            return true;
        }

        String setting = args[0].toLowerCase();
        boolean value = Boolean.parseBoolean(args[1]);

        switch (setting) {
            case "start_book":
                DatabaseManager.updatePlayerSetting(player.getUniqueId(), "start_book", value);
                player.sendMessage("Start book setting updated.");
                break;
            case "start_glytch":
                DatabaseManager.updatePlayerSetting(player.getUniqueId(), "start_glytch", value);
                player.sendMessage("Start glytch setting updated.");
                break;
            default:
                player.sendMessage("Invalid setting. Usage: /settings <start_book | start_glytch> <true | false>");
                break;
        }

        return true;
    }
}
