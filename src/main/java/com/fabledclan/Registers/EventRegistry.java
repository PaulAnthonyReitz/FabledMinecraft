package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.event.Listener;

import com.fabledclan.Commands.*;
import com.fabledclan.CustomBlocks.WandCrafter;
import com.fabledclan.CustomItems.*;
import com.fabledclan.Listeners.*;

public class EventRegistry {
    private static ArrayList<Listener> listeners;

    public static void init() {
        ArrayList<Listener> ret = new ArrayList<Listener>(Arrays.asList(
            // ADD LISTENERS HERE:
            new AbilityUseListener(),
            new BlockBreakListener(),
            new BlockPlaceListener(),
            new EntityDamageListenerPlayerAttackingEntity(),
            new EntityDamageListenerPlayerAttackingPlayer(),
            new EntityDamageListenerEntityAttackingPlayer(),
            new EntityDeathListener(),
            new EntitySpawnListener(),
            new LockInteractionListener(),
            new PlayerDeathListener(),
            new PlayerInteractListener(),
            new PlayerJoinListener(),
            new PrepareCraftItemListener(),
            new MenuGUI(),
            new SummonCommand(),
            new Abilities(),
            new ExperienceStorageInventory(),
            new AnimalCage(),
            new WandCrafter(),
            new Wand(),
            new ChunkLoadListener(),
            new ChatListener(),
            new PlayerQuitListener(),
            new PartyInviteListener(),
            new TestWand()
        ));
        listeners = ret;
    }

    public static ArrayList<Listener> getListeners() {
        return listeners;
    }
}
