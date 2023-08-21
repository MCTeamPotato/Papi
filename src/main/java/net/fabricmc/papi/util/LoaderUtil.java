package net.fabricmc.papi.util;

import net.minecraftforge.forgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.List;

public class LoaderUtil {
    public static List<Path> getModContainerPaths(IModInfo container) {
        return List.of(container.getOwningFile().getFile().findResource("."));
    }
}
