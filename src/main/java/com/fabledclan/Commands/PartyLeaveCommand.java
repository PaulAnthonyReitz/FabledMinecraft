package com.fabledclan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Party.Party;
import com.fabledclan.Party.PartyManager;

public class PartyLeaveCommand extends CommandClass {
    public PartyLeaveCommand() {
        super("partyleave");
        //TODO Auto-generated constructor stub
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        Party party = PartyManager.getParty(player);
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You are not in a party.");
            return true;
        }

        party.removeMember(player);
        PartyManager.removePlayerFromParty(player);
        player.sendMessage(ChatColor.GREEN + "You have left the party.");

        // Clear the player's scoreboard
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        return true;
    }
}
