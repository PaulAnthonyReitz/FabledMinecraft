package com.fabledclan;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class RemoveLockCommand implements CommandExecutor {

    public RemoveLockCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
    
        Player player = (Player) sender;
    
        // Check if the player has provided a PIN
        if (args.length != 1) {
            player.sendMessage("Usage: /removelock <pin>");
            return true;
        }
    
        String pin = args[0];
    
        // Check if the player is looking at a lockable block (door or chest)
        Block targetBlock = getTargetBlock(player, 10);
    
        if (targetBlock == null || (!isChest(targetBlock) && !isDoor(targetBlock))) {
            player.sendMessage("You must be looking at a door or a chest.");
            return true;
        }
    
        // Check if the provided PIN matches the one stored in the database
        String storedPin = DatabaseManager.getLockedBlockPin(targetBlock.getLocation());

    
        if ((storedPin == null || !storedPin.equals(pin))) {
            player.sendMessage("Incorrect PIN. Access denied.");
            return true;
        }
    
        // Remove the lock from the target block and the adjacent block (if present)
        DatabaseManager.deleteLockedBlock(targetBlock.getLocation());
        // Remove the lock from the adjacent door, if present
        if (isDoor(targetBlock)) {
            List<Block> doubleDoorBlocks = getDoubleDoor(targetBlock);
            Block adjacentBlock = getAdjacentBlock(targetBlock);
            if (adjacentBlock != null) {
                doubleDoorBlocks.addAll(getDoubleDoor(adjacentBlock));
            }
            for (Block doubleDoorBlock : doubleDoorBlocks) {
                String doubleDoorStoredPin = DatabaseManager.getLockedBlockPin(doubleDoorBlock.getLocation());
                if (doubleDoorStoredPin != null && doubleDoorStoredPin.equals(pin)) {
                    DatabaseManager.deleteLockedBlock(doubleDoorBlock.getLocation());
                }
            }
        }
        if (isChest(targetBlock)) {
            Block adjacentChest = getAdjacentChest(targetBlock);
            if (adjacentChest != null) {
                String adjacentChestStoredPin = DatabaseManager.getLockedBlockPin(adjacentChest.getLocation());
                if (adjacentChestStoredPin != null && adjacentChestStoredPin.equals(pin)) {
                    DatabaseManager.deleteLockedBlock(adjacentChest.getLocation());
                }
            }
        }
        

    player.sendMessage("The lock has been successfully removed.");
    return true;
}
    
public Block getAdjacentChest(Block block) {
    if (isChest(block)) {
        org.bukkit.block.data.type.Chest chest = (org.bukkit.block.data.type.Chest) block.getBlockData();
        if (chest.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
            for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                Block adjacent = block.getRelative(face);
                if (adjacent.getBlockData() instanceof org.bukkit.block.data.type.Chest) {
                    org.bukkit.block.data.type.Chest adjacentChest = (org.bukkit.block.data.type.Chest) adjacent.getBlockData();
                    if (adjacentChest.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE && adjacentChest.getFacing() == chest.getFacing()) {
                        return adjacent;
                    }
                }
            }
        }
    }
    return null;
}


public List<Block> getDoubleDoor(Block block) {
    List<Block> doubleDoorBlocks = new ArrayList<>();
    if (isDoor(block)) {
        Door door = (Door) block.getBlockData();
        BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (BlockFace face : faces) {
            Block adjacent = block.getRelative(face);
            if (adjacent.getBlockData() instanceof Door) {
                Door adjacentDoor = (Door) adjacent.getBlockData();
                if (adjacentDoor.getFacing() == door.getFacing() && adjacentDoor.isOpen() == door.isOpen() && adjacentDoor.getHinge() != door.getHinge()) {
                    doubleDoorBlocks.add(adjacent);
                    Block adjacentVertical = getAdjacentBlock(adjacent);
                    if (adjacentVertical != null) {
                        doubleDoorBlocks.add(adjacentVertical);
                    }
                }
            }
        }
        Block targetVertical = getAdjacentBlock(block);
        if (targetVertical != null) {
            doubleDoorBlocks.add(targetVertical);
        }
    }
    return doubleDoorBlocks;
}

            
public  Block getTargetBlock(Player player, int maxDistance) {
        BlockIterator blockIterator = new BlockIterator(player, maxDistance);
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                return block;
            }
        }
        return null;
    }

    public  Block getAdjacentBlock(Block block) {
        if (isChest(block)) {
            org.bukkit.block.data.type.Chest chest = (org.bukkit.block.data.type.Chest) block.getBlockData();
            if (chest.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
                for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                    Block adjacent = block.getRelative(face);
                    if (adjacent.getBlockData() instanceof org.bukkit.block.data.type.Chest) {
                        org.bukkit.block.data.type.Chest adjacentChest = (org.bukkit.block.data.type.Chest) adjacent.getBlockData();
                        if (adjacentChest.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE && adjacentChest.getFacing() == chest.getFacing()) {
                            return adjacent;
                        }
                    }
                }
            }
        }
        if (isDoor(block)) {
            Door door = (Door) block.getBlockData();
            if (door.getHalf() == Door.Half.BOTTOM) {
                Block topBlock = block.getRelative(BlockFace.UP);
                if (topBlock.getBlockData() instanceof Door) {
                    Door topDoor = (Door) topBlock.getBlockData();
                    if (topDoor.getHalf() == Door.Half.TOP && topDoor.getFacing() == door.getFacing()) {
                        return topBlock;
                    }
                }
            } else {
                Block bottomBlock = block.getRelative(BlockFace.DOWN);
                if (bottomBlock.getBlockData() instanceof Door) {
                    Door bottomDoor = (Door) bottomBlock.getBlockData();
                    if (bottomDoor.getHalf() == Door.Half.BOTTOM && bottomDoor.getFacing() == door.getFacing()) {
                        return bottomBlock;
                    }
                }
            }
        }
        return null;
    }
        
    public boolean isChest(Block block) {
        return block.getBlockData() instanceof org.bukkit.block.data.type.Chest;
    }
    
    public boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }       
}
