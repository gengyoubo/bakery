package com.renyigesai.bakeries.common.cake;

import com.mojang.datafixers.util.Pair;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.items.FoodEffectItem;
import com.renyigesai.bakeries.items.FoodEffectTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Cake roll: on top of its own food value, applies the effects of the fillings rolled into it. */
public class CakeRollItem extends FoodEffectItem {
    public CakeRollItem(Properties properties, FoodEffectTooltip.Entry... effects) {
        super(properties, effects);
    }

    public static ItemStack create(NonNullList<ItemStack> fillings, int count) {
        ItemStack roll = new ItemStack(BakeriesItems.CAKE_ROLL, count);
        if (fillings.stream().anyMatch(s -> !s.isEmpty())) {
            roll.getOrCreateTag().put("Inventory", CakeRollProcessingBlockEntity.writeInventory(fillings));
            StringBuilder name = new StringBuilder();
            for (ItemStack filling : fillings) {
                if (!filling.isEmpty()) {
                    name.append(filling.getHoverName().getString());
                }
            }
            name.append(roll.getItem().getName(roll).getString());
            roll.setHoverName(Component.literal(name.toString()));
        }
        return roll;
    }

    public static List<ItemStack> getFillings(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("Inventory")) {
            return List.of();
        }
        return CakeRollProcessingBlockEntity.readInventory(stack.getTag().getCompound("Inventory"), CakeRollProcessingBlockEntity.SLOTS)
                .stream().filter(s -> !s.isEmpty()).toList();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            for (ItemStack filling : getFillings(stack)) {
                FoodProperties food = filling.getItem().getFoodProperties();
                if (food == null) {
                    continue;
                }
                for (Pair<MobEffectInstance, Float> effect : food.getEffects()) {
                    if (effect.getFirst() != null && level.random.nextFloat() < effect.getSecond()) {
                        entity.addEffect(new MobEffectInstance(effect.getFirst()));
                    }
                }
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        List<ItemStack> fillings = getFillings(stack);
        if (!fillings.isEmpty()) {
            tooltip.add(Component.translatable("item.bakeries.custom_containing.tips").withStyle(ChatFormatting.BLUE));
            for (ItemStack filling : fillings) {
                tooltip.add(filling.getHoverName().copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
