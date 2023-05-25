package com.fabledclan.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.MetadataValue;

import com.fabledclan.DatabaseManager;
import com.fabledclan.CustomBlocks.CustomBlock;
import com.fabledclan.CustomBlocks.CustomContainer;
import com.fabledclan.Registers.CustomBlockRegistry;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

// Add other required imports

public class PlayerInteractListener implements Listener {
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState().hasMetadata(CustomContainer.getContainerKey())) {
                List<MetadataValue> values = event.getClickedBlock().getState().getMetadata(CustomContainer.getContainerKey());
                if (values.size() == 0) return;
                String value = values.get(0).asString();
                for (CustomBlock block : CustomBlockRegistry.getBlocks()) {
                    if (!(block instanceof CustomContainer)) continue;
                    if (block.getName().equals(value)) {
                        ((CustomContainer)block).interactEvent(event);
                    }
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && (isChest(block) || isDoor(block))) {
                String storedPin = DatabaseManager.getLockedBlockPin(block.getLocation());

                if (storedPin != null) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("This block is locked. Use /unlock <pin> to unlock it.");
                }
            }
        }

        // Book opening logic
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (item != null && item.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) item.getItemMeta();

            if (bookMeta.getAuthor().equals("Fabled Clan")) {
                List<BaseComponent[]> pages = generateFreshPages(player);
                bookMeta.spigot().setPages(pages);
                item.setItemMeta(bookMeta);
            }
        }
    }

    private boolean isChest(Block block) {
        return block.getBlockData() instanceof Chest;
    }

    private boolean isDoor(Block block) {
        return block.getBlockData() instanceof Door;
    }

    private List<BaseComponent[]> generateFreshPages(Player player) {
        List<BaseComponent[]> pages = new ArrayList<>();
        pages.add(new BaseComponent[]{new TextComponent("Welcome to Fabled Clan")});
        // player stats and URL
        pages.add(PlayerJoinListener.createSecondPage(player));
        // ENEMY PAGES
        pages.addAll(PlayerJoinListener.createEnemyPages());
        return pages;
    }
}
