package com.fabledclan.Listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.MetadataValue;

import com.fabledclan.CustomBlockRegistry;
import com.fabledclan.DatabaseManager;
import com.fabledclan.RemoveLockCommand;
import com.fabledclan.CustomBlocks.CustomBlock;
import com.fabledclan.CustomBlocks.CustomContainer;

public class BlockBreakListener implements Listener {

    private int protectionRadius = 2; // 2-block radius around the locked block

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        UUID playerUUID = event.getPlayer().getUniqueId();

        if (event.getBlock().getState().hasMetadata(CustomContainer.getContainerKey())) {
            List<MetadataValue> metadataValues = event.getBlock().getState().getMetadata(CustomContainer.getContainerKey());
            if (metadataValues.size() == 0) return;
            String value = metadataValues.get(0).asString();
            for (CustomBlock b : CustomBlockRegistry.getBlocks()) {
                if (!(b instanceof CustomContainer)) continue;
                if (b.getMaterial() != event.getBlock().getType()) continue;
                if (b.getName().equals(value)) {
                    ((CustomContainer)b).breakEvent(event);
                }
            }
        }
    
        for (int x = -protectionRadius; x <= protectionRadius; x++) {
            for (int y = -protectionRadius; y <= protectionRadius; y++) {
                for (int z = -protectionRadius; z <= protectionRadius; z++) {
                    Block nearbyBlock = block.getRelative(x, y, z);
                    if (RemoveLockCommand.isChest(nearbyBlock) || RemoveLockCommand.isDoor(nearbyBlock)) {
                        String storedPin = DatabaseManager.getLockedBlockPin(nearbyBlock.getLocation());
                        UUID ownerUUID = DatabaseManager.getLockedBlockOwnerUUID(nearbyBlock.getLocation());
    
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
    
