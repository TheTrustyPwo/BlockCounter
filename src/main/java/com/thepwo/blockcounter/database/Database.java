package com.thepwo.blockcounter.database;

import com.thepwo.blockcounter.BlockCounter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class Database {
    public final Map<UUID, Long> blockCache;
    protected final BlockCounter plugin;

    public Database(BlockCounter plugin) {
        this.plugin = plugin;
        this.blockCache = new HashMap<>();
        startAutoSave();
    }

    private void startAutoSave() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, () -> this.blockCache.keySet()
                .forEach(uuid -> this.save(uuid, false)), 10L,
                this.plugin.getConfig().getLong("database.autosave-interval") * 20L);
    }

    public void cache(UUID uuid) {
        get(uuid).whenComplete((blocks, throwable) -> this.blockCache.put(uuid, blocks));
    }

    public void uncache(UUID uuid) {
        this.blockCache.remove(uuid);
    }

    public abstract CompletableFuture<Boolean> connect();

    public abstract void close();

    public abstract CompletableFuture<Long> get(UUID uuid);

    public abstract void set(UUID uuid, long blocks);

    public abstract void save(UUID uuid, boolean uncache);
}
