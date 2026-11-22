package com.renyigesai.bakeries.init.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Side-by-side connecting block (bread racks, coffee table, sofas), ported from the Forge version.
 * Neighbours on the left/right with the same facing join up into LEFT / RIGHT / ALL shapes.
 * Placing while sneaking marks the block SINGLE: it never connects and others won't connect to it.
 */
public class HorizontalConnectBlock extends FacingBlock {
    public static final EnumProperty<RackType> TYPE = EnumProperty.create("type", RackType.class);
    public static final BooleanProperty SINGLE = BooleanProperty.create("single");

    public HorizontalConnectBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, RackType.SINGLE).setValue(SINGLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE, SINGLE);
    }

    /** Which neighbours count as "the same kind" for connecting. */
    public boolean matchConnect(BlockState state) {
        return state.is(this);
    }

    protected Direction getLeft(Direction facing) {
        return facing.getCounterClockWise();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        if (context.isSecondaryUseActive()) {
            return state.setValue(TYPE, RackType.SINGLE).setValue(SINGLE, true);
        }
        return state.setValue(SINGLE, false).setValue(TYPE, computeType(context.getLevel(), context.getClickedPos(), state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (!updated.is(this) || updated.getValue(SINGLE)) {
            return updated;
        }
        Direction left = getLeft(updated.getValue(FACING));
        if (direction == left || direction == left.getOpposite()) {
            return updated.setValue(TYPE, computeType(level, pos, updated.getValue(FACING)));
        }
        return updated;
    }

    private RackType computeType(BlockGetter level, BlockPos pos, Direction facing) {
        boolean left = canConnectTo(level, pos, facing, false);
        boolean right = canConnectTo(level, pos, facing, true);
        if (left && right) {
            return RackType.ALL;
        }
        if (left) {
            return RackType.LEFT;
        }
        return right ? RackType.RIGHT : RackType.SINGLE;
    }

    protected boolean canConnectTo(BlockGetter level, BlockPos pos, Direction facing, boolean isRight) {
        Direction left = getLeft(facing);
        BlockState neighbor = level.getBlockState(pos.relative(isRight ? left.getOpposite() : left));
        return matchConnect(neighbor)
                && neighbor.hasProperty(SINGLE) && !neighbor.getValue(SINGLE)
                && neighbor.getValue(FACING) == facing;
    }
}
