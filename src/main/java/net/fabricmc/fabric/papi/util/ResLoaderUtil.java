package net.fabricmc.fabric.papi.util;

import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.List;

public class ResLoaderUtil {
    public static List<Path> getModContainerPaths(IModInfo container) {
        return List.of(container.getOwningFile().getFile().findResource("."));
    }

    public static IModInfo getModContainerMetadata(IModInfo container) {
        return container;
    }
}
