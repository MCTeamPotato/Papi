package net.fabricmc.fabric.impl.recipe.ingredient;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;

public class RecipeForgeImpl {
    public static void registerRecipeSerializers(@NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(CustomIngredientInit::init);
    }
}
