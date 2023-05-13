package com.fabledclan.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.fabledclan.DatabaseManager;

public class LockCommand extends CommandClass {
    public LockCommand() {
        super("lock");
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
            player.sendMessage("Usage: /lock <pin>");
            return true;
        }

        String pin = args[0];

        // Check if the player is looking at a lockable block (door or chest)
        Block targetBlock = getTargetBlock(player, 10);

        if (targetBlock == null || (!isChest(targetBlock) && !isDoor(targetBlock))) {
            player.sendMessage("You must be looking at a door or a chest.");
            return true;
        }

    // Store the PIN in the database
    UUID ownerUuid = player.getUniqueId();
    String ownerName = Bukkit.getOfflinePlayer(ownerUuid).getName();
    DatabaseManager.insertLockedBlock(targetBlock.getLocation(), pin, ownerUuid, ownerName);
    Block adjacentBlock = getAdjacentBlock(targetBlock);
    if (adjacentBlock != null) {
        DatabaseManager.insertLockedBlock(adjacentBlock.getLocation(), pin, ownerUuid, ownerName);
    }
        // New code to lock double doors
        if (isDoor(targetBlock)) {
            List<Block> doubleDoorBlocks = getDoubleDoor(targetBlock);
            Block adjacentDoorBlock = getAdjacentBlock(targetBlock);
            if (adjacentDoorBlock != null) {
                doubleDoorBlocks.addAll(getDoubleDoor(adjacentDoorBlock));
            }
            if (!doubleDoorBlocks.isEmpty()) {
                for (Block doubleDoorBlock : doubleDoorBlocks) {
                    DatabaseManager.insertLockedBlock(doubleDoorBlock.getLocation(), pin, ownerUuid, ownerName);
                }
            }
        }

        player.sendMessage("The lock has been successfully set.");
        return true;
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
    
    private boolean isChest(Block block) {
        return block.getBlockData() instanceof Chest;
    }

    private boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }
}
