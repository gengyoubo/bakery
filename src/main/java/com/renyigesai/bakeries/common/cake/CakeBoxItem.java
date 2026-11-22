package com.renyigesai.bakeries.common.cake;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Cake box. Hold an empty box in the main hand and something in the off hand, then use it to pack
 * one of the off-hand item (anything not in #bakeries:not_cake_box_ingredients). Place it to show it off.
 */
public class CakeBoxItem extends BlockItem {
    public static final TagKey<Item> NOT_INGREDIENTS = TagKey.create(Registries.ITEM, new ResourceLocation("bakeries", "not_cake_box_ingredients"));

    public CakeBoxItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack getContent(ItemStack box) {
        if (!box.hasTag() || !box.getTag().contains("Inventory")) {
            return ItemStack.EMPTY;
        }
        return CakeRollProcessingBlockEntity.readInventory(box.getTag().getCompound("Inventory"), 1).get(0);
    }

    public static void setContent(ItemStack box, ItemStack content) {
        box.getOrCreateTag().put("Inventory", CakeRollProcessingBlockEntity.writeInventory(NonNullList.of(ItemStack.EMPTY, content)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack box = player.getItemInHand(hand);
        ItemStack offhand = player.getOffhandItem();
        if (hand == InteractionHand.MAIN_HAND && getContent(box).isEmpty() && !offhand.isEmpty()
                && !(offhand.getItem() instanceof CakeBoxItem) && !offhand.is(NOT_INGREDIENTS)) {
            if (!level.isClientSide) {
                setContent(box, offhand.copyWithCount(1));
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
            }
            return InteractionResultHolder.sidedSuccess(box, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack content = getContent(context.getItemInHand()).copy();
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !content.isEmpty()
                && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CakeBoxBlockEntity box) {
            box.setContent(content);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bakeries.cake_box.tips").withStyle(ChatFormatting.BLUE));
        ItemStack content = getContent(stack);
        if (!content.isEmpty()) {
            tooltip.add(Component.translatable("item.bakeries.containing.tips").withStyle(ChatFormatting.BLUE));
            tooltip.add(content.getHoverName().copy().withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
