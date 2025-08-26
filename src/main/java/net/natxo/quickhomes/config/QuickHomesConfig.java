package net.natxo.quickhomes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QuickHomesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickhomes.json");
    
    private int maxHomes = 5;
    private int teleportDelay = 3;
    private boolean allowCrossDimension = true;
    private boolean showTeleportAnimation = true;
    private String teleportMessage = "§eTeleporting to home §6%s§e...";
    private String teleportCompleteMessage = "§aWelcome to §6%s§a!";
    private String teleportCancelledMessage = "§cTeleport cancelled!";
    
    public static QuickHomesConfig loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                return GSON.fromJson(reader, QuickHomesConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        QuickHomesConfig config = new QuickHomesConfig();
        config.saveConfig();
        return config;
    }
    
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int getMaxHomes() {
        return maxHomes;
    }
    
    public int getTeleportDelay() {
        return teleportDelay;
    }
    
    public boolean isAllowCrossDimension() {
        return allowCrossDimension;
    }
    
    public boolean isShowTeleportAnimation() {
        return showTeleportAnimation;
    }
    
    public String getTeleportMessage() {
        return teleportMessage;
    }
    
    public String getTeleportCompleteMessage() {
        return teleportCompleteMessage;
    }
    
    public String getTeleportCancelledMessage() {
        return teleportCancelledMessage;
    }
}