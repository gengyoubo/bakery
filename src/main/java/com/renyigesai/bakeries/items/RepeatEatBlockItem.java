package com.renyigesai.bakeries.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Food that takes several servings to finish (e.g. the long egg tart); the bar shows what is left. */
public class RepeatEatBlockItem extends FoodEffectBlockItem {
    private static final String EATEN = "Eaten";
    private final int servings;

    public RepeatEatBlockItem(Block block, Properties properties, int servings, FoodEffectTooltip.Entry... effects) {
        super(block, properties.stacksTo(1), effects);
        this.servings = servings;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity eater) {
        // Apply nutrition and effects on a throw-away copy so the real stack is not consumed.
        eater.eat(level, stack.copy());
        if (eater instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }
        int eaten = stack.getOrCreateTag().getInt(EATEN) + 1;
        if (eaten >= servings) {
            stack.shrink(1);
        } else {
            stack.getTag().putInt(EATEN, eaten);
        }
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getInt(EATEN) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int left = servings - (stack.hasTag() ? stack.getTag().getInt(EATEN) : 0);
        return Math.round(13.0F * left / servings);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(0.1F, 0.7F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int left = servings - (stack.hasTag() ? stack.getTag().getInt(EATEN) : 0);
        tooltip.add(Component.translatable("item.bakeries.tips.repeat_eat_item").append(" " + left + "/" + servings).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
