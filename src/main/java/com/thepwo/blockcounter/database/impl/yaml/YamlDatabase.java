package com.thepwo.blockcounter.database.impl.yaml;

import com.thepwo.blockcounter.BlockCounter;
import com.thepwo.blockcounter.database.Database;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlDatabase extends Database {
    private final String fileName;
    private File file;
    private YamlConfiguration data;

    public YamlDatabase(BlockCounter plugin) {
        super(plugin);
        this.fileName = this.plugin.getConfig().getString("database.yaml.file");
    }

    @Override
    public CompletableFuture<Boolean> connect() {
        return CompletableFuture.supplyAsync(() -> {
            this.file = new File(this.plugin.getDataFolder(), this.fileName);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    return false;
                }
            }

            this.data = YamlConfiguration.loadConfiguration(this.file);
            return true;
        });
    }

    @Override
    public void close() {
        saveFile();
    }

    @Override
    public CompletableFuture<Long> get(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.blockCache.containsKey(uuid)) return this.blockCache.get(uuid);
            return this.data.getLong(uuid.toString(), 0L);
        });
    }

    @Override
    public void set(UUID uuid, long blocks) {
        CompletableFuture.runAsync(() -> {
            if (this.blockCache.containsKey(uuid)) {
                this.blockCache.put(uuid, blocks);
                return;
            }

            this.data.set(uuid.toString(), blocks);
            saveFile();
        });
    }

    @Override
    public void save(UUID uuid, boolean uncache) {
        CompletableFuture.runAsync(() -> {
            this.data.set(uuid.toString(), this.blockCache.getOrDefault(uuid, 0L));
            saveFile();

            if (uncache) uncache(uuid);
        });
    }

    private void saveFile() {
        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
