package com.renyigesai.bakeries.common.pizza;

import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Baked custom pizza: carries its toppings ("Inventory", 4 slots) and places them into the block. */
public class CustomPizzaItem extends BlockItem {
    public static final int SLOTS = 4;

    public CustomPizzaItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static NonNullList<ItemStack> getToppings(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("Inventory")) {
            return NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        }
        return CakeRollProcessingBlockEntity.readInventory(stack.getTag().getCompound("Inventory"), SLOTS);
    }

    public static void setToppings(ItemStack stack, NonNullList<ItemStack> toppings) {
        if (toppings.stream().anyMatch(s -> !s.isEmpty())) {
            stack.getOrCreateTag().put("Inventory", CakeRollProcessingBlockEntity.writeInventory(toppings));
        }
    }

    /** Forge formula: average topping nutrition + 4, average saturation + 0.5. */
    public static int[] foodValues(List<ItemStack> toppings, float[] saturationOut) {
        int nutrition = 0;
        float saturation = 0.0F;
        int count = 0;
        for (ItemStack stack : toppings) {
            FoodProperties food = stack.getItem().getFoodProperties();
            if (food != null) {
                nutrition += food.getNutrition();
                saturation += food.getSaturationModifier();
                count++;
            }
        }
        int divisor = Math.max(1, count);
        saturationOut[0] = saturation / divisor + 0.5F;
        return new int[]{nutrition / divisor + 4};
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        NonNullList<ItemStack> toppings = getToppings(context.getItemInHand());
        InteractionResult result = super.place(context);
        if (result.consumesAction() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CustomPizzaBlockEntity pizza) {
            pizza.setToppings(toppings);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        appendToppings(stack, tooltip);
    }

    public static void appendToppings(ItemStack stack, List<Component> tooltip) {
        tooltip.add(Component.translatable("item.bakeries.custom_containing.tips").withStyle(ChatFormatting.BLUE));
        for (ItemStack topping : getToppings(stack)) {
            if (!topping.isEmpty()) {
                tooltip.add(topping.getHoverName().copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
