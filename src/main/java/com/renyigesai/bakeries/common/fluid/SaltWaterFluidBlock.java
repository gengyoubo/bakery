package com.renyigesai.bakeries.common.fluid;

import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

/**
 * Liquid salt water. Random ticks above a lit heat source turn the source into a raw salt block,
 * matching the Forge behaviour.
 */
public class SaltWaterFluidBlock extends LiquidBlock {
    public SaltWaterFluidBlock() {
        super((net.minecraft.world.level.material.FlowingFluid) BakeriesFluids.SALT_WATER,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WATER)
                        .strength(100.0F)
                        .noCollission()
                        .noLootTable()
                        .pushReaction(PushReaction.DESTROY)
                        .sound(SoundType.EMPTY)
                        .replaceable());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        boolean heated = below.getBlock() instanceof AbstractFurnaceBlock
                && below.hasProperty(AbstractFurnaceBlock.LIT)
                && below.getValue(AbstractFurnaceBlock.LIT);
        if (!heated && below.is(Blocks.CAMPFIRE)) {
            heated = below.hasProperty(CampfireBlock.LIT) && below.getValue(CampfireBlock.LIT);
        }
        if (heated) {
            level.setBlock(pos, BakeriesBlocks.RAW_SALT_BLOCK.defaultBlockState(), 3);
        }
    }
}
