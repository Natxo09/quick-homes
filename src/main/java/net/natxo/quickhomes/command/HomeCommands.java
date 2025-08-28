package net.natxo.quickhomes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.natxo.quickhomes.QuickHomes;
import net.natxo.quickhomes.data.Home;
import net.natxo.quickhomes.util.TeleportManager;
import net.natxo.quickhomes.util.MessageUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HomeCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("sethome")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .executes(context -> setHome(context, StringArgumentType.getString(context, "name"))))
                .executes(context -> setHome(context, "home")));
        
        dispatcher.register(CommandManager.literal("home")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .suggests(HomeCommands::suggestHomes)
                        .executes(context -> goHome(context, StringArgumentType.getString(context, "name"))))
                .executes(context -> goHome(context, "home")));
        
        dispatcher.register(CommandManager.literal("delhome")
                .then(CommandManager.argument("name", StringArgumentType.word())
                        .suggests(HomeCommands::suggestHomes)
                        .executes(context -> deleteHome(context, StringArgumentType.getString(context, "name"))))
                .executes(context -> deleteHome(context, "home")));
        
        dispatcher.register(CommandManager.literal("homes")
                .executes(HomeCommands::listHomes));
    }
    
    private static int setHome(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        Home existingHome = QuickHomes.getHomeStorage().getHome(player.getUuid(), name);
        int homeCount = QuickHomes.getHomeStorage().getHomeCount(player.getUuid());
        int maxHomes = QuickHomes.getConfig().getMaxHomes();
        
        if (existingHome != null) {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.home.exists", name));
            return 0;
        }
        
        if (homeCount >= maxHomes) {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.max.homes", maxHomes));
            return 0;
        }
        
        Home home = new Home(
                name,
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYaw(),
                player.getPitch(),
                player.getWorld().getRegistryKey()
        );
        
        QuickHomes.getHomeStorage().setHome(player.getUuid(), name, home);
        source.sendFeedback(() -> MessageUtils.createHomeSetMessage(
                player,
                name, 
                player.getWorld().getRegistryKey().getValue().toString(),
                player.getX(), 
                player.getY(), 
                player.getZ()
        ), false);
        
        return 1;
    }
    
    private static int goHome(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        Home home = QuickHomes.getHomeStorage().getHome(player.getUuid(), name);
        if (home == null) {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.home.notfound", name));
            return 0;
        }
        
        if (!QuickHomes.getConfig().isAllowCrossDimension() && !player.getWorld().getRegistryKey().equals(home.getDimension())) {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.crossdim.disabled"));
            return 0;
        }
        
        TeleportManager.teleportToHome(player, home);
        return 1;
    }
    
    private static int deleteHome(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        if (QuickHomes.getHomeStorage().deleteHome(player.getUuid(), name)) {
            source.sendFeedback(() -> MessageUtils.createHomeDeleteMessage(player, name), false);
            return 1;
        } else {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.home.notfound", name));
            return 0;
        }
    }
    
    private static int listHomes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        
        Map<String, Home> homes = QuickHomes.getHomeStorage().getPlayerHomes(player.getUuid());
        
        if (homes.isEmpty()) {
            source.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.error.no.homes"));
            return 0;
        }
        
        int maxHomes = QuickHomes.getConfig().getMaxHomes();
        source.sendFeedback(() -> MessageUtils.createListHeader(player, homes.size(), maxHomes), false);
        
        for (Map.Entry<String, Home> entry : homes.entrySet()) {
            Home home = entry.getValue();
            String dimension = home.getDimension().getValue().toString();
            source.sendFeedback(() -> MessageUtils.createHomeListEntry(
                    player,
                    entry.getKey(),
                    dimension,
                    home.getX(),
                    home.getY(),
                    home.getZ()
            ), false);
        }
        
        source.sendFeedback(() -> MessageUtils.createListFooter(), false);
        
        return 1;
    }
    
    private static CompletableFuture<Suggestions> suggestHomes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Map<String, Home> homes = QuickHomes.getHomeStorage().getPlayerHomes(player.getUuid());
        
        for (String homeName : homes.keySet()) {
            builder.suggest(homeName);
        }
        
        return builder.buildFuture();
    }
}