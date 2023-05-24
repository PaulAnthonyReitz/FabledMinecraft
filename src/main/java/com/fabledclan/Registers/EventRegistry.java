package com.fabledclan.Registers;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.event.Listener;

import com.fabledclan.Commands.SummonCommand;
import com.fabledclan.Listeners.Abilities;
import com.fabledclan.Listeners.AbilityUseListener;
import com.fabledclan.Listeners.BlockBreakListener;
import com.fabledclan.Listeners.BlockPlaceListener;
import com.fabledclan.Listeners.ChatListener;
import com.fabledclan.Listeners.EntityDamageListenerEntityAttackingPlayer;
import com.fabledclan.Listeners.EntityDamageListenerPlayerAttackingEntity;
import com.fabledclan.Listeners.EntityDamageListenerPlayerAttackingPlayer;
import com.fabledclan.Listeners.EntityDeathListener;
import com.fabledclan.Listeners.EntitySpawnListener;
import com.fabledclan.Listeners.ExperienceStorageInventory;
import com.fabledclan.Listeners.LockInteractionListener;
import com.fabledclan.Listeners.MenuGUI;
import com.fabledclan.Listeners.PartyInviteListener;
import com.fabledclan.Listeners.PlayerDeathListener;
import com.fabledclan.Listeners.PlayerInteractListener;
import com.fabledclan.Listeners.PlayerJoinListener;
import com.fabledclan.Listeners.PlayerQuitListener;
import com.fabledclan.Listeners.PrepareCraftItemListener;

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
            new ChatListener(),
            new PlayerQuitListener(),
            new PartyInviteListener()
        ));
        listeners = ret;
    }

    public static ArrayList<Listener> getListeners() {
        return listeners;
    }
}
