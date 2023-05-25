package com.fabledclan.CustomAbilities;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.fabledclan.Main;

public class YeetBoat extends StaminaAbility {
    public YeetBoat() {
        super("yeet_boat", 1, 30);
    }

    public void cast(Player player) {
        if (failedCastChecks(player)) return;
        
        // Spawn a boat at the player's location
        Location boatLocation = player.getLocation();
        Boat boat = player.getWorld().spawn(boatLocation, Boat.class);
    
        // Add the player to the boat
        boat.addPassenger(player);
    
        // Set the boat's velocity for height and forward momentum
        Vector velocity = player.getEyeLocation().getDirection();
        velocity.setY(4.5); // Increase the Y-component for greater height
        velocity.multiply(3); // Multiply the vector for increased forward momentum
        boat.setVelocity(velocity);
    
        // Prevent fall damage for the player
        player.setFallDistance(0);
    
        // Schedule the removal of the boat after 30 seconds
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (!boat.isDead()) {
                boat.remove();
            }
        }, 20 * 30);
    
        // Start a repeating task that checks for a 360-degree spin
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), new Runnable() {
            float initialYaw = player.getLocation().getYaw();
    
            @Override
            public void run() {
                float rotationDiff = Math.abs(player.getLocation().getYaw() - initialYaw);
                if (rotationDiff >= 350 && rotationDiff <= 370) {
                    int score = new Random().nextInt(100) + 1;
                    player.sendMessage(ChatColor.GREEN + "Radical! Your 360 spin scored " + score + " points!");
                    initialYaw = player.getLocation().getYaw(); // Reset the initial yaw to the current yaw
                }
            }
        }, 0L, 1L); // Run every tick
    
        // Cancel the repeating task after 30 seconds
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> Bukkit.getScheduler().cancelTask(taskId), 20 * 30);
    }
    
}
