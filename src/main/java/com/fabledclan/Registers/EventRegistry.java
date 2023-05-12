package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.event.Listener;

import com.fabledclan.Commands.SummonCommand;
import com.fabledclan.Listeners.Abilities;
import com.fabledclan.Listeners.AbilityUseListener;
import com.fabledclan.Listeners.BlockBreakListener;
import com.fabledclan.Listeners.BlockPlaceListener;
import com.fabledclan.Listeners.EntityDamageListenerMobs;
import com.fabledclan.Listeners.EntityDamageListenerPlayers;
import com.fabledclan.Listeners.EntityDeathListener;
import com.fabledclan.Listeners.EntitySpawnListener;
import com.fabledclan.Listeners.LockInteractionListener;
import com.fabledclan.Listeners.MenuGUI;
import com.fabledclan.Listeners.PlayerDeathListener;
import com.fabledclan.Listeners.PlayerInteractListener;
import com.fabledclan.Listeners.PlayerJoinListener;
import com.fabledclan.Listeners.PrepareCraftItemListener;

public class EventRegistry {
    private static ArrayList<Listener> listeners;

    public static void init() {
        ArrayList<Listener> ret = new ArrayList<Listener>(Arrays.asList(
            // ADD LISTENERS HERE:
            new AbilityUseListener(),
            new BlockBreakListener(),
            new BlockPlaceListener(),
            new EntityDamageListenerMobs(),
            new EntityDamageListenerPlayers(),
            new EntityDeathListener(),
            new EntitySpawnListener(),
            new LockInteractionListener(),
            new PlayerDeathListener(),
            new PlayerInteractListener(),
            new PlayerJoinListener(),
            new PrepareCraftItemListener(),
            new MenuGUI(),
            new SummonCommand(),
            new Abilities()
        ));
        listeners = ret;
    }

    public static ArrayList<Listener> getListeners() {
        return listeners;
    }
}
