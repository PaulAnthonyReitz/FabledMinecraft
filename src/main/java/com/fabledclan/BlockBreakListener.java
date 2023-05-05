package com.fabledclan;

import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private Main plugin;
    private RemoveLockCommand removeLockCommand;
    private int protectionRadius = 2; // 2-block radius around the locked block

    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
        this.removeLockCommand = new RemoveLockCommand(plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        UUID playerUUID = event.getPlayer().getUniqueId();
    
        for (int x = -protectionRadius; x <= protectionRadius; x++) {
            for (int y = -protectionRadius; y <= protectionRadius; y++) {
                for (int z = -protectionRadius; z <= protectionRadius; z++) {
                    Block nearbyBlock = block.getRelative(x, y, z);
                    if (removeLockCommand.isChest(nearbyBlock) || removeLockCommand.isDoor(nearbyBlock)) {
                        String storedPin = plugin.getDatabaseManager().getLockedBlockPin(nearbyBlock.getLocation());
                        UUID ownerUUID = plugin.getDatabaseManager().getLockedBlockOwnerUUID(nearbyBlock.getLocation());
    
                        if (storedPin != null && !playerUUID.equals(ownerUUID)) {
                            // Cancel the BlockBreakEvent if a nearby block has a lock on it and the player is not the owner
                            event.setCancelled(true);
    
                            event.getPlayer().sendMessage("You can't break blocks near a locked block!");
                            event.getPlayer().playSound(block.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                            return;
                        }
                    }
                }
            }
        }
    }
}
    
