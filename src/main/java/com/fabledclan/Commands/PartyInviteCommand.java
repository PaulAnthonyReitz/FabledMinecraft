package com.fabledclan.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fabledclan.Party.Party;
import com.fabledclan.Party.PartyManager;

public class PartyInviteCommand extends CommandClass {
    public PartyInviteCommand() {
        super("partyinvite");
        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player inviter = (Player) sender;
        if (args.length != 1) {
            inviter.sendMessage(ChatColor.RED + "Usage: /partyinvite <player>");
            return true;
        }

        Player invitee = Bukkit.getPlayer(args[0]);
        if (invitee == null) {
            inviter.sendMessage(ChatColor.RED + "That player is not online.");
            return true;
        }

        Party party = PartyManager.getParty(inviter);
        if (party == null) {
            party = new Party(inviter);
            PartyManager.addParty(party);
        }

        party.sendInvite(invitee);
        return true;
    }
}
