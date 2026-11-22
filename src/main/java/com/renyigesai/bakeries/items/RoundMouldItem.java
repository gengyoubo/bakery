package com.renyigesai.bakeries.items;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Reusable round mould: stays in the crafting grid after use, losing one durability each time. */
public class RoundMouldItem extends Item implements FabricItem {
    public RoundMouldItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        ItemStack remainder = stack.copyWithCount(1);
        remainder.setDamageValue(stack.getDamageValue() + 1);
        return remainder.getDamageValue() >= remainder.getMaxDamage() ? ItemStack.EMPTY : remainder;
    }
}
