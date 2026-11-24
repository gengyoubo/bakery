package com.renyigesai.bakeries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.common.decoration.LightBoxBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LightBoxRenderer implements BlockEntityRenderer<LightBoxBlockEntity> {
    private final Font font;

    public LightBoxRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(@NotNull LightBoxBlockEntity entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        String text = entity.getText();
        if (text == null || text.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.basicsRotation(entity, poseStack);
        poseStack.translate(0.0D, 0.625D, 0.015625D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.scale(0.023F, -0.035F, 0.023F);
        int width = this.font.width(text);
        this.font.drawInBatch(Component.literal(text), -width / 2.0F + 1.0F, 0.0F, entity.getColor(),
                true, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        poseStack.popPose();
    }

    private void basicsRotation(LightBoxBlockEntity entity, PoseStack poseStack) {
        poseStack.translate(0.5D, 0.0D, 0.5D);
        BlockState state = entity.getBlockState();
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
