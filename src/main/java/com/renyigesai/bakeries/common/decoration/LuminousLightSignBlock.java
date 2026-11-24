package com.renyigesai.bakeries.common.decoration;

import com.renyigesai.bakeries.init.blocks.HorizontalConnectBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuminousLightSignBlock extends HorizontalConnectBlock implements EntityBlock {
    private static final int MAX_SIGN_LENGTH = 45;

    public LuminousLightSignBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> shape(0, 10, 6, 16, 14, 10);
            case EAST, WEST -> shape(6, 10, 0, 10, 14, 16);
            default -> shape(0, 10, 6, 16, 14, 10);
        };
    }

    private static VoxelShape shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Shapes.box(x1 / 16.0D, y1 / 16.0D, z1 / 16.0D, x2 / 16.0D, y2 / 16.0D, z2 / 16.0D);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof LuminousLightSignBlockEntity)) {
            return super.use(state, level, pos, player, hand, hit);
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof NameTagItem) {
            if (!level.isClientSide) {
                this.continuousSetText(level, pos, held.getHoverName(), 0, true);
                if (isHelloWorld(held.getHoverName().getString()) && player instanceof ServerPlayer serverPlayer) {
                    var advancement = level.getServer().getAdvancements()
                            .getAdvancement(new ResourceLocation("bakeries", "hello_world"));
                    if (advancement != null
                            && !serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                        serverPlayer.getAdvancements().award(advancement, "hello_world");
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.getItem() instanceof DyeItem dye) {
            if (!level.isClientSide) {
                ((LuminousLightSignBlockEntity) blockEntity).setColor(dye.getDyeColor().getTextColor());
                ((LuminousLightSignBlockEntity) blockEntity).update();
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    private void continuousSetText(Level level, BlockPos pos, Component component, int depth, boolean isCenter) {
        if (!(level.getBlockEntity(pos) instanceof LuminousLightSignBlockEntity sign)) {
            return;
        }
        String text = component.getString();
        String cutText = text.length() > MAX_SIGN_LENGTH ? text.substring(0, MAX_SIGN_LENGTH) : text;
        sign.setText(cutText);
        sign.setCenter(isCenter);
        sign.update();

        Direction facing = level.getBlockState(pos).getValue(FACING);
        Direction right = facing.getClockWise();
        BlockPos rightPos = pos.relative(right);
        BlockState rightState = level.getBlockState(rightPos);
        depth++;
        if (depth >= 16 || text.length() <= MAX_SIGN_LENGTH || !rightState.is(this) || rightState.getValue(FACING) != facing) {
            return;
        }
        String remaining = text.substring(cutText.length());
        if (!remaining.isEmpty()) {
            this.continuousSetText(level, rightPos, Component.literal(remaining), depth, false);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new LuminousLightSignBlockEntity(pos, state);
    }

    private static boolean isHelloWorld(String input) {
        if (input == null) {
            return false;
        }
        String normalized = input.toUpperCase(java.util.Locale.ROOT).replace(",", "").trim().replaceAll("\\s+", " ");
        return normalized.equals("HELLO WORLD");
    }
}
