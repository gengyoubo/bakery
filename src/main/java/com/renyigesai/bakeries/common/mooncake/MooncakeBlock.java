package com.renyigesai.bakeries.common.mooncake;

import com.renyigesai.bakeries.capabilities.PlayerKeyAuxiliary;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.BakeriesSounds;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A pile of up to 4 mooncakes. Each layer is stored in the block entity, so the filling survives being placed:
 * right-click with a mooncake to add one, with an empty hand to take the top one back.
 */
public class MooncakeBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final IntegerProperty PILE = IntegerProperty.create("pile", 1, 4);
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 5, 14);

    public MooncakeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PILE, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PILE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MooncakeBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof MooncakeBlockEntity mooncakes)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        int pile = state.getValue(PILE);
        if (held.is(BakeriesItems.MOONCAKE)) {
            if (pile >= 4) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(PILE, pile + 1), 3);
                mooncakes.setItem(pile, held.copyWithCount(1));
                ItemUtils.shrink(held, 1, player);
                level.playSound(null, pos, BakeriesSounds.PASTRY_PLACE, SoundSource.PLAYERS, 0.8F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.isEmpty() && !PlayerKeyAuxiliary.isKeyDown(player.getUUID())) {
            if (!level.isClientSide) {
                ItemStack top = mooncakes.getItem(pile - 1);
                mooncakes.setItem(pile - 1, ItemStack.EMPTY);
                ItemUtils.givePlayerItem(player, top.isEmpty() ? new ItemStack(BakeriesItems.MOONCAKE) : top);
                if (pile == 1) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, state.setValue(PILE, pile - 1), 3);
                }
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof MooncakeBlockEntity mooncakes) {
            mooncakes.dropAll();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
