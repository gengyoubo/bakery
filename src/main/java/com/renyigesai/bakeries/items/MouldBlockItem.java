package com.renyigesai.bakeries.items;

import com.renyigesai.bakeries.init.blocks.FoodBlocks;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Baked loaf in its tin. With a knife in the off hand, hold use to demould it straight from the inventory. */
public class MouldBlockItem extends BlockItem {
    public MouldBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && getBlock() instanceof FoodBlocks.MouldContentBlock && FoodBlocks.isKnife(player.getOffhandItem())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && getBlock() instanceof FoodBlocks.MouldContentBlock mould) {
            if (!level.isClientSide) {
                ItemUtils.givePlayerItem(player, new ItemStack(mould.getEmptyMould()));
                ItemUtils.givePlayerItem(player, new ItemStack(mould.getContent()));
                level.playSound(null, player.blockPosition(), SoundEvents.LANTERN_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                player.getOffhandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.OFFHAND));
            }
            player.getCooldowns().addCooldown(this, 10);
        }
        return stack;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bakeries.tips.mould").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
