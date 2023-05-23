package com.fabledclan.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.fabledclan.Party.Party;

import org.bukkit.entity.Player;

public class PartyInviteListener implements Listener {
    private Party party;
    private Player invitee;

    public PartyInviteListener(Party party, Player invitee) {
        this.party = party;
        this.invitee = invitee;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().equals(invitee)) {
            String message = event.getMessage();
            if (message.equalsIgnoreCase("/accept")) {
                party.addMember(invitee);
                invitee.sendMessage(ChatColor.GREEN + "You have joined the party.");
                event.setCancelled(true);
                AsyncPlayerChatEvent.getHandlerList().unregister(this);
            } else if (message.equalsIgnoreCase("/decline")) {
                invitee.sendMessage(ChatColor.RED + "You have declined the party invitation.");
                event.setCancelled(true);
                AsyncPlayerChatEvent.getHandlerList().unregister(this);
            }
        }
    }
}
