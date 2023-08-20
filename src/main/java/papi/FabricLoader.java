package papi;

import net.minecraft.client.MinecraftClient;

import java.nio.file.Path;

public class FabricLoader {
    private static final FabricLoader INSTANCE = new FabricLoader();

    private final Path configDir;

    public FabricLoader() {
        this.configDir = MinecraftClient.getInstance().runDirectory.toPath().resolve("config");
    }

    public static FabricLoader getInstance() {
        return INSTANCE;
    }

    public Path getConfigDir() {
        return this.configDir;
    }
}
