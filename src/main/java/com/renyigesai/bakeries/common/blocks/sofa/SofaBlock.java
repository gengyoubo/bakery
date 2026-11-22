package com.renyigesai.bakeries.common.blocks.sofa;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.init.blocks.HorizontalConnectBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SofaBlock extends HorizontalConnectBlock {
    private static final TagKey<Block> SOFAS = TagKey.create(Registries.BLOCK, new ResourceLocation(BakeriesMod.MODID, "sofa"));
    private static final String SOFA_SEAT_TAG = "bakeries_sofa_seat";
    private static final String SOFA_SEAT_POS_TAG_PREFIX = "bakeries_sofa_pos:";
    private static final VoxelShape SOFA_SHAPE = box(0, 0, 0, 16, 10, 16);

    public SofaBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /** Any sofa (all colours, #bakeries:sofa) connects, like the Forge version. */
    @Override
    public boolean matchConnect(BlockState state) {
        return state.is(SOFAS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SOFA_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SOFA_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        String posTag = SOFA_SEAT_POS_TAG_PREFIX + pos.getX() + "," + pos.getY() + "," + pos.getZ();
        AABB seatArea = new AABB(
                pos.getX() + 0.35D, pos.getY() - 1.6D, pos.getZ() + 0.35D,
                pos.getX() + 0.65D, pos.getY() + 0.4D, pos.getZ() + 0.65D
        );
        List<ArmorStand> seats = serverLevel.getEntitiesOfClass(ArmorStand.class, seatArea, seat ->
                seat.getTags().contains(SOFA_SEAT_TAG) && seat.getTags().contains(posTag)
        );
        ArmorStand seat = null;
        for (ArmorStand existing : seats) {
            if (existing.getPassengers().isEmpty()) {
                seat = existing;
                break;
            }
        }

        if (seat == null) {
            seat = new ArmorStand(level, pos.getX() + 0.5D, pos.getY() - 1.22D, pos.getZ() + 0.5D);
            seat.setInvisible(true);
            seat.setNoGravity(true);
            seat.setInvulnerable(true);
            seat.setSilent(true);
            seat.addTag(SOFA_SEAT_TAG);
            seat.addTag(posTag);
            serverLevel.addFreshEntity(seat);
        }

        if (player.startRiding(seat, false)) {
            // Clean up stale empty seat entities around this sofa.
            for (ArmorStand existing : seats) {
                if (existing != seat && existing.getPassengers().isEmpty() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(existing)) {
                    existing.discard();
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            String posTag = SOFA_SEAT_POS_TAG_PREFIX + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            AABB seatArea = new AABB(
                    pos.getX() + 0.35D, pos.getY() - 1.6D, pos.getZ() + 0.35D,
                    pos.getX() + 0.65D, pos.getY() + 0.4D, pos.getZ() + 0.65D
            );
            for (ArmorStand seat : level.getEntitiesOfClass(ArmorStand.class, seatArea, entity ->
                    entity.getTags().contains(SOFA_SEAT_TAG) && entity.getTags().contains(posTag)
            )) {
                seat.discard();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

}
