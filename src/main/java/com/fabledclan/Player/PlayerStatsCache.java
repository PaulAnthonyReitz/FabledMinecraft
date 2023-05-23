package com.fabledclan.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsCache {
    private Map<UUID, PlayerStats> cache = new HashMap<>();

    public PlayerStats get(UUID uuid) {
        return cache.get(uuid);
    }

    public void put(UUID uuid, PlayerStats stats) {
        cache.put(uuid, stats);
    }

    public void remove(UUID uuid) {
        cache.remove(uuid);
    }

    public Collection<PlayerStats> values() {
        return cache.values();
    }
    
}
