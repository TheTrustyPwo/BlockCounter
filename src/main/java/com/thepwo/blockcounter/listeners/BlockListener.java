package com.thepwo.blockcounter.listeners;

import com.thepwo.blockcounter.BlockCounter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener {
    private final BlockCounter plugin = BlockCounter.getPlugin();

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        this.plugin.getDatabase().get(e.getPlayer().getUniqueId())
                .whenComplete((blocks, throwable) -> this.plugin.getDatabase().set(e.getPlayer().getUniqueId(), blocks + 1));
    }
}
