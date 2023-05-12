package com.fabledclan.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Listeners.MenuGUI;

public class MenuCommand extends CommandClass {
    public MenuCommand() {
        super("menu");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return false;
        }

        Player player = (Player) sender;
        MenuGUI.openGUI(player);
        return true;
    }
}
