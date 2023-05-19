package com.fabledclan.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
    
        if (message.equalsIgnoreCase("For Fabled!")) {
            // Obtain player
            Player player = event.getPlayer();
            
            // Create the potion effect
            PotionEffect speedBuff = new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1); // Speed II for 20 seconds
    
            // Use the server scheduler to add the potion effect in the main server thread
            Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(getClass()), () -> {
                player.addPotionEffect(speedBuff);
            });

            event.setCancelled(true);
        }
    }
}
    
