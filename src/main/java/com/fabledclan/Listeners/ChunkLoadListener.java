package com.fabledclan.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import com.fabledclan.Registers.CustomBlockRegistry;

public class ChunkLoadListener implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) return; // ignores newly loaded chunks
        CustomBlockRegistry.updateContainerBlocks(); // checks custom container blocks to update them
    }
}
