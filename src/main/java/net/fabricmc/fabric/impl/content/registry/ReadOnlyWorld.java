package net.fabricmc.fabric.impl.content.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ReadOnlyWorld extends World {
    private final World wrapped;
    private final Scoreboard scoreboard;

    public ReadOnlyWorld(@NotNull World level) {
        super(null, null, level.getDimensionEntry(), null, level.isClient, false, 0, 1);
        this.wrapped = level;
        scoreboard = new Scoreboard();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return wrapped.getBlockState(pos);
    }

    @Override
    public void playSound(@Nullable PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed) {

    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed) {

    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

    }

    @Override
    public String asString() {
        return wrapped.asString();
    }

    @Nullable
    @Override
    public Entity getEntityById(int id) {
        return wrapped.getEntityById(id);
    }

    @Nullable
    @Override
    public MapState getMapState(String id) {
        return null;
    }

    @Override
    public void putMapState(String id, MapState state) {

    }

    @Override
    public int getNextMapId() {
        return 0;
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public RecipeManager getRecipeManager() {
        return wrapped.getRecipeManager();
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ChunkManager getChunkManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void emitGameEvent(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter) {

    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return 0;
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return wrapped.getPlayers();
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return wrapped.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return null;
    }
}