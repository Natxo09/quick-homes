package net.natxo.quickhomes.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.natxo.quickhomes.QuickHomes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageUtils {
    
    // Configuration option to control translation behavior
    private static boolean forceServerSideText = false;
    private static String serverLanguage = "en_us";
    private static final Map<String, String> serverTranslations = new HashMap<>();
    
    public static void setForceServerSideText(boolean force) {
        forceServerSideText = force;
    }
    
    public static void loadServerTranslations(String language) {
        serverLanguage = language;
        serverTranslations.clear();
        
        // Load translations from the resource file
        String resourcePath = "/assets/quickhomes/lang/" + language + ".json";
        try (InputStream stream = MessageUtils.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
                json.entrySet().forEach(entry -> {
                    serverTranslations.put(entry.getKey(), entry.getValue().getAsString());
                });
                QuickHomes.LOGGER.info("Loaded " + serverTranslations.size() + " translations for language: " + language);
            } else {
                QuickHomes.LOGGER.warn("Translation file not found: " + resourcePath + ", using fallback English");
            }
        } catch (Exception e) {
            QuickHomes.LOGGER.error("Failed to load translations for " + language, e);
        }
    }
    
    public static MutableText getLocalizedText(ServerPlayerEntity player, String key, Object... args) {
        // If configured to force server-side text (for server-only installations)
        if (forceServerSideText) {
            return Text.literal(getServerTranslation(key, args));
        }
        
        // Otherwise, always use translatable text
        // This allows clients with the mod to see translations in their language
        // Clients without the mod will see the translation key
        return Text.translatable(key, args);
    }
    
    private static String getServerTranslation(String key, Object... args) {
        // First try to get the translation from the loaded language file
        String translation = serverTranslations.get(key);
        if (translation != null) {
            // Format the translation with the provided arguments
            if (args.length > 0) {
                try {
                    // Replace %s, %d, etc. with the arguments
                    return String.format(translation, args);
                } catch (Exception e) {
                    QuickHomes.LOGGER.warn("Failed to format translation for key: " + key);
                    return translation;
                }
            }
            return translation;
        }
        
        // If not found, use the hardcoded fallback
        return getFallbackText(key, args);
    }
    
    private static String getFallbackText(String key, Object... args) {
        switch (key) {
            case "quickhomes.teleport.start.delayed":
                return String.format("Teleporting to %s in %d seconds...", args[0], args[1]);
            case "quickhomes.teleport.start.instant":
                return String.format("Teleporting to %s...", args[0]);
            case "quickhomes.home.set":
                return String.format("Home '%s' has been set", args[0]);
            case "quickhomes.home.location":
                return String.format("%s in %s", args[0], args[1]);
            case "quickhomes.home.deleted":
                return String.format("Home '%s' has been deleted", args[0]);
            case "quickhomes.teleport.complete":
                return String.format("Welcome to %s!", args[0]);
            case "quickhomes.teleport.cancelled":
                return "Teleport cancelled";
            case "quickhomes.teleport.cancelled.reason":
                return String.format("Teleport cancelled: %s", args[0]);
            case "quickhomes.list.title":
                return "Your Homes";
            case "quickhomes.dimension.overworld":
                return "Overworld";
            case "quickhomes.dimension.nether":
                return "Nether";
            case "quickhomes.dimension.end":
                return "The End";
            case "quickhomes.dimension.custom":
                return "Custom Dimension";
            case "quickhomes.error.home.notfound":
                return args.length > 0 ? String.format("Home '%s' not found", args[0]) : "Home not found";
            case "quickhomes.error.no.homes":
                return "You don't have any homes set";
            case "quickhomes.error.max.homes":
                return args.length > 0 ? String.format("Maximum homes reached (%s)", args[0]) : "Maximum homes reached";
            case "quickhomes.error.home.exists":
                return args.length > 0 ? String.format("Home '%s' already exists", args[0]) : "Home already exists";
            case "quickhomes.error.invalid.name":
                return "Invalid home name";
            case "quickhomes.error.teleport.pending":
                return "You already have a teleport in progress";
            case "quickhomes.teleport.moved":
                return "Teleport cancelled - you moved!";
            case "quickhomes.teleport.damaged":
                return "Teleport cancelled - you took damage!";
            case "quickhomes.error.world.not.loaded":
                return "Target world is not loaded";
            case "quickhomes.error.crossdim.disabled":
                return "Cross-dimensional teleportation is disabled";
            case "quickhomes.teleport.cancelled.movement":
                return "Teleport cancelled - too much movement detected";
            case "quickhomes.teleport.cancelled.attempts":
                return args.length > 0 ? String.format("Teleport cancelled after %s attempts", args[0]) : "Teleport cancelled - too many attempts";
            case "quickhomes.teleport.retry":
                return args.length >= 2 ? String.format("Retrying teleport (Attempt %s/%s)", args[0], args[1]) : "Retrying teleport";
            default:
                return key;
        }
    }
    
    private static final String PROGRESS_BAR_FILLED = "█";
    private static final String PROGRESS_BAR_EMPTY = "░";
    private static final int PROGRESS_BAR_LENGTH = 20;
    
    public static Text createProgressBar(int current, int total) {
        int filled = (int) ((double) current / total * PROGRESS_BAR_LENGTH);
        int empty = PROGRESS_BAR_LENGTH - filled;
        
        MutableText progressBar = Text.literal("");
        
        progressBar.append(Text.literal("[").formatted(Formatting.GRAY));
        
        if (filled > 0) {
            progressBar.append(Text.literal(PROGRESS_BAR_FILLED.repeat(filled))
                    .setStyle(Style.EMPTY.withColor(interpolateColor(current, total))));
        }
        
        if (empty > 0) {
            progressBar.append(Text.literal(PROGRESS_BAR_EMPTY.repeat(empty))
                    .formatted(Formatting.DARK_GRAY));
        }
        
        progressBar.append(Text.literal("]").formatted(Formatting.GRAY));
        
        progressBar.append(Text.literal(" " + (total - current) + "s")
                .formatted(Formatting.AQUA));
        
        return progressBar;
    }
    
    private static int interpolateColor(int current, int total) {
        float progress = (float) current / total;
        
        if (progress < 0.33f) {
            return 0xFF0000;
        } else if (progress < 0.66f) {
            return 0xFFAA00;
        } else {
            return 0x55FF55;
        }
    }
    
    public static Text createTeleportStartMessage(ServerPlayerEntity player, String homeName, int delay) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("⚡ ").formatted(Formatting.YELLOW));
        
        if (delay > 0) {
            message.append(getLocalizedText(player, "quickhomes.teleport.start.delayed", homeName, delay).formatted(Formatting.GRAY));
        } else {
            message.append(getLocalizedText(player, "quickhomes.teleport.start.instant", homeName).formatted(Formatting.GRAY));
        }
        
        return message;
    }
    
    public static Text createHomeSetMessage(ServerPlayerEntity player, String homeName, String dimension, double x, double y, double z) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✓ ").formatted(Formatting.GREEN));
        message.append(getLocalizedText(player, "quickhomes.home.set", homeName).formatted(Formatting.GRAY));
        message.append(Text.literal("\n  ").formatted(Formatting.GRAY));
        message.append(Text.literal("➤ ").formatted(Formatting.DARK_GRAY));
        message.append(getLocalizedText(player, "quickhomes.home.location", 
                String.format("%.0f, %.0f, %.0f", x, y, z), 
                formatDimension(player, dimension)).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createHomeDeleteMessage(ServerPlayerEntity player, String homeName) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        message.append(getLocalizedText(player, "quickhomes.home.deleted", homeName).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createTeleportCompleteMessage(ServerPlayerEntity player, String homeName) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✨ ").formatted(Formatting.LIGHT_PURPLE));
        message.append(getLocalizedText(player, "quickhomes.teleport.complete", homeName).formatted(Formatting.GREEN));
        
        return message;
    }
    
    public static Text createTeleportCancelledMessage(ServerPlayerEntity player, String reason) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        
        if (reason != null && !reason.isEmpty()) {
            message.append(getLocalizedText(player, "quickhomes.teleport.cancelled.reason", reason).formatted(Formatting.RED));
        } else {
            message.append(getLocalizedText(player, "quickhomes.teleport.cancelled").formatted(Formatting.RED));
        }
        
        return message;
    }
    
    public static Text createErrorMessage(ServerPlayerEntity player, String key, Object... args) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        
        if (key.startsWith("quickhomes.")) {
            message.append(getLocalizedText(player, key, args).formatted(Formatting.RED));
        } else {
            message.append(Text.literal(key).formatted(Formatting.RED));
        }
        
        return message;
    }
    
    public static Text createListHeader(ServerPlayerEntity player, int count, int max) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("\n"));
        message.append(Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA));
        message.append(Text.literal("\n"));
        message.append(Text.literal("     ").formatted(Formatting.GRAY));
        message.append(getLocalizedText(player, "quickhomes.list.title").formatted(Formatting.AQUA, Formatting.BOLD));
        message.append(Text.literal(" (").formatted(Formatting.GRAY));
        message.append(Text.literal(String.valueOf(count)).formatted(Formatting.YELLOW));
        message.append(Text.literal("/").formatted(Formatting.GRAY));
        message.append(Text.literal(String.valueOf(max)).formatted(Formatting.GREEN));
        message.append(Text.literal(")").formatted(Formatting.GRAY));
        message.append(Text.literal("\n"));
        message.append(Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA));
        
        return message;
    }
    
    public static Text createHomeListEntry(ServerPlayerEntity player, String name, String dimension, double x, double y, double z) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("\n "));
        message.append(Text.literal("▸ ").formatted(Formatting.DARK_GRAY));
        message.append(Text.literal(name).formatted(Formatting.GOLD));
        message.append(Text.literal(" ➤ ").formatted(Formatting.DARK_GRAY));
        message.append(getLocalizedText(player, getDimensionTranslationKey(dimension)).formatted(Formatting.AQUA));
        message.append(Text.literal("\n   ").formatted(Formatting.GRAY));
        message.append(Text.literal(String.format("%.0f, %.0f, %.0f", x, y, z)).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createListFooter() {
        return Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA);
    }
    
    private static String formatDimension(ServerPlayerEntity player, String dimension) {
        if (forceServerSideText) {
            return getServerTranslation(getDimensionTranslationKey(dimension));
        } else {
            return Text.translatable(getDimensionTranslationKey(dimension)).getString();
        }
    }
    
    private static String getDimensionTranslationKey(String dimension) {
        if (dimension.contains("overworld")) {
            return "quickhomes.dimension.overworld";
        } else if (dimension.contains("nether")) {
            return "quickhomes.dimension.nether";
        } else if (dimension.contains("end")) {
            return "quickhomes.dimension.end";
        } else {
            return "quickhomes.dimension.custom";
        }
    }
}