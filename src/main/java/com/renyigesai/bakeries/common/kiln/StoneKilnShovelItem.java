package com.renyigesai.bakeries.common.kiln;

import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Kiln peel. Use it towards a single dropped item to scoop it up, then use it on the stone kiln to slide it in
 * (and again to turn it over between stages). Shift-use on the kiln takes the item out;
 * shift-use in the air hands the carried item back to you.
 */
public class StoneKilnShovelItem extends ShovelItem {
    public StoneKilnShovelItem(Properties properties) {
        super(Tiers.WOOD, 1.5F, -3.0F, properties);
    }

    public static ItemStack getLoaded(ItemStack shovel) {
        return shovel.hasTag() && shovel.getTag().contains("Loaded") ? ItemStack.of(shovel.getTag().getCompound("Loaded")) : ItemStack.EMPTY;
    }

    public static void setLoaded(ItemStack shovel, ItemStack stack) {
        if (stack.isEmpty()) {
            if (shovel.hasTag()) {
                shovel.getTag().remove("Loaded");
            }
        } else {
            shovel.getOrCreateTag().put("Loaded", stack.save(new CompoundTag()));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player != null && player.isShiftKeyDown() && level.getBlockEntity(context.getClickedPos()) instanceof StoneKilnBlockEntity kiln
                && !kiln.isEmpty() && getLoaded(context.getItemInHand()).isEmpty()) {
            if (!level.isClientSide) {
                setLoaded(context.getItemInHand(), kiln.takeItem());
                level.playSound(null, context.getClickedPos(), SoundEvents.WOOL_STEP, SoundSource.BLOCKS, 0.8F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.getBlockEntity(context.getClickedPos()) instanceof StoneKilnBlockEntity) {
            return InteractionResult.PASS; // don't flatten grass paths etc. while working the kiln
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack shovel = player.getItemInHand(hand);
        ItemStack loaded = getLoaded(shovel);
        if (!loaded.isEmpty()) {
            if (player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    ItemUtils.givePlayerItem(player, loaded);
                    setLoaded(shovel, ItemStack.EMPTY);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8F, 0.8F);
                }
                return InteractionResultHolder.sidedSuccess(shovel, level.isClientSide);
            }
            return InteractionResultHolder.pass(shovel);
        }
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(shovel);
        }
        AABB area = new AABB(blockHit.getLocation(), blockHit.getLocation()).inflate(1.0);
        ItemEntity target = level.getEntitiesOfClass(ItemEntity.class, area, e -> e.getItem().getCount() == 1).stream().findFirst().orElse(null);
        if (target == null) {
            return InteractionResultHolder.pass(shovel);
        }
        if (!level.isClientSide) {
            setLoaded(shovel, target.getItem().copy());
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8F, 0.8F);
            target.discard();
        }
        return InteractionResultHolder.sidedSuccess(shovel, level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !getLoaded(stack).isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bakeries.stone_kiln_shovel.tips").withStyle(ChatFormatting.BLUE));
        ItemStack loaded = getLoaded(stack);
        if (!loaded.isEmpty()) {
            tooltip.add(Component.translatable("item.bakeries.containing.tips").withStyle(ChatFormatting.BLUE));
            tooltip.add(loaded.getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
