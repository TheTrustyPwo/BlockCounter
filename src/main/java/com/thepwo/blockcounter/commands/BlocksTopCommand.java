package com.thepwo.blockcounter.commands;

import com.thepwo.blockcounter.BlockCounter;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BlocksTopCommand implements CommandExecutor {
    private final BlockCounter plugin = BlockCounter.getPlugin();
    private Map<Gang, Long> gangBlocks;
    private boolean updating;

    public BlocksTopCommand() {
        this.gangBlocks = new LinkedHashMap<>();
        updating = false;
        startBlockTopUpdate();
    }

    private void startBlockTopUpdate() {
        this.updating = true;

        this.gangBlocks.clear();

        GangsPlusApi.getAllGangs().forEach(gang -> {
            gang.getAllMembers().forEach(member -> {
                this.plugin.getDatabase().get(member.getUniqueId()).whenComplete((blocks, throwable) -> {
                    gangBlocks.put(gang, gangBlocks.getOrDefault(gang, 0L) + blocks);
                });
            });
        });

        this.gangBlocks = this.gangBlocks.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        this.updating = false;
    }

    public void register() {
        PluginCommand command = this.plugin.getCommand("blockstop");
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
