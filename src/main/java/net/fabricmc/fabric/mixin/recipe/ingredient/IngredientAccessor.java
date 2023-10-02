package net.fabricmc.fabric.mixin.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Ingredient.class)
public interface IngredientAccessor {
    @Accessor("matchingStacks")
    ItemStack[] getMatchingStacks();

    @Accessor("matchingStacks")
    void setMatchingStacks(ItemStack[] newMatchingStacks);
}
