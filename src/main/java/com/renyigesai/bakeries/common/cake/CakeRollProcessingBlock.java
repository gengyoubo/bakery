package com.renyigesai.bakeries.common.cake;

import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.blocks.FacingBlock;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cut cake base on silicone paper. Add up to 4 plain foods (not blocks, no container, not in
 * #bakeries:not_cake_roll_ingredients), right-click empty-handed to roll it, again to get 2 cake rolls
 * that carry those foods' effects.
 */
public class CakeRollProcessingBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty ROLL = BooleanProperty.create("roll");
    public static final TagKey<Item> NOT_INGREDIENTS = TagKey.create(Registries.ITEM, new ResourceLocation("bakeries", "not_cake_roll_ingredients"));
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 3, 13);

    public CakeRollProcessingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ROLL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROLL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CakeRollProcessingBlockEntity(pos, state);
    }

    /** Breaking it returns the fillings (the paper and cake base come from the loot table). */
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CakeRollProcessingBlockEntity cake) {
            net.minecraft.world.Containers.dropContents(level, pos, cake.getItems());
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public static boolean isFilling(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().isEdible() && !(stack.getItem() instanceof BlockItem)
                && !stack.getItem().hasCraftingRemainingItem() && !stack.is(NOT_INGREDIENTS) && !stack.is(BakeriesItems.CAKE_ROLL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CakeRollProcessingBlockEntity cake)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            if (!level.isClientSide) {
                if (state.getValue(ROLL)) {
                    ItemStack rolls = CakeRollItem.create(cake.getItems(), 2);
                    cake.getItems().clear(); // rolled into the cake roll, don't drop them again in onRemove
                    level.removeBlock(pos, false);
                    level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, rolls, 0, 0, 0));
                } else {
                    level.setBlock(pos, state.setValue(ROLL, true), 3);
                }
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!state.getValue(ROLL) && isFilling(held)) {
            if (!level.isClientSide && cake.addItem(held)) {
                ItemUtils.shrink(held, 1, player);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
