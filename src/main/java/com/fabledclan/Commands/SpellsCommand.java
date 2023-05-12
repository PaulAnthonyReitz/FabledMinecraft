package com.fabledclan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Listeners.AbilityUseListener;

public class SpellsCommand extends CommandClass {
    public SpellsCommand() {
        super("spells");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.GOLD + "Available Spells:");

        for (String spell : AbilityUseListener.getSpellList()) {
            player.sendMessage(ChatColor.GREEN + "- " + spell);
        }

        return true;
    }
}
