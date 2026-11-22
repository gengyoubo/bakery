package com.renyigesai.bakeries.items;

import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** Placeable food that hands back its container when eaten (e.g. cup cake -> paper cup). */
public class ContainerFoodBlockItem extends FoodEffectBlockItem {
    private final Supplier<Item> container;

    public ContainerFoodBlockItem(Block block, Properties properties, Supplier<Item> container, FoodEffectTooltip.Entry... effects) {
        super(block, properties, effects);
        this.container = container;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity eater) {
        ItemStack result = super.finishUsingItem(stack, level, eater);
        if (!level.isClientSide && eater instanceof Player player && !player.getAbilities().instabuild) {
            ItemUtils.givePlayerItem(player, new ItemStack(container.get()));
        }
        return result;
    }
}
