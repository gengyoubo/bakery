package com.renyigesai.bakeries.init.blocks;

import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesMobEffects;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/** Food / kitchen blocks ported from the Forge version. */
public final class FoodBlocks {
    private FoodBlocks() {
    }

    /** Decorative facing block with a fixed outline (tray of scones, bread holders). */
    public static class ShapedFacingBlock extends FacingBlock {
        private final VoxelShape shape;

        public ShapedFacingBlock(BlockBehaviour.Properties properties, VoxelShape shape) {
            super(properties);
            this.shape = shape;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return shape;
        }
    }

    /** Base for food placed on a solid block: pops off when the block below goes away. */
    public abstract static class SupportedFoodBlock extends FacingBlock {
        protected SupportedFoodBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return level.getBlockState(pos.below()).isSolid();
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
            return direction == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
    }

    /** Cream pumpkin pie: eaten in 4 bites straight from the block, each bite gives Enjoy for 30s. */
    public static class CreamPumpkinPieBlock extends SupportedFoodBlock {
        public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
        private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 2, 12);

        public CreamPumpkinPieBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.defaultBlockState().setValue(BITES, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(BITES);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!player.canEat(false)) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.getFoodData().eat(5, 3.0F);
            player.addEffect(new MobEffectInstance(BakeriesMobEffects.ENJOY, 600));
            level.gameEvent(player, GameEvent.EAT, pos);
            int bites = state.getValue(BITES);
            if (bites < 3) {
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            } else {
                level.removeBlock(pos, false);
            }
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
            return InteractionResult.CONSUME;
        }
    }

    /** Cream mushroom soup with baguette: two servings, then the bowl (flower pot) drops. */
    public static class CreamMushroomSoupBlock extends FacingBlock {
        public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 2);
        private static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12));

        public CreamMushroomSoupBlock(BlockBehaviour.Properties properties) {
            super(properties);
            this.registerDefaultState(this.defaultBlockState().setValue(BITES, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(BITES);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            int bites = state.getValue(BITES);
            if (bites >= 2) {
                level.destroyBlock(pos, true);
            } else {
                player.getFoodData().eat(9, 0.5F);
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
                level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.CONSUME;
        }

        @Override
        public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
            if (random.nextDouble() < 0.25) {
                level.addParticle(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            }
        }
    }

    /** A baked item still in its tin: demoulding gives {@link #getContent()} and leaves {@link #getEmptyMould()}. */
    public interface MouldContentBlock {
        Item getContent();

        Block getEmptyMould();
    }

    private static final TagKey<Item> BREAD_KNIVES = TagKey.create(Registries.ITEM, new ResourceLocation("bakeries", "bread_knife"));
    private static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "tools/knives"));

    public static boolean isKnife(ItemStack stack) {
        return stack.is(BREAD_KNIVES) || stack.is(KNIVES);
    }

    /**
     * Cake eaten slice by slice straight from the block (4 slices), like the Forge BCakeBlock:
     * each slice restores food and applies the cake's effects.
     */
    public static class BakeryCakeBlock extends SupportedFoodBlock {
        public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
        private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 8, 13);
        private final int nutrition;
        private final float saturation;
        private final Supplier<MobEffectInstance>[] effects;

        @SafeVarargs
        public BakeryCakeBlock(BlockBehaviour.Properties properties, int nutrition, float saturation, Supplier<MobEffectInstance>... effects) {
            super(properties);
            this.nutrition = nutrition;
            this.saturation = saturation;
            this.effects = effects;
            this.registerDefaultState(this.defaultBlockState().setValue(BITES, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(BITES);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide) {
                eat(state, level, pos, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        protected void eat(BlockState state, Level level, BlockPos pos, Player player) {
            player.awardStat(net.minecraft.stats.Stats.EAT_CAKE_SLICE);
            player.getFoodData().eat(nutrition, saturation);
            for (Supplier<MobEffectInstance> effect : effects) {
                player.addEffect(effect.get());
            }
            level.gameEvent(player, GameEvent.EAT, pos);
            int bites = state.getValue(BITES);
            if (bites < 3) {
                level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            } else {
                level.removeBlock(pos, false);
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean hasAnalogOutputSignal(BlockState state) {
            return true;
        }

        @Override
        @SuppressWarnings("deprecation")
        public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
            return state.getValue(BITES) + 1;
        }
    }

    /** Cream cake: right-click an uneaten one with another cream cake to build a multi-layer cake. */
    public static class CreamCakeBlock extends BakeryCakeBlock {
        private final Supplier<Block> multiLayer;

        @SafeVarargs
        public CreamCakeBlock(BlockBehaviour.Properties properties, Supplier<Block> multiLayer, int nutrition, float saturation, Supplier<MobEffectInstance>... effects) {
            super(properties, nutrition, saturation, effects);
            this.multiLayer = multiLayer;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(this.asItem()) && state.getValue(BITES) == 0) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, multiLayer.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
                    ItemUtils.shrink(held, 1, player);
                    level.playSound(null, pos, this.soundType.getPlaceSound(), SoundSource.PLAYERS, 0.8F, 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return super.use(state, level, pos, player, hand, hit);
        }
    }

    /** Two-layer cream cake: cut it with a knife into 8 cream cake cubes. */
    public static class MultiLayerCreamCakeBlock extends SupportedFoodBlock {
        private static final VoxelShape SHAPE = Block.box(3, 0, 3, 13, 13, 13);
        private final Supplier<Item> cube;

        public MultiLayerCreamCakeBlock(BlockBehaviour.Properties properties, Supplier<Item> cube) {
            super(properties);
            this.cube = cube;
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
            if (!isKnife(held)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.removeBlock(pos, false);
                net.minecraft.world.Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, new ItemStack(cube.get(), 8));
                if (!player.getAbilities().instabuild) {
                    held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    /** Baked cake still in the round mould (no facing). Right-click to take the cake; the empty mould stays. */
    public static class MouldCakeBlock extends Block implements MouldContentBlock {
        private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 7, 14);
        private final Supplier<Item> content;
        private final Supplier<Block> emptyMould;

        public MouldCakeBlock(BlockBehaviour.Properties properties, Supplier<Item> content, Supplier<Block> emptyMould) {
            super(properties);
            this.content = content;
            this.emptyMould = emptyMould;
        }

        @Override
        public Item getContent() {
            return content.get();
        }

        @Override
        public Block getEmptyMould() {
            return emptyMould.get();
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return level.getBlockState(pos.below()).isSolid();
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
            return direction == Direction.DOWN && !state.canSurvive(level, pos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide) {
                ItemUtils.givePlayerItem(player, new ItemStack(getContent()));
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
                level.setBlockAndUpdate(pos, getEmptyMould().defaultBlockState());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    /**
     * Stack of paper cups. Once there are 4, pour a cake paste bucket in to fill them;
     * taking from a filled stack gives paper cups of cake paste (ready for the oven).
     */
    public static class PaperCupBlock extends StateBlocks.FacingPileBlock {
        public static final BooleanProperty FILL = BooleanProperty.create("fill");
        private final Supplier<Item> pasteBucket;
        private final Supplier<Item> filledCup;

        public PaperCupBlock(BlockBehaviour.Properties properties, Supplier<Item> pasteBucket, Supplier<Item> filledCup) {
            super(properties, 4);
            this.pasteBucket = pasteBucket;
            this.filledCup = filledCup;
            this.registerDefaultState(this.defaultBlockState().setValue(FILL, false));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(FILL);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(pasteBucket.get())) {
                if (state.getValue(PILE) < getMaxPile() || state.getValue(FILL)) {
                    return InteractionResult.PASS;
                }
                if (!level.isClientSide) {
                    ItemUtils.shrink(held, 1, player);
                    ItemUtils.givePlayerItem(player, new ItemStack(net.minecraft.world.item.Items.BUCKET));
                    level.setBlock(pos, state.setValue(FILL, true), 3);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        protected boolean canStack(BlockState state) {
            return !state.getValue(FILL);
        }

        @Override
        protected ItemStack takenItem(BlockState state) {
            return state.getValue(FILL) ? new ItemStack(filledCup.get()) : new ItemStack(this.asItem());
        }
    }

    /**
     * Baked loaf still in its tin. Right-click to demould: you get the loaf and the empty mould stays behind.
     * Right-click with another one of the same to stack two.
     */
    public static class MouldToastBlock extends SupportedFoodBlock implements MouldContentBlock {
        public static final IntegerProperty PILE = IntegerProperty.create("pile", 1, 2);
        public static final BooleanProperty FILL = BooleanProperty.create("fill");
        private static final VoxelShape X_BOX = Block.box(6, 0, 4, 10, 5, 12);
        private static final VoxelShape Z_BOX = Block.box(4, 0, 6, 12, 5, 10);
        private static final VoxelShape PILE_BOX = Block.box(2, 0, 2, 14, 5, 14);
        private final Supplier<Item> content;

        public MouldToastBlock(BlockBehaviour.Properties properties, Supplier<Item> content) {
            super(properties);
            this.content = content;
            this.registerDefaultState(this.defaultBlockState().setValue(PILE, 1).setValue(FILL, true));
        }

        @Override
        public Item getContent() {
            return content.get();
        }

        @Override
        public Block getEmptyMould() {
            return BakeriesBlocks.MOULD;
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(PILE, FILL);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            if (state.getValue(PILE) > 1) {
                return PILE_BOX;
            }
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_BOX : Z_BOX;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack held = player.getItemInHand(hand);
            int pile = state.getValue(PILE);
            if (held.is(this.asItem())) {
                if (pile >= 2) {
                    return InteractionResult.FAIL;
                }
                if (!level.isClientSide) {
                    ItemUtils.shrink(held, 1, player);
                    level.setBlock(pos, state.setValue(PILE, pile + 1), 3);
                    level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.PLAYERS, 0.8F, 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide) {
                ItemUtils.givePlayerItem(player, new ItemStack(getContent()));
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
                if (pile > 1) {
                    // Forge did nothing on a stack of two; hand out the top tin's loaf and its mould instead.
                    ItemUtils.givePlayerItem(player, new ItemStack(BakeriesBlocks.MOULD));
                    level.setBlockAndUpdate(pos, state.setValue(PILE, pile - 1));
                } else {
                    level.setBlockAndUpdate(pos, BakeriesBlocks.MOULD.defaultBlockState().setValue(FACING, state.getValue(FACING)));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }
}
