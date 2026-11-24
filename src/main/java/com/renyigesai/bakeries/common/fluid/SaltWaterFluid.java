package com.renyigesai.bakeries.common.fluid;

import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesFluids;
import com.renyigesai.bakeries.init.BakeriesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Salt water, ported from the Forge {@code SaltWaterFluid}. The two instances are registered in
 * {@link BakeriesFluids}; the block and bucket are looked up lazily so registration order does not matter.
 */
public abstract class SaltWaterFluid extends FlowingFluid {
    @Override
    public @NotNull Fluid getFlowing() {
        return BakeriesFluids.FLOWING_SALT_WATER;
    }

    @Override
    public @NotNull Fluid getSource() {
        return BakeriesFluids.SALT_WATER;
    }

    @Override
    public @NotNull Item getBucket() {
        return BakeriesItems.SALT_WATER_BUCKET;
    }

    @Override
    protected boolean canConvertToSource(@NotNull Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(@NotNull LevelReader level) {
        return 4;
    }

    @Override
    protected int getDropOff(@NotNull LevelReader level) {
        return 1;
    }

    @Override
    protected int getSpreadDelay(@NotNull Level level, @NotNull BlockPos pos, @NotNull FluidState current, @NotNull FluidState next) {
        return 5;
    }

    @Override
    public int getTickDelay(@NotNull LevelReader level) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public boolean isSame(@NotNull Fluid fluid) {
        return fluid == BakeriesFluids.SALT_WATER || fluid == BakeriesFluids.FLOWING_SALT_WATER;
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(@NotNull FluidState state) {
        return BakeriesBlocks.SALT_WATER_BLOCK == null
                ? net.minecraft.world.level.block.Blocks.WATER.defaultBlockState()
                : BakeriesBlocks.SALT_WATER_BLOCK.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean canBeReplacedWith(@NotNull FluidState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                     @NotNull Fluid fluid, @NotNull Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    public @NotNull Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends SaltWaterFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(@NotNull FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends SaltWaterFluid {
        @Override
        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}
