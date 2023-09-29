package net.fabricmc.fabric.impl.object.builder;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ObjectBuilderForgeImpl {
    private static final Map<EntityType<? extends LivingEntity>, DefaultAttributeContainer> SUPPLIERS = new Object2ObjectOpenHashMap<>();

    public static DefaultAttributeContainer register(EntityType<? extends LivingEntity> entityType, DefaultAttributeContainer supplier) {
        return SUPPLIERS.put(entityType, supplier);
    }

    @SubscribeEvent
    public static void onAttributesCreate(@NotNull EntityAttributeCreationEvent event) {
        SUPPLIERS.forEach(event::put);
    }
}
