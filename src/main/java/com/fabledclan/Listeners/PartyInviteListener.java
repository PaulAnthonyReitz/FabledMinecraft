package com.fabledclan.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.fabledclan.Party.Party;
import com.fabledclan.Party.PartyManager;

import org.bukkit.entity.Player;

public class PartyInviteListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player invitee = event.getPlayer();
        Party partyInvite = PartyManager.getPendingInvite(invitee);

        if (partyInvite != null) {
            String message = event.getMessage().toLowerCase();
            if (message.equalsIgnoreCase("accept")) {
                PartyManager.addPlayerToParty(invitee, partyInvite);
                invitee.sendMessage(ChatColor.GREEN + "You have joined the party.");
                PartyManager.removePendingInvite(invitee);
                event.setCancelled(true);
            } else if (message.equalsIgnoreCase("decline")) {
                invitee.sendMessage(ChatColor.RED + "You have declined the party invitation.");
                PartyManager.removePendingInvite(invitee);
                event.setCancelled(true);
            }
        }
    } 
}
