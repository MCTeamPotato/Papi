package net.fabricmc.fabric.mixin.recipe.ingredient;

import com.google.common.collect.BiMap;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingHelper.class)
public interface CraftingHelperAccessor {

    @Accessor
    static BiMap<Identifier, IIngredientSerializer<?>> getIngredients() {
        throw new UnsupportedOperationException();
    }
}