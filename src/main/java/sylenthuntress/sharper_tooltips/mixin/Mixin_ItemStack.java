package sylenthuntress.sharper_tooltips.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
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
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class Mixin_ItemStack {
    @Shadow public abstract ItemEnchantmentsComponent getEnchantments();

    @ModifyExpressionValue(
            method = "appendAttributeModifierTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;value()D"
            )
    )
    private double addSharpnessDamage(double original, Consumer<Text> textConsumer, @Nullable PlayerEntity player, RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
        var enchantments = this.getEnchantments();
        if (enchantments.isEmpty()) {
            return original;
        }

        if (modifier.idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID)) {
            for (var registryEntry : enchantments.getEnchantments()) {
                var enchantment = registryEntry.value();

                for (var effectEntry : enchantment.getEffect(EnchantmentEffectComponentTypes.DAMAGE)) {
                    if (effectEntry.requirements().isPresent()) {
                        continue;
                    }

                    original = effectEntry.effect().apply(
                            enchantments.getLevel(registryEntry),
                            player != null
                                    ? player.getRandom()
                                    : null,
                            (float) original
                    );
                }
            }
        }

        return original;
    }
}
