package com.fabledclan.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Party.Party;
import com.fabledclan.Party.PartyManager;

public class PartyDisbandCommand extends CommandClass {
    public PartyDisbandCommand() {
        super("partydisband");
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

        if (!party.getLeader().equals(player)) {
            player.sendMessage(ChatColor.RED + "Only the party leader can disband the party.");
            return true;
        }

        // Clear the scoreboard for all members of the party
        for (Player member : party.getMembers()) {
            member.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        PartyManager.disbandParty(party);
        player.sendMessage(ChatColor.GREEN + "The party has been disbanded.");
        return true;
    }
}
