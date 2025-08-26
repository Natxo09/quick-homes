package net.natxo.quickhomes.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.natxo.quickhomes.QuickHomes;
import net.natxo.quickhomes.data.Home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class HomeStorage {
    private final Map<UUID, Map<String, Home>> playerHomes = new HashMap<>();
    private final File saveFile;
    
    public HomeStorage(MinecraftServer server) {
        File worldDir = server.getSavePath(net.minecraft.util.WorldSavePath.ROOT).toFile();
        File dataDir = new File(worldDir, "quick-homes");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.saveFile = new File(dataDir, "homes.dat");
    }
    
    public void load() {
        if (!saveFile.exists()) {
            QuickHomes.LOGGER.info("No homes data file found, starting fresh");
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(saveFile)) {
            NbtCompound nbt = NbtIo.readCompressed(fis, NbtSizeTracker.ofUnlimitedBytes());
            playerHomes.clear();
            
            for (String uuidString : nbt.getKeys()) {
                try {
                    UUID playerUuid = UUID.fromString(uuidString);
                    NbtCompound playerNbt = nbt.contains(uuidString) ? nbt.getCompound(uuidString).orElse(new NbtCompound()) : new NbtCompound();
                    Map<String, Home> homes = new HashMap<>();
                    
                    for (String homeName : playerNbt.getKeys()) {
                        NbtCompound homeNbt = playerNbt.contains(homeName) ? playerNbt.getCompound(homeName).orElse(new NbtCompound()) : new NbtCompound();
                        Home home = Home.fromNbt(homeNbt);
                        homes.put(homeName, home);
                    }
                    
                    playerHomes.put(playerUuid, homes);
                    QuickHomes.LOGGER.info("Loaded {} homes for player {}", homes.size(), uuidString);
                } catch (Exception e) {
                    QuickHomes.LOGGER.error("Failed to load homes for player {}", uuidString, e);
                }
            }
            
            QuickHomes.LOGGER.info("Successfully loaded homes for {} players", playerHomes.size());
        } catch (IOException e) {
            QuickHomes.LOGGER.error("Failed to load homes data from {}", saveFile.getAbsolutePath(), e);
        }
    }
    
    public void save() {
        try {
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            
            NbtCompound nbt = new NbtCompound();
            int totalHomes = 0;
            
            for (Map.Entry<UUID, Map<String, Home>> entry : playerHomes.entrySet()) {
                NbtCompound playerNbt = new NbtCompound();
                
                for (Map.Entry<String, Home> homeEntry : entry.getValue().entrySet()) {
                    playerNbt.put(homeEntry.getKey(), homeEntry.getValue().toNbt());
                    totalHomes++;
                }
                
                nbt.put(entry.getKey().toString(), playerNbt);
            }
            
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                NbtIo.writeCompressed(nbt, fos);
            }
            
            QuickHomes.LOGGER.info("Saved {} homes for {} players to {}", 
                    totalHomes, playerHomes.size(), saveFile.getAbsolutePath());
        } catch (IOException e) {
            QuickHomes.LOGGER.error("Failed to save homes data to {}", saveFile.getAbsolutePath(), e);
        }
    }
    
    public void setHome(UUID playerUuid, String name, Home home) {
        playerHomes.computeIfAbsent(playerUuid, k -> new HashMap<>()).put(name, home);
        QuickHomes.LOGGER.info("Set home '{}' for player {}", name, playerUuid);
        save();
    }
    
    public Home getHome(UUID playerUuid, String name) {
        Map<String, Home> homes = playerHomes.get(playerUuid);
        return homes != null ? homes.get(name) : null;
    }
    
    public boolean deleteHome(UUID playerUuid, String name) {
        Map<String, Home> homes = playerHomes.get(playerUuid);
        if (homes != null) {
            boolean removed = homes.remove(name) != null;
            if (removed) {
                QuickHomes.LOGGER.info("Deleted home '{}' for player {}", name, playerUuid);
                save();
            }
            return removed;
        }
        return false;
    }
    
    public Map<String, Home> getPlayerHomes(UUID playerUuid) {
        return playerHomes.getOrDefault(playerUuid, new HashMap<>());
    }
    
    public int getHomeCount(UUID playerUuid) {
        Map<String, Home> homes = playerHomes.get(playerUuid);
        return homes != null ? homes.size() : 0;
    }
}