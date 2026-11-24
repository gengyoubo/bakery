package com.renyigesai.bakeries.common.decoration;

import com.renyigesai.bakeries.init.blocks.FacingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Single-block glass cabinet door that opens on right click. Ported from Forge's glass cabinet door two.
 */
public class GlassCabinetDoorTwoBlock extends FacingBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 3);
    private static final VoxelShape SHAPE_EAST = Block.box(13, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 3, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 13, 16, 16, 16);
    private static final VoxelShape SHAPE_NORTH_OPEN = Block.box(0, 0, 0, 2, 16, 3);
    private static final VoxelShape SHAPE_EAST_OPEN = Block.box(13, 0, 0, 16, 16, 2);
    private static final VoxelShape SHAPE_WEST_OPEN = Block.box(0, 0, 14, 3, 16, 16);
    private static final VoxelShape SHAPE_SOUTH_OPEN = Block.box(14, 0, 13, 16, 16, 16);

    public GlassCabinetDoorTwoBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(OPEN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        boolean open = state.getValue(OPEN);
        return switch (state.getValue(FACING)) {
            case NORTH -> open ? SHAPE_NORTH_OPEN : SHAPE_NORTH;
            case EAST -> open ? SHAPE_EAST_OPEN : SHAPE_EAST;
            case WEST -> open ? SHAPE_WEST_OPEN : SHAPE_WEST;
            default -> open ? SHAPE_SOUTH_OPEN : SHAPE_SOUTH;
        };
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean open = !state.getValue(OPEN);
            level.setBlock(pos, state.setValue(OPEN, open), 3);
            level.playSound(null, pos, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE,
                    SoundSource.PLAYERS, 0.8F, 0.8F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
