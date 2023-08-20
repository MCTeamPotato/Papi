package papi;

import net.minecraft.client.MinecraftClient;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.nio.file.Path;

public class FabricLoader {
    private final Path configDir;

    public FabricLoader() {
        this.configDir = getConfigPath();
    }

    private static Path getConfigPath() {
        Path path;
        try {
            path = MinecraftClient.getInstance().runDirectory.toPath().resolve("config");
        } catch (Throwable e){

            path = ServerLifecycleHooks.getCurrentServer().getRunDirectory().toPath().resolve("config");
        }
        return path;
    }

    public static FabricLoader getInstance() {
        return new FabricLoader();
    }

    public Path getConfigDir() {
        return this.configDir;
    }
}
