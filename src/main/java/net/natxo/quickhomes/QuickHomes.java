package net.natxo.quickhomes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.natxo.quickhomes.command.HomeCommands;
import net.natxo.quickhomes.config.QuickHomesConfig;
import net.natxo.quickhomes.storage.HomeStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickHomes implements ModInitializer {
	public static final String MOD_ID = "quickhomes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	private static HomeStorage homeStorage;
	private static QuickHomesConfig config;
	private static MinecraftServer server;
	
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Quick Homes mod");
		
		config = QuickHomesConfig.loadConfig();
		
		CommandRegistrationCallback.EVENT.register(HomeCommands::register);
		
		ServerLifecycleEvents.SERVER_STARTED.register(srv -> {
			server = srv;
			homeStorage = new HomeStorage(srv);
			homeStorage.load();
			LOGGER.info("Quick Homes mod loaded successfully");
		});
		
		ServerLifecycleEvents.SERVER_STOPPING.register(srv -> {
			if (homeStorage != null) {
				LOGGER.info("Saving homes data before server stops...");
				homeStorage.save();
			}
		});
		
		// Auto-save every 5 minutes using server tick events
		ServerTickEvents.END_SERVER_TICK.register(srv -> {
			if (srv.getTicks() % (20 * 60 * 5) == 0) { // 20 ticks/sec * 60 sec * 5 min
				if (homeStorage != null) {
					homeStorage.save();
					LOGGER.debug("Auto-saving homes data");
				}
			}
		});
	}
	
	public static HomeStorage getHomeStorage() {
		return homeStorage;
	}
	
	public static QuickHomesConfig getConfig() {
		return config;
	}
	
	public static MinecraftServer getServer() {
		return server;
	}
}