package net.natxo.quickhomes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QuickHomesConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "quickhomes.json");
    
    private int maxHomes = 5;
    private int teleportDelay = 3;
    private boolean allowCrossDimension = true;
    private boolean showTeleportAnimation = true;
    private boolean serverOnlyMode = true; // When true, uses server language instead of client translations
    private String serverLanguage = "en_us"; // Language to use when in server-only mode (en_us, es_es, fr_fr, de_de, it_it, pt_br)
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
            // Create a config object with comments
            writer.write("{\n");
            writer.write("  \"_comment1\": \"Quick Homes Configuration\",\n");
            writer.write("  \"_comment2\": \"==============================================\",\n");
            writer.write("  \n");
            writer.write("  \"maxHomes\": " + maxHomes + ",\n");
            writer.write("  \"teleportDelay\": " + teleportDelay + ",\n");
            writer.write("  \"allowCrossDimension\": " + allowCrossDimension + ",\n");
            writer.write("  \"showTeleportAnimation\": " + showTeleportAnimation + ",\n");
            writer.write("  \n");
            writer.write("  \"_comment3\": \"Server-Only Mode Settings\",\n");
            writer.write("  \"_comment4\": \"When serverOnlyMode is true, the server will send messages in the configured language\",\n");
            writer.write("  \"_comment5\": \"instead of translation keys. This is useful when clients don't have the mod installed.\",\n");
            writer.write("  \"serverOnlyMode\": " + serverOnlyMode + ",\n");
            writer.write("  \n");
            writer.write("  \"_comment6\": \"Server Language (used when serverOnlyMode is true)\",\n");
            writer.write("  \"_comment7\": \"Available languages: en_us (English), es_es (Spanish), fr_fr (French),\",\n");
            writer.write("  \"_comment8\": \"de_de (German), it_it (Italian), pt_br (Portuguese Brazil)\",\n");
            writer.write("  \"serverLanguage\": \"" + serverLanguage + "\",\n");
            writer.write("  \n");
            writer.write("  \"_comment9\": \"Legacy message settings (deprecated, will be removed in future versions)\",\n");
            writer.write("  \"teleportMessage\": \"" + teleportMessage + "\",\n");
            writer.write("  \"teleportCompleteMessage\": \"" + teleportCompleteMessage + "\",\n");
            writer.write("  \"teleportCancelledMessage\": \"" + teleportCancelledMessage + "\"\n");
            writer.write("}\n");
            writer.flush();
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
    
    public boolean isServerOnlyMode() {
        return serverOnlyMode;
    }
    
    public String getServerLanguage() {
        return serverLanguage;
    }
}