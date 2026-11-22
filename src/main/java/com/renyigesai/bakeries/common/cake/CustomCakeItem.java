package com.renyigesai.bakeries.common.cake;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

/** Finished custom cake: eat it in 4 bites, or place it back down (only while uneaten). */
public class CustomCakeItem extends BlockItem {
    private static final int BITES = 4;

    public CustomCakeItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static List<String> getPartIds(ItemStack stack) {
        return stack.hasTag() ? CustomCakeBlockEntity.splitPartIds(stack.getTag().getString("PartId")) : List.of();
    }

    private static boolean isStarted(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("EatCount") && stack.getTag().getInt("EatCount") < BITES;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (isStarted(context.getItemInHand())) {
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (getPartIds(stack).isEmpty()) {
            return InteractionResult.FAIL;
        }
        CompoundTag tag = stack.getTag().copy();
        InteractionResult result = super.place(context);
        if (result.consumesAction() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CustomCakeBlockEntity cake) {
            cake.readCake(tag);
            cake.update();
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getPartIds(stack).isEmpty() || !player.canEat(true)) {
            return InteractionResultHolder.pass(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        CompoundTag tag = stack.getOrCreateTag();
        if (living instanceof Player player) {
            player.getFoodData().eat(tag.getInt("Hunger") / BITES, tag.getFloat("Saturation") / BITES);
            player.awardStat(Stats.ITEM_USED.get(this));
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            }
        }
        if (!level.isClientSide) {
            for (MobEffectInstance effect : CakeParts.applyRules(CustomCakeBlockEntity.readEffects(tag))) {
                living.addEffect(effect);
            }
        }
        level.playSound(null, living.getX(), living.getY(), living.getZ(), living.getEatingSound(stack), net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
        if (living instanceof Player player && player.getAbilities().instabuild) {
            return stack;
        }
        int left = (tag.contains("EatCount") ? tag.getInt("EatCount") : BITES) - 1;
        if (left <= 0) {
            stack.shrink(1);
        } else {
            tag.putInt("EatCount", left);
            tag.putInt("EatCountMax", BITES);
        }
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.hasTag() && !stack.getTag().getString("Name").isEmpty()) {
            return Component.literal(stack.getTag().getString("Name"));
        }
        return super.getName(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isStarted(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Mth.clamp(Math.round(13.0F * stack.getOrCreateTag().getInt("EatCount") / BITES), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 15574564;
    }

}
