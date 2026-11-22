package com.renyigesai.bakeries.common.pizza;

import com.mojang.datafixers.util.Pair;
import com.renyigesai.bakeries.common.kiln.StoneKilnShovelItem;
import com.renyigesai.bakeries.init.blocks.FoodBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/** Raw pizza, baked pizzas (eaten in 4 slices) and the custom pizza. */
public final class PizzaBlocks {
    private static final VoxelShape PIZZA_SHAPE = Block.box(1, 0, 1, 15, 2, 15);

    private PizzaBlocks() {
    }

    /** Raw pizza on the counter: scoop it up with an empty kiln shovel. */
    public static class RawPizzaBlock extends Block {
        private final Supplier<Item> rawItem;

        public RawPizzaBlock(BlockBehaviour.Properties properties, Supplier<Item> rawItem) {
            super(properties);
            this.rawItem = rawItem;
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return PIZZA_SHAPE;
        }

        @Override
        @SuppressWarnings("deprecation")
        public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
            return level.getBlockState(pos.below()).isSolid();
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem() instanceof StoneKilnShovelItem && StoneKilnShovelItem.getLoaded(held).isEmpty()) {
                if (!level.isClientSide) {
                    StoneKilnShovelItem.setLoaded(held, new ItemStack(rawItem.get()));
                    level.removeBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.WOOL_STEP, SoundSource.BLOCKS, 0.8F, 0.8F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }
    }

    /** Baked pizza: four slices, each restoring a little food. */
    public static class PizzaBlock extends FoodBlocks.SupportedFoodBlock {
        public static final IntegerProperty SLICE = IntegerProperty.create("slice", 0, 3);
        private final int nutrition;
        private final float saturation;

        public PizzaBlock(BlockBehaviour.Properties properties, int nutrition, float saturation) {
            super(properties);
            this.nutrition = nutrition;
            this.saturation = saturation;
            this.registerDefaultState(this.defaultBlockState().setValue(SLICE, 0));
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            super.createBlockStateDefinition(builder);
            builder.add(SLICE);
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
            return PIZZA_SHAPE;
        }

        protected int nutrition(Level level, BlockPos pos) {
            return nutrition;
        }

        protected float saturation(Level level, BlockPos pos) {
            return saturation;
        }

        protected void applyEffects(Level level, BlockPos pos, Player player) {
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!level.isClientSide) {
                player.getFoodData().eat(nutrition(level, pos), saturation(level, pos));
                applyEffects(level, pos, player);
                level.gameEvent(player, GameEvent.EAT, pos);
                int slice = state.getValue(SLICE);
                if (slice < 3) {
                    level.setBlock(pos, state.setValue(SLICE, slice + 1), 3);
                } else {
                    level.removeBlock(pos, false);
                }
                level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    /** Custom pizza: slices use the toppings' average food value and apply the toppings' effects. */
    public static class CustomPizzaBlock extends PizzaBlock implements EntityBlock {
        public CustomPizzaBlock(BlockBehaviour.Properties properties) {
            super(properties, 4, 0.5F);
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new CustomPizzaBlockEntity(pos, state);
        }

        @Override
        protected int nutrition(Level level, BlockPos pos) {
            return level.getBlockEntity(pos) instanceof CustomPizzaBlockEntity pizza ? pizza.getNutrition() : 4;
        }

        @Override
        protected float saturation(Level level, BlockPos pos) {
            return level.getBlockEntity(pos) instanceof CustomPizzaBlockEntity pizza ? pizza.getSaturation() : 0.5F;
        }

        @Override
        protected void applyEffects(Level level, BlockPos pos, Player player) {
            if (!(level.getBlockEntity(pos) instanceof CustomPizzaBlockEntity pizza)) {
                return;
            }
            for (ItemStack topping : pizza.getToppings()) {
                FoodProperties food = topping.getItem().getFoodProperties();
                if (food == null) {
                    continue;
                }
                for (Pair<MobEffectInstance, Float> effect : food.getEffects()) {
                    if (effect.getFirst() != null && level.random.nextFloat() < effect.getSecond()) {
                        player.addEffect(new MobEffectInstance(effect.getFirst()));
                    }
                }
            }
        }
    }
}
