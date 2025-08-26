package net.natxo.quickhomes.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Home {
    private final String name;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final RegistryKey<World> dimension;
    
    public Home(String name, double x, double y, double z, float yaw, float pitch, RegistryKey<World> dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.dimension = dimension;
    }
    
    public String getName() {
        return name;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public RegistryKey<World> getDimension() {
        return dimension;
    }
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putDouble("x", x);
        nbt.putDouble("y", y);
        nbt.putDouble("z", z);
        nbt.putFloat("yaw", yaw);
        nbt.putFloat("pitch", pitch);
        nbt.putString("dimension", dimension.getValue().toString());
        return nbt;
    }
    
    public static Home fromNbt(NbtCompound nbt) {
        String name = nbt.contains("name") ? nbt.getString("name").orElse("") : "";
        double x = nbt.contains("x") ? nbt.getDouble("x").orElse(0.0) : 0.0;
        double y = nbt.contains("y") ? nbt.getDouble("y").orElse(0.0) : 0.0;
        double z = nbt.contains("z") ? nbt.getDouble("z").orElse(0.0) : 0.0;
        float yaw = nbt.contains("yaw") ? nbt.getFloat("yaw").orElse(0.0f) : 0.0f;
        float pitch = nbt.contains("pitch") ? nbt.getFloat("pitch").orElse(0.0f) : 0.0f;
        String dimString = nbt.contains("dimension") ? nbt.getString("dimension").orElse("minecraft:overworld") : "minecraft:overworld";
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimString));
        
        return new Home(name, x, y, z, yaw, pitch, dimension);
    }
}