package com.thepwo.blockcounter.commands;

import com.thepwo.blockcounter.BlockCounter;
import com.thepwo.blockcounter.utils.number.NumberUtils;
import com.thepwo.blockcounter.utils.number.enums.NumberFormatType;
import com.thepwo.blockcounter.utils.string.StringUtils;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import org.bukkit.Bukkit;
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
    private Map<Gang, Long> topGangBlocks;
    private boolean updating;

    public BlocksTopCommand() {
        this.topGangBlocks = new LinkedHashMap<>();
        updating = false;
        startBlockTopUpdate();
    }

    private void startBlockTopUpdate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateBlockTop, 10L,
                this.plugin.getConfig().getLong("blockstop-update-interval") * 20L);
    }

    private void updateBlockTop() {
        this.updating = true;

        this.topGangBlocks.clear();

        GangsPlusApi.getAllGangs().forEach(gang -> {
            gang.getAllMembers().forEach(member -> {
                this.plugin.getDatabase().get(member.getUniqueId()).whenComplete((blocks, throwable) -> {
                    topGangBlocks.put(gang, topGangBlocks.getOrDefault(gang, 0L) + blocks);
                });
            });
        });

        this.topGangBlocks = this.topGangBlocks.entrySet()
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
        if (this.updating) {
            sender.sendMessage(StringUtils.colorize(this.plugin.getConfig().getStringList("messages.leaderboard-updating")));
            return false;
        }

        System.out.println("TESTESTEST");

        int position = 0;
        for (String message : this.plugin.getConfig().getStringList("messages.blockstop")) {
            if (message.startsWith("{FOR_EACH_GANG}")) {
                message = message.replace("{FOR_EACH_GANG}", "");
                for (Gang gang : this.topGangBlocks.keySet()) {
                    sender.sendMessage(StringUtils.colorize(message
                            .replace("%gang%", gang.getName())
                            .replace("%position%", String.valueOf(++position))
                            .replace("%blocks%", NumberUtils.format(this.topGangBlocks.get(gang), NumberFormatType.COMMAS))));
                }
            }

            sender.sendMessage(StringUtils.colorize(message));
        }

        return true;
    }
}
