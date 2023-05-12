package com.fabledclan;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.fabledclan.CustomBlocks.*;

// This class is used to register all the custom blocks
// Newly created blocks will have to be registered in the initializeBlocks() method
// initializeBlocks() is called in Main

public class CustomBlockRegistry {
    private static ArrayList<CustomBlock> blocks;

    public static void initializeBlocks(Plugin plugin) {
        ArrayList<CustomBlock> list = new ArrayList<CustomBlock>(
            Arrays.asList(
                // ADD BLOCKS HERE:
                new WandCrafter()
            ));
        blocks = list;

        // Loops over CustomContainer blocks and checks each block listed in the database to initialize metadata
        for (CustomBlock b : blocks) {
            if (!(b instanceof CustomContainer)) continue;
            for (World world : plugin.getServer().getWorlds()) {
                ArrayList<Location> locations = DatabaseManager.getAllCustomContainerLocations(world);
                for (Location location : locations) {
                    String blockName = DatabaseManager.getCustomContainerName(location);
                    System.out.println("from db return: " + blockName);
                    if (!b.getName().equals(blockName)) continue;
                    Block worldBlock = world.getBlockAt((int)location.getX(), (int)location.getY(), (int)location.getZ());
                    worldBlock.getState().setMetadata(CustomContainer.getContainerKey(), new FixedMetadataValue(plugin, b.getName()));
                }
            }
        }
    }

    public static ArrayList<CustomBlock> getBlocks() {
        return blocks;
    }
}
