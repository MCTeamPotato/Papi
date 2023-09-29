package net.fabricmc.fabric.mixin.client.rendering;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.impl.client.rendering.RegistrationHelperImpl;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderers.class)
public abstract class EntityRenderersMixin {
	// synthetic lambda in reloadEntityRenderers
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Dynamic
	@Redirect(method = {"method_32174", "m_234599_", "lambda$createEntityRenderers$25"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRendererFactory;create(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
	private static EntityRenderer<?> createEntityRenderer(@NotNull EntityRendererFactory<?> entityRendererFactory, EntityRendererFactory.Context context, ImmutableMap.Builder builder, EntityRendererFactory.Context context2, EntityType<?> entityType) {
		EntityRenderer<?> entityRenderer = entityRendererFactory.create(context);

		if (entityRenderer instanceof LivingEntityRenderer) { // Must be living for features
			LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers((EntityType<? extends LivingEntity>) entityType, (LivingEntityRenderer) entityRenderer, new RegistrationHelperImpl(((LivingEntityRenderer) entityRenderer)::addFeature), context);
		}

		return entityRenderer;
	}

	// private static synthetic method_32175(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/class_5617$class_5618;Ljava/lang/String;Lnet/minecraft/class_5617;)V
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Dynamic
	@Redirect(method = {"method_32175", "m_234604_", "lambda$createPlayerRenderers$26"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRendererFactory;create(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;)Lnet/minecraft/client/render/entity/EntityRenderer;"))
	private static EntityRenderer<? extends PlayerEntity> createPlayerEntityRenderer(@NotNull EntityRendererFactory<AbstractClientPlayerEntity> playerEntityRendererFactory, EntityRendererFactory.Context context, ImmutableMap.Builder builder, EntityRendererFactory.Context context2, String str, EntityRendererFactory<AbstractClientPlayerEntity> playerEntityRendererFactory2) {
		EntityRenderer<? extends PlayerEntity> entityRenderer = playerEntityRendererFactory.create(context);

		LivingEntityFeatureRendererRegistrationCallback.EVENT.invoker().registerRenderers(EntityType.PLAYER, (LivingEntityRenderer) entityRenderer, new RegistrationHelperImpl(((LivingEntityRenderer) entityRenderer)::addFeature), context);

		return entityRenderer;
	}
}