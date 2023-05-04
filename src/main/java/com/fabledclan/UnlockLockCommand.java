package com.fabledclan;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class UnlockLockCommand implements CommandExecutor {

    private Main plugin;

    public UnlockLockCommand(Main plugin) {
        this.plugin = plugin;
    }
    @Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
        sender.sendMessage("This command can only be used by players.");
        return true;
    }

    Player player = (Player) sender;

    // Check if the player has provided a PIN
    if (args.length != 1) {
        player.sendMessage("Usage: /unlock <pin>");
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
    String storedPin = plugin.getDatabaseManager().getLockedBlockPin(targetBlock.getLocation());

    if ((storedPin == null || !storedPin.equals(pin))) {
        player.sendMessage("Incorrect PIN. Access denied.");
        return true;
    }

    if (targetBlock.getType() == Material.CHEST) {
        org.bukkit.block.Chest chestBlockState = (org.bukkit.block.Chest) targetBlock.getState();
        player.openInventory(chestBlockState.getInventory());
    } 
    if (targetBlock.getType().name().endsWith("_DOOR")) {
        openAndCloseDoor(targetBlock);
    
        List<Block> doubleDoorBlocks = getDoubleDoor(targetBlock);
        Block adjacentBlock = getAdjacentBlock(targetBlock);
        if (adjacentBlock != null) {
            doubleDoorBlocks.addAll(getDoubleDoor(adjacentBlock));
        }
        if (!doubleDoorBlocks.isEmpty()) {
            for (Block doubleDoorBlock : doubleDoorBlocks) {
                openAndCloseDoor(doubleDoorBlock);
            }
        }
    }
    
        

    return false;
}

private void openAndCloseDoor(Block doorBlock) {
    Door door = (Door) doorBlock.getBlockData();
    List<Block> doubleDoorBlocks = getDoubleDoor(doorBlock);
    Block adjacentBlock = getAdjacentBlock(doorBlock);
    if (adjacentBlock != null) {
        doubleDoorBlocks.addAll(getDoubleDoor(adjacentBlock));
    }

    if (!door.isOpen()) {
        door.setOpen(true);
        doorBlock.setBlockData(door);
        if (!doubleDoorBlocks.isEmpty()) {
            for (Block doubleDoorBlock : doubleDoorBlocks) {
                Door doubleDoor = (Door) doubleDoorBlock.getBlockData();
                if (!doubleDoor.isOpen()) {
                    doubleDoor.setOpen(true);
                    doubleDoorBlock.setBlockData(doubleDoor);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            BlockData currentBlockData = doorBlock.getBlockData();
            if (currentBlockData instanceof Door && ((Door) currentBlockData).isOpen()) {
                door.setOpen(false);
                doorBlock.setBlockData(door);
            }
            if (!doubleDoorBlocks.isEmpty()) {
                for (Block doubleDoorBlock : doubleDoorBlocks) {
                    Door doubleDoor = (Door) doubleDoorBlock.getBlockData();
                    if (doubleDoor.isOpen()) {
                        doubleDoor.setOpen(false);
                        doubleDoorBlock.setBlockData(doubleDoor);
                    }
                }
            }
        }, 100L); // 5 seconds = 100 ticks
    }
}


    private List<Block> getDoubleDoor(Block block) {
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
    
    

    private Block getTargetBlock(Player player, int maxDistance) {
        BlockIterator blockIterator = new BlockIterator(player, maxDistance);
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                return block;
            }
        }
        return null;
    }
    
    private Block getAdjacentBlock(Block block) {
        if (isChest(block)) {
            Chest chest = (Chest) block.getBlockData();
            if (chest.getType() != Chest.Type.SINGLE) {
                for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                    Block adjacent = block.getRelative(face);
                    if (adjacent.getBlockData() instanceof Chest) {
                        Chest adjacentChest = (Chest) adjacent.getBlockData();
                        if (adjacentChest.getType() != Chest.Type.SINGLE && adjacentChest.getFacing() == chest.getFacing()) {
                            return adjacent;
                        }
                    }
                }
            }
        } else if (isDoor(block)) {
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


    private boolean isChest(Block block) {
        return block.getBlockData() instanceof Chest;
    }
    
    
    private boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }
    
        
    }
