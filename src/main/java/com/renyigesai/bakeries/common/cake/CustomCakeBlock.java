package com.renyigesai.bakeries.common.cake;

import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.blocks.FacingBlock;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Custom cake under construction. Right-click with a cake part to add it (one per type),
 * a candle to put it on, flint and steel to light it, an empty hand to blow it out, a name tag to name it.
 * Breaking it gives a custom cake item that carries the whole cake.
 */
public class CustomCakeBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty CANDLE_LIT = BooleanProperty.create("candle_lit");
    private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 6, 13);

    public CustomCakeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CANDLE_LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CANDLE_LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomCakeBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CustomCakeBlockEntity cake)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof NameTagItem && held.hasCustomHoverName()) {
            if (!level.isClientSide) {
                cake.setName(held.getHoverName().getString());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractCandleBlock && !cake.hasCandle()) {
            if (!level.isClientSide) {
                cake.setCandleId(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString());
                ItemUtils.shrink(held, 1, player);
                level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.getItem() instanceof FlintAndSteelItem && cake.hasCandle() && !state.getValue(CANDLE_LIT)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(CANDLE_LIT, true), 3);
                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.isEmpty() && cake.hasCandle() && state.getValue(CANDLE_LIT)) {
            if (!level.isClientSide) {
                extinguish(player, state, level, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            // the part list lives on the server; let it decide
            return held.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
        }
        Optional<CakePartData> part = CakeParts.find(held);
        if (part.isPresent() && cake.canAdd(part.get())) {
            cake.addCakePart(part.get());
            ItemUtils.shrink(held, 1, player);
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CustomCakeBlockEntity cake) {
                ItemStack stack = new ItemStack(BakeriesItems.CUSTOM_CAKE);
                cake.writeCake(stack.getOrCreateTag());
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                if (cake.hasCandle()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(cake.candleBlock()));
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(CANDLE_LIT)) {
            return;
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.8125;
        double z = pos.getZ() + 0.5;
        float f = random.nextFloat();
        if (f < 0.3F) {
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
            if (f < 0.17F) {
                level.playLocalSound(x + 0.5, y + 0.5, z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS,
                        1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
        level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.0, 0.0);
    }

    private void extinguish(@Nullable Player player, BlockState state, Level level, BlockPos pos) {
        level.setBlock(pos, state.setValue(CANDLE_LIT, false), 3);
        level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.8125, pos.getZ() + 0.5, 0.0, 0.1, 0.0);
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }
}
