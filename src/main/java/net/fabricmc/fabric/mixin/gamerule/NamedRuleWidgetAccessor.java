package net.fabricmc.fabric.mixin.gamerule;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EditGameRulesScreen.NamedRuleWidget.class)
public interface NamedRuleWidgetAccessor {
    @Mutable
    @Accessor("name")
    void setName(List<OrderedText> name);
}
