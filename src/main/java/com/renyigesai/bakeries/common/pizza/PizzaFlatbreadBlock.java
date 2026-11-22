package com.renyigesai.bakeries.common.pizza;

import com.renyigesai.bakeries.common.kiln.StoneKilnShovelItem;
import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.BakeriesParticleTypes;
import com.renyigesai.bakeries.init.blocks.FoodBlocks;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pizza flatbread: spread a tomato on it, add up to 4 edible toppings, then a cheese.
 * Scoop the finished pizza with an empty kiln shovel (no toppings = plain raw pizza, otherwise a raw custom pizza);
 * a plain pizza can also be finished by right-clicking, which turns it into a raw pizza block.
 */
public class PizzaFlatbreadBlock extends FoodBlocks.SupportedFoodBlock implements EntityBlock {
    public static final BooleanProperty SAUCE = BooleanProperty.create("sauce");
    public static final TagKey<Item> TOMATOES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "crops/tomato"));
    public static final TagKey<Item> CHEESES = TagKey.create(Registries.ITEM, new ResourceLocation("c", "cheese"));
    public static final TagKey<Item> NOT_INGREDIENTS = TagKey.create(Registries.ITEM, new ResourceLocation("bakeries", "not_pizza_ingredients"));
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 2, 14);

    public PizzaFlatbreadBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SAUCE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SAUCE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaFlatbreadBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof PizzaFlatbreadBlockEntity pizza)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(SAUCE) && pizza.hasCheese()) {
            // finished: scoop it, or bake it into a plain raw pizza block
            if (held.getItem() instanceof StoneKilnShovelItem && StoneKilnShovelItem.getLoaded(held).isEmpty()) {
                StoneKilnShovelItem.setLoaded(held, rawPizza(pizza));
                pizza.clear();
                level.removeBlock(pos, false);
                level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 0.8F, 0.8F);
            } else if (!pizza.hasToppings()) {
                pizza.clear();
                level.setBlockAndUpdate(pos, BakeriesBlocks.RAW_PIZZA.defaultBlockState());
                level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 0.8F, 0.8F);
            } else {
                player.displayClientMessage(Component.translatable("tip.bakeries.pizza_stone_kiln_shovel"), true);
            }
            return InteractionResult.CONSUME;
        }
        if (!state.getValue(SAUCE)) {
            if (!held.is(TOMATOES)) {
                player.displayClientMessage(Component.translatable("tip.bakeries.pizza_tomato"), true);
                return InteractionResult.PASS;
            }
            ItemUtils.shrink(held, 1, player);
            level.scheduleTick(pos, this, 8);
            level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 0.8F, 0.8F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(BakeriesParticleTypes.TOMATO_SAUCE, pos.getX() + 0.5, pos.getY() + 0.063, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }
            return InteractionResult.CONSUME;
        }
        if (held.is(CHEESES)) {
            pizza.setCheese(held);
            ItemUtils.shrink(held, 1, player);
            level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 0.8F, 0.8F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.WAX_ON, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 32, 0.5, 0.5, 0.5, 0.0025);
            }
            return InteractionResult.CONSUME;
        }
        if (!held.isEmpty() && held.getItem().isEdible() && !held.is(NOT_INGREDIENTS) && pizza.addTopping(held)) {
            if (held.getItem().hasCraftingRemainingItem()) {
                ItemUtils.givePlayerItem(player, new ItemStack(held.getItem().getCraftingRemainingItem()));
            }
            ItemUtils.shrink(held, 1, player);
            level.playSound(null, pos, SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 0.8F, 0.8F);
            return InteractionResult.CONSUME;
        }
        player.displayClientMessage(Component.translatable("tip.bakeries.pizza_cheese"), true);
        return InteractionResult.PASS;
    }

    private static ItemStack rawPizza(PizzaFlatbreadBlockEntity pizza) {
        if (!pizza.hasToppings()) {
            return new ItemStack(BakeriesItems.RAW_PIZZA);
        }
        ItemStack raw = new ItemStack(BakeriesItems.RAW_CUSTOM_PIZZA);
        NonNullList<ItemStack> toppings = NonNullList.withSize(CustomPizzaItem.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < toppings.size(); i++) {
            toppings.set(i, pizza.getToppings().get(i).copy());
        }
        CustomPizzaItem.setToppings(raw, toppings);
        return raw;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(SAUCE)) {
            level.setBlock(pos, state.setValue(SAUCE, true), 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PizzaFlatbreadBlockEntity pizza) {
            for (ItemStack topping : pizza.getToppings()) {
                if (!topping.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), topping);
                }
            }
            if (pizza.hasCheese()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), pizza.getCheese());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
