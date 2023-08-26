package net.fabricmc.fabric.impl.resource.loader;

import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.TranslatableText;

public class ModResourcePackCreator {
    public static final ResourcePackSource RESOURCE_PACK_SOURCE = text -> new TranslatableText("pack.nameAndSource", text, new TranslatableText("pack.source.fabricmod"));
}