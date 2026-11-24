package com.renyigesai.bakeries.common.decoration;

import com.renyigesai.bakeries.init.blocks.FacingBlock;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Magnetic plate: sticks to a wall and holds up to two small items (and can display a block texture).
 */
public class MagneticPlateBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty CONTENT = BooleanProperty.create("content");

    public MagneticPlateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONTENT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONTENT);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.box(0, 0, 0, 1, 1, 1.0D / 16.0D);
            case EAST -> Shapes.box(15.0D / 16.0D, 0, 0, 1, 1, 1);
            case WEST -> Shapes.box(0, 0, 0, 1.0D / 16.0D, 1, 1);
            default -> Shapes.box(0, 0, 15.0D / 16.0D, 1, 1, 1);
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MagneticPlateBlockEntity plate)) {
            return super.use(state, level, pos, player, hand, hit);
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            for (int slot = 0; slot < plate.getItems().size(); slot++) {
                ItemStack stored = plate.getItems().get(slot);
                if (!stored.isEmpty()) {
                    ItemUtils.givePlayerItem(player, stored.copy());
                    plate.getItems().set(slot, ItemStack.EMPTY);
                    plate.update();
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.PASS;
        }
        if (held.getItem() instanceof BlockItem blockItem) {
            plate.setBlockId(BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString());
            level.setBlock(pos, state.setValue(CONTENT, true), 3);
            plate.update();
            level.playSound(null, pos, blockItem.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        }
        float[] uv = this.getSlotFromHit(hit.getLocation(), pos, state.getValue(FACING), hit.getDirection());
        for (int slot = 0; slot < plate.getItems().size(); slot++) {
            if (plate.getItems().get(slot).isEmpty()) {
                if (slot == 0) {
                    plate.setXyo0(uv);
                } else {
                    plate.setXyo1(uv);
                }
                plate.getItems().set(slot, held.copy());
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                plate.update();
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.CONSUME;
    }

    private float[] getSlotFromHit(Vec3 hitPos, BlockPos pos, Direction facing, Direction hitFace) {
        if (hitFace == facing) {
            double relX = hitPos.x - pos.getX();
            double relY = hitPos.y - pos.getY();
            double relZ = hitPos.z - pos.getZ();
            float u;
            float v = (float) relY;
            switch (facing) {
                case NORTH -> u = 1.0F - (float) relX;
                case EAST -> u = 1.0F - (float) relZ;
                case WEST -> u = (float) relZ;
                case SOUTH -> u = (float) relX;
                default -> {
                    return new float[]{0.0F, 0.0F};
                }
            }
            return new float[]{u, v};
        }
        return new float[]{0.0F, 0.0F};
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MagneticPlateBlockEntity plate) {
                plate.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MagneticPlateBlockEntity(pos, state);
    }
}
