package net.fabricmc.fabric.mixin.papi.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public abstract class ForgeHooksMixin {
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private static void hookGetAttributeModifiers(ItemStack item, EquipmentSlot slot, Multimap<EntityAttribute, EntityAttributeModifier> attributes, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> cir) {
        Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = LinkedHashMultimap.create(((FabricItem)item.getItem()).getAttributeModifiers(item, slot));
        ModifyItemAttributeModifiersCallback.EVENT.invoker().modifyAttributeModifiers(item, slot, attributeModifiers);
        cir.setReturnValue(attributeModifiers);
    }
}
