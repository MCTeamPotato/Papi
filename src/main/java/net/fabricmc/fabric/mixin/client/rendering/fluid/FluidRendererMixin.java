package net.fabricmc.fabric.mixin.client.rendering.fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRendererHookContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
	@Final
	@Shadow
	private Sprite[] lavaSprites;
	@Final
	@Shadow
	private Sprite[] waterSprites;
	@Shadow
	private Sprite waterOverlaySprite;

	@Unique
	private ThreadLocal<FluidRendererHookContainer> fabric_renderHandler = null;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(CallbackInfo ci) {
		if (this.fabric_renderHandler == null) fabric_renderHandler = ThreadLocal.withInitial(FluidRendererHookContainer::new);
	}

	@Unique
	private final ThreadLocal<Boolean> fabric_customRendering = ThreadLocal.withInitial(() -> false);

	@Inject(at = @At("RETURN"), method = "onResourceReload")
	public void onResourceReloadReturn(CallbackInfo info) {
		FluidRenderer self = (FluidRenderer) (Object) this;
		((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).onFluidRendererReload(self, waterSprites, lavaSprites, waterOverlaySprite);
	}
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void tesselate(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
		if (!fabric_customRendering.get()) {
			// Prevent recursively looking up custom fluid renderers when default behavior is being invoked
			try {
				fabric_customRendering.set(true);
				papi$tessellateViaHandler(view, pos, vertexConsumer, blockState, fluidState, info);
			} finally {
				fabric_customRendering.set(false);
			}
		}
		if (info.isCancelled()) {
			return;
		}
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		ctr.getSprites(view, pos, fluidState);
	}

	@Unique
	private void papi$tessellateViaHandler(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		FluidRenderHandler handler = ((FluidRenderHandlerRegistryImpl) FluidRenderHandlerRegistry.INSTANCE).getOverride(fluidState.getFluid());
		ctr.view = view;
		ctr.pos = pos;
		ctr.blockState = blockState;
		ctr.fluidState = fluidState;
		ctr.handler = handler;
		if (handler != null) {
			handler.renderFluid(pos, view, vertexConsumer, blockState, fluidState);
			info.cancel();
		}
	}
	@Inject(at = @At("RETURN"), method = "render")
	public void tesselateReturn(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
		fabric_renderHandler.get().clear();
	}

	// Redirect redirects all 'waterOverlaySprite' gets in 'render' to this method, this is correct
	@Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/block/FluidRenderer;waterOverlaySprite:Lnet/minecraft/client/texture/Sprite;"), method = "render")
	public Sprite modWaterOverlaySprite(FluidRenderer self) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		return ctr.handler != null && ctr.hasOverlay ? ctr.overlay : waterOverlaySprite;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/extensions/common/IClientFluidTypeExtensions;getTintColor(Lnet/minecraft/fluid/FluidState;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
	public int modTintColor(IClientFluidTypeExtensions extensions, FluidState state, BlockRenderView getter, BlockPos pos) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		if (ctr.handler != null) {
			// Include alpha in tint color
			int color = ctr.handler.getFluidColor(ctr.view, ctr.pos, ctr.fluidState);
			return 0xFF000000 | color;
		}
		return extensions.getTintColor(state, getter, pos);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;getFluidSprites(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;)[Lnet/minecraft/client/texture/Sprite;"))
	private Sprite[] redirectSprites(BlockRenderView level, BlockPos pos, FluidState fluidStateIn) {
		FluidRendererHookContainer ctr = fabric_renderHandler.get();
		if (ctr.handler != null) {
			return new Sprite[] {
					ctr.sprites[0],
					ctr.sprites[1],
					ctr.hasOverlay ? ctr.overlay : null
			};
		}
		//noinspection UnstableApiUsage
		return ForgeHooksClient.getFluidSprites(level, pos, fluidStateIn);
	}
}