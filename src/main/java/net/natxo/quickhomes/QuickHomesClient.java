package net.natxo.quickhomes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QuickHomesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Client-side initialization
        // Empty because this is a server-side mod
        // But we need this for local testing in single-player
    }
}