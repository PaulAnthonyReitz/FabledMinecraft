package com.fabledclan.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Party.Party;
import com.fabledclan.Party.PartyManager;
import com.fabledclan.Party.PartyScoreboard;

public class PartyCreateCommand extends CommandClass {
    public PartyCreateCommand() {
        super("partycreate");
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
        if (party != null) {
            player.sendMessage(ChatColor.RED + "You are already in a party.");
            return true;
        }

        party = new Party(player);
        PartyManager.addParty(party);
        player.sendMessage(ChatColor.GREEN + "You have created a new party.");

        PartyScoreboard partyScoreboard = new PartyScoreboard(party);
        player.setScoreboard(partyScoreboard.getScoreboard());

        return true;
    }
}
