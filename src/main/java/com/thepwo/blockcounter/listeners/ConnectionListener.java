package com.thepwo.blockcounter.listeners;

import com.thepwo.blockcounter.BlockCounter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {
    private final BlockCounter plugin = BlockCounter.getPlugin();

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.plugin.getDatabase().cache(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.plugin.getDatabase().save(e.getPlayer().getUniqueId(), true);
    }
}
