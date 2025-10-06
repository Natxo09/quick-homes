package net.natxo.quickhomes.util;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.natxo.quickhomes.QuickHomes;
import net.natxo.quickhomes.data.Home;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TeleportManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Map<UUID, TeleportTask> pendingTeleports = new HashMap<>();
    
    public static void teleportToHome(ServerPlayerEntity player, Home home) {
        UUID playerUuid = player.getUuid();
        
        if (pendingTeleports.containsKey(playerUuid)) {
            player.sendMessage(Text.literal("You already have a pending teleport!").formatted(Formatting.RED), true);
            return;
        }
        
        int delay = QuickHomes.getConfig().getTeleportDelay();
        player.sendMessage(MessageUtils.createTeleportStartMessage(player, home.getName(), delay), false);
        
        TeleportTask task = new TeleportTask(player, home);
        pendingTeleports.put(playerUuid, task);
        
        if (delay > 0) {
            task.start(delay);
        } else {
            executeTeleport(player, home);
            pendingTeleports.remove(playerUuid);
        }
    }
    
    private static void executeTeleport(ServerPlayerEntity player, Home home) {
        ServerWorld targetWorld = QuickHomes.getServer().getWorld(home.getDimension());
        if (targetWorld == null) {
            player.sendMessage(Text.literal("Target dimension not found!").formatted(Formatting.RED), true);
            return;
        }
        
        if (QuickHomes.getConfig().isShowTeleportAnimation()) {
            spawnTeleportParticles(player.getEntityWorld(), new Vec3d(player.getX(), player.getY(), player.getZ()));
            player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        
        player.teleport(targetWorld, home.getX(), home.getY(), home.getZ(), java.util.Set.of(), home.getYaw(), home.getPitch(), false);
        
        if (QuickHomes.getConfig().isShowTeleportAnimation()) {
            spawnTeleportParticles(targetWorld, new Vec3d(home.getX(), home.getY(), home.getZ()));
            targetWorld.playSound(null, home.getX(), home.getY(), home.getZ(),
                    SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20, 0, false, false));
        }
        
        player.sendMessage(MessageUtils.createTeleportCompleteMessage(player, home.getName()), true);
    }
    
    private static void spawnTeleportParticles(ServerWorld world, Vec3d pos) {
        for (int i = 0; i < 32; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetY = world.random.nextDouble() * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
            
            world.spawnParticles(ParticleTypes.PORTAL,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0);
        }
        
        for (int i = 0; i < 16; i++) {
            double offsetX = (world.random.nextDouble() - 0.5);
            double offsetY = world.random.nextDouble();
            double offsetZ = (world.random.nextDouble() - 0.5);
            
            world.spawnParticles(ParticleTypes.WITCH,
                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }
    
    public static void cancelTeleport(UUID playerUuid, boolean sendMessage) {
        TeleportTask task = pendingTeleports.remove(playerUuid);
        if (task != null) {
            task.cancel();
            if (sendMessage && task.player != null && task.player.isAlive()) {
                task.player.sendMessage(MessageUtils.createTeleportCancelledMessage(task.player, "Movement detected"), true);
            }
        }
    }
    
    private static class TeleportTask {
        private final ServerPlayerEntity player;
        private final Home home;
        private Vec3d startPos;
        private ScheduledFuture<?> future;
        private int remainingSeconds;
        private int attemptCount = 0;
        private long firstMovementTime = 0;
        private static final int MAX_ATTEMPTS = 3;
        private static final long MAX_MOVEMENT_TIME = 5000; // 5 seconds in milliseconds
        
        public TeleportTask(ServerPlayerEntity player, Home home) {
            this.player = player;
            this.home = home;
            this.startPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        }
        
        public void start(int delaySeconds) {
            this.remainingSeconds = delaySeconds;
            
            // Cancel any existing future before creating a new one
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
            
            future = scheduler.scheduleAtFixedRate(() -> {
                if (!player.isAlive() || player.isRemoved()) {
                    cancelTeleport(player.getUuid(), false);
                    return;
                }
                
                Vec3d currentPos = new Vec3d(player.getX(), player.getY(), player.getZ());
                if (currentPos.distanceTo(startPos) > 0.5) {
                    handleMovement();
                    return;
                }
                
                if (remainingSeconds > 0) {
                    int totalSeconds = QuickHomes.getConfig().getTeleportDelay();
                    Text progressBar = MessageUtils.createProgressBar(totalSeconds - remainingSeconds, totalSeconds);
                    player.sendMessage(progressBar, true);
                    
                    if (QuickHomes.getConfig().isShowTeleportAnimation()) {
                        ServerWorld world = player.getEntityWorld();

                        double time = System.currentTimeMillis() / 1000.0;
                        int particleCount = 8;

                        for (int i = 0; i < particleCount; i++) {
                            double angle = (2 * Math.PI / particleCount) * i + (time * 2);
                            double radius = 1.2;
                            double x = player.getX() + Math.cos(angle) * radius;
                            double z = player.getZ() + Math.sin(angle) * radius;
                            double y = player.getY() + 0.1 + Math.sin(time * 3) * 0.3;

                            world.spawnParticles(ParticleTypes.ENCHANTED_HIT,
                                    x, y, z, 1, 0, 0.1, 0, 0);
                        }

                        for (int i = 0; i < 3; i++) {
                            double offsetX = (world.random.nextDouble() - 0.5) * 0.5;
                            double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;
                            double offsetY = world.random.nextDouble() * 2;

                            world.spawnParticles(ParticleTypes.END_ROD,
                                    player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                                    1, 0, 0.05, 0, 0.01);
                        }

                        if (remainingSeconds == 1) {
                            for (int i = 0; i < 20; i++) {
                                double angle = (2 * Math.PI / 20) * i;
                                double x = player.getX() + Math.cos(angle) * 0.5;
                                double z = player.getZ() + Math.sin(angle) * 0.5;

                                world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                                        x, player.getY() + 1, z, 1, 0.0, 0.0, 0.0, 0.02);
                            }
                        }
                    }
                    
                    remainingSeconds--;
                } else {
                    cancel();
                    pendingTeleports.remove(player.getUuid());
                    executeTeleport(player, home);
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
        
        public void cancel() {
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        }
        
        private void handleMovement() {
            long currentTime = System.currentTimeMillis();
            
            // Track first movement time
            if (firstMovementTime == 0) {
                firstMovementTime = currentTime;
            }
            
            // Check if player has been moving for too long
            if (currentTime - firstMovementTime > MAX_MOVEMENT_TIME) {
                player.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.teleport.cancelled.movement"), true);
                cancelTeleport(player.getUuid(), false);
                return;
            }
            
            attemptCount++;
            
            if (attemptCount >= MAX_ATTEMPTS) {
                player.sendMessage(MessageUtils.createErrorMessage(player, "quickhomes.teleport.cancelled.attempts", MAX_ATTEMPTS), true);
                cancelTeleport(player.getUuid(), false);
            } else {
                // Show retry message
                player.sendMessage(MessageUtils.getLocalizedText(player, "quickhomes.teleport.retry", attemptCount + 1, MAX_ATTEMPTS).formatted(Formatting.YELLOW), true);

                // Update start position for next attempt
                startPos = new Vec3d(player.getX(), player.getY(), player.getZ());

                // Reset remaining seconds for the retry
                remainingSeconds = QuickHomes.getConfig().getTeleportDelay();
            }
        }
    }
}