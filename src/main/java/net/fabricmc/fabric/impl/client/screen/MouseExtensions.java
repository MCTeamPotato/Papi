package net.fabricmc.fabric.impl.client.screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface MouseExtensions {
    double getHorizontalScroll();
}
