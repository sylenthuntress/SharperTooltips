package sylenthuntress.sharper_tooltips.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack {
    @Shadow public abstract ItemEnchantmentsComponent getEnchantments();
    @Unique private static ItemStack savedStack;

    @Inject(
            method = "appendAttributeModifiersTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;applyAttributeModifier(Lnet/minecraft/component/type/AttributeModifierSlot;Lorg/apache/commons/lang3/function/TriConsumer;)V"
            )
    )
    private void saveStack(Consumer<Text> textConsumer, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, CallbackInfo ci) {
        savedStack = (ItemStack) (Object) this;
    }

    @WrapOperation(
            method = "method_57370",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/component/type/AttributeModifiersComponent$Display;addTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/attribute/EntityAttributeModifier;)V"
            )
    )
    private static void addSharpnessDamage(AttributeModifiersComponent.Display instance,
                                           Consumer<Text> textConsumer,
                                           @Nullable PlayerEntity player,
                                           RegistryEntry<EntityAttribute> attribute,
                                           EntityAttributeModifier modifier,
                                           Operation<Void> original) {
        if (savedStack == null) {
            original.call(instance, textConsumer, player, attribute, modifier);
            return;
        }

        var enchantments = savedStack.getEnchantments();
        savedStack = null;

        if (enchantments.isEmpty() || !modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
            original.call(instance, textConsumer, player, attribute, modifier);
            return;
        }

        double value = modifier.value();
        for (var registryEntry : enchantments.getEnchantments()) {
            var enchantment = registryEntry.value();

            for (var effectEntry : enchantment.getEffect(EnchantmentEffectComponentTypes.DAMAGE)) {
                if (effectEntry.requirements().isPresent()) {
                    continue;
                }

                value = effectEntry.effect().apply(
                        enchantments.getLevel(registryEntry),
                        player != null
                                ? player.getRandom()
                                : null,
                        (float) value
                );
            }
        }

        original.call(instance,
                textConsumer,
                player,
                attribute,
                new EntityAttributeModifier(
                        modifier.id(),
                        value,
                        modifier.operation()
                )
        );
    }
}
