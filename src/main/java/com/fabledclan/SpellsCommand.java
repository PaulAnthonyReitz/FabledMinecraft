package com.fabledclan;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpellsCommand implements CommandExecutor {
    private AbilityUseListener abilities;

    public SpellsCommand(AbilityUseListener abilities) {
        this.abilities = abilities;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.GOLD + "Available Spells:");

        for (String spell : abilities.getSpellList()) {
            player.sendMessage(ChatColor.GREEN + "- " + spell);
        }

        return true;
    }
}
