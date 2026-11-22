package com.renyigesai.bakeries.common.mooncake;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.items.FoodEffectTooltip;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Baked mooncake: eating it also eats every filling (their food and effects apply), right-clicking another player
 * gives them one, and placing it keeps the filling in the pile's block entity.
 * The fillings are stored as "ItemId" = "&ns:a&ns:b" (same NBT as Forge).
 */
public class MooncakeItem extends BlockItem {
    public static final String FILLINGS = "ItemId";
    private static boolean eating;
    private final FoodEffectTooltip.Entry[] effects;

    public MooncakeItem(Block block, Properties properties, FoodEffectTooltip.Entry... effects) {
        super(block, properties);
        this.effects = effects;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack one = context.getItemInHand().copyWithCount(1);
        InteractionResult result = super.place(context);
        if (result.consumesAction() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MooncakeBlockEntity mooncakes) {
            mooncakes.setItem(0, one);
        }
        return result;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (!level.isClientSide && !eating) {
            eating = true;
            try {
                for (Item filling : getFillings(stack)) {
                    try {
                        filling.finishUsingItem(new ItemStack(filling), level, living);
                    } catch (Exception e) {
                        BakeriesMod.LOGGER.error("Failed to apply mooncake filling {}", BuiltInRegistries.ITEM.getKey(filling), e);
                    }
                }
            } finally {
                eating = false;
            }
        }
        return super.finishUsingItem(stack, level, living);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof Player receiver) {
            if (!player.level().isClientSide) {
                ItemUtils.givePlayerItem(receiver, stack.copyWithCount(1));
                ItemUtils.shrink(stack, 1, player);
                receiver.displayClientMessage(Component.translatable("tip.bakeries.give_mooncake", player.getName().getString()), true);
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        appendFillings(stack, tooltip);
        FoodEffectTooltip.append(tooltip, effects);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public static void appendFillings(ItemStack stack, List<Component> tooltip) {
        StringBuilder names = new StringBuilder();
        for (Item filling : getFillings(stack)) {
            names.append(filling.getDescription().getString());
        }
        if (!names.isEmpty()) {
            tooltip.add(Component.translatable("tip.bakeries.mooncake", names.toString()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    public static List<Item> getFillings(ItemStack stack) {
        List<Item> fillings = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(FILLINGS)) {
            return fillings;
        }
        for (String id : tag.getString(FILLINGS).split("&")) {
            ResourceLocation key = ResourceLocation.tryParse(id);
            if (!id.isEmpty() && key != null) {
                Item item = BuiltInRegistries.ITEM.get(key);
                if (item != Items.AIR) {
                    fillings.add(item);
                }
            }
        }
        return fillings;
    }

    public static void setFillings(ItemStack stack, List<Item> fillings) {
        StringBuilder ids = new StringBuilder();
        for (Item item : fillings) {
            ids.append('&').append(BuiltInRegistries.ITEM.getKey(item));
        }
        stack.getOrCreateTag().putString(FILLINGS, ids.toString());
    }

    /** Copies the fillings of a raw mooncake onto its baked result. */
    public static ItemStack copyFillings(ItemStack from, ItemStack to) {
        CompoundTag tag = from.getTag();
        if (tag != null && tag.contains(FILLINGS)) {
            to.getOrCreateTag().putString(FILLINGS, tag.getString(FILLINGS));
        }
        return to;
    }

    /** Raw mooncake: a plain item (not a second block item of the mooncake block), baked in the oven. */
    public static class Raw extends Item {
        public Raw(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tip.bakeries.raw_mooncake").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            appendFillings(stack, tooltip);
            tooltip.add(Component.literal("Min170°C").withStyle(ChatFormatting.BLUE));
        }
    }
}
