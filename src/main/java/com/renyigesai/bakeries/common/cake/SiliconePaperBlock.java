package com.renyigesai.bakeries.common.cake;

import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.blocks.FacingBlock;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Silicone paper: the work surface for cakes.
 * A cut cake base turns it into a cake roll in progress; a cake-base part starts a custom cake.
 */
public class SiliconePaperBlock extends FacingBlock {
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 1, 15);

    public SiliconePaperBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (held.is(BakeriesItems.CUT_CAKE_BASE)) {
            if (!level.isClientSide) {
                level.setBlock(pos, BakeriesBlocks.CAKE_ROLL_PROCESSING.defaultBlockState().setValue(FACING, state.getValue(FACING)), 3);
                ItemUtils.shrink(held, 1, player);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Optional<CakePartData> part = CakeParts.find(held);
        if (part.isPresent() && CakeParts.CAKE_BASE.equals(part.get().type())) {
            level.setBlock(pos, BakeriesBlocks.CUSTOM_CAKE.defaultBlockState().setValue(FACING, state.getValue(FACING)), 3);
            if (level.getBlockEntity(pos) instanceof CustomCakeBlockEntity cake) {
                cake.addCakePart(part.get());
            }
            ItemUtils.shrink(held, 1, player);
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
