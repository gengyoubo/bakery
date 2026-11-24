package com.renyigesai.bakeries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.common.decoration.MagneticPlateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MagneticPlateRenderer implements BlockEntityRenderer<MagneticPlateBlockEntity> {
    public MagneticPlateRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull MagneticPlateBlockEntity entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        for (int slot = 0; slot < entity.getItems().size(); slot++) {
            ItemStack stack = entity.getItems().get(slot);
            if (stack.isEmpty()) {
                continue;
            }
            float u = slot == 0 ? entity.getXyo()[0] : entity.getXyo()[2];
            float v = slot == 0 ? entity.getXyo()[1] : entity.getXyo()[3];
            poseStack.pushPose();
            this.basicsRotation(entity.getBlockState(), poseStack);
            poseStack.translate(0.5F - u, v, -0.40625F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            int light = entity.getLevel() == null ? packedLight
                    : LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos());
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light,
                    packedOverlay, poseStack, buffer, entity.getLevel(),
                    (int) entity.getBlockPos().asLong() + slot);
            poseStack.popPose();
        }
    }

    private void basicsRotation(BlockState state, PoseStack poseStack) {
        poseStack.translate(0.5D, 0.0D, 0.5D);
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            switch (facing) {
                case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
                default -> {
                }
            }
        }
    }
}
