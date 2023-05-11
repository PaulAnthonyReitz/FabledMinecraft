package com.fabledclan;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.plugin.Plugin;

import com.fabledclan.CustomBlocks.*;

public class CustomBlockRegistry {
    private static ArrayList<CustomBlock> blocks;

    public static void initializeBlocks(Plugin plugin) {
        ArrayList<CustomBlock> list = new ArrayList<CustomBlock>(
            Arrays.asList(
                // ADD BLOCKS HERE:
                new WandCrafter(plugin)
            ));
        blocks = list;
    }

    public static ArrayList<CustomBlock> getBlocks() {
        return blocks;
    }
}
