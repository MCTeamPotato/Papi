package net.fabricmc.fabric.impl.resource.loader;

import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;

public class ModResourcePackCreator {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = text -> Text.translatable("pack.nameAndSource", text, Text.translatable("pack.source.fabricmod"));
}
