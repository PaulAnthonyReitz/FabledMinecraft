package com.fabledclan.Listeners;

import java.util.EnumSet;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.fabledclan.DatabaseManager;

public class LockInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
    
        if (block != null) {
            Block connectedBlock = null;
    
            if (block.getBlockData() instanceof Switch) {
                Switch buttonData = (Switch) block.getBlockData();
                connectedBlock = block.getRelative(buttonData.getFacing().getOppositeFace());
            } else if (block.getBlockData() instanceof Powerable && (block.getType().name().endsWith("_PRESSURE_PLATE"))) {
                connectedBlock = block.getRelative(BlockFace.UP);
            }
    
            if (connectedBlock != null && isLockable(connectedBlock) && DatabaseManager.isLocked(connectedBlock.getLocation())) {
                event.getPlayer().sendMessage("This door or chest is locked. Use /unlock <pin> to unlock it.");
                event.setCancelled(true);
            }
        }
    }
    
    

@EventHandler
public void onBlockRedstone(BlockRedstoneEvent event) {
    Block block = event.getBlock();
    if (block != null && event.getNewCurrent() > 0) {
        EnumSet<BlockFace> faces = EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);
        for (BlockFace face : faces) {
            Block connectedBlock = block.getRelative(face);
            if (isLockable(connectedBlock) && DatabaseManager.isLocked(connectedBlock.getLocation())) {
                event.setNewCurrent(0);
                break;
            }
        }
    }
}
    
    private boolean isLockable(Block block) {
        return block.getBlockData() instanceof Door || block.getBlockData() instanceof Chest;
    }
    
}
