package net.natxo.quickhomes.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageUtils {
    
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
    
    public static Text createTeleportStartMessage(String homeName, int delay) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("⚡ ").formatted(Formatting.YELLOW));
        
        if (delay > 0) {
            message.append(Text.translatable("quickhomes.teleport.start.delayed", homeName, delay).formatted(Formatting.GRAY));
        } else {
            message.append(Text.translatable("quickhomes.teleport.start.instant", homeName).formatted(Formatting.GRAY));
        }
        
        return message;
    }
    
    public static Text createHomeSetMessage(String homeName, String dimension, double x, double y, double z) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✓ ").formatted(Formatting.GREEN));
        message.append(Text.translatable("quickhomes.home.set", homeName).formatted(Formatting.GRAY));
        message.append(Text.literal("\n  ").formatted(Formatting.GRAY));
        message.append(Text.literal("➤ ").formatted(Formatting.DARK_GRAY));
        message.append(Text.translatable("quickhomes.home.location", 
                String.format("%.0f, %.0f, %.0f", x, y, z), 
                formatDimension(dimension)).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createHomeDeleteMessage(String homeName) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        message.append(Text.translatable("quickhomes.home.deleted", homeName).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createTeleportCompleteMessage(String homeName) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✨ ").formatted(Formatting.LIGHT_PURPLE));
        message.append(Text.translatable("quickhomes.teleport.complete", homeName).formatted(Formatting.GREEN));
        
        return message;
    }
    
    public static Text createTeleportCancelledMessage(String reason) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        
        if (reason != null && !reason.isEmpty()) {
            message.append(Text.translatable("quickhomes.teleport.cancelled.reason", reason).formatted(Formatting.RED));
        } else {
            message.append(Text.translatable("quickhomes.teleport.cancelled").formatted(Formatting.RED));
        }
        
        return message;
    }
    
    public static Text createErrorMessage(String key, Object... args) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("✗ ").formatted(Formatting.RED));
        
        if (key.startsWith("quickhomes.")) {
            message.append(Text.translatable(key, args).formatted(Formatting.RED));
        } else {
            message.append(Text.literal(key).formatted(Formatting.RED));
        }
        
        return message;
    }
    
    public static Text createListHeader(int count, int max) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("\n"));
        message.append(Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA));
        message.append(Text.literal("\n"));
        message.append(Text.literal("     ").formatted(Formatting.GRAY));
        message.append(Text.translatable("quickhomes.list.title").formatted(Formatting.AQUA, Formatting.BOLD));
        message.append(Text.literal(" (").formatted(Formatting.GRAY));
        message.append(Text.literal(String.valueOf(count)).formatted(Formatting.YELLOW));
        message.append(Text.literal("/").formatted(Formatting.GRAY));
        message.append(Text.literal(String.valueOf(max)).formatted(Formatting.GREEN));
        message.append(Text.literal(")").formatted(Formatting.GRAY));
        message.append(Text.literal("\n"));
        message.append(Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA));
        
        return message;
    }
    
    public static Text createHomeListEntry(String name, String dimension, double x, double y, double z) {
        MutableText message = Text.literal("");
        
        message.append(Text.literal("\n "));
        message.append(Text.literal("▸ ").formatted(Formatting.DARK_GRAY));
        message.append(Text.literal(name).formatted(Formatting.GOLD));
        message.append(Text.literal(" ➤ ").formatted(Formatting.DARK_GRAY));
        message.append(Text.translatable(getDimensionTranslationKey(dimension)).formatted(Formatting.AQUA));
        message.append(Text.literal("\n   ").formatted(Formatting.GRAY));
        message.append(Text.literal(String.format("%.0f, %.0f, %.0f", x, y, z)).formatted(Formatting.GRAY));
        
        return message;
    }
    
    public static Text createListFooter() {
        return Text.literal("═══════════════════════════════").formatted(Formatting.DARK_AQUA);
    }
    
    private static String formatDimension(String dimension) {
        return Text.translatable(getDimensionTranslationKey(dimension)).getString();
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