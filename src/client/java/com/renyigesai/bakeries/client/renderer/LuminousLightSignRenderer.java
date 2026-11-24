package com.renyigesai.bakeries.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.common.decoration.LuminousLightSignBlockEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LuminousLightSignRenderer implements BlockEntityRenderer<LuminousLightSignBlockEntity> {
    private static final SimpleDateFormat HOURS = new SimpleDateFormat("HH");
    private static final SimpleDateFormat MINUTES = new SimpleDateFormat("mm");
    private static boolean colonVisible = true;
    private static String lazyTime = "00 00";
    private static int count;

    private final Font font;

    public LuminousLightSignRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    public static void clientTick() {
        count++;
        if (count == 10) {
            colonVisible = !colonVisible;
        }
        if (count >= 20) {
            Date date = new Date();
            lazyTime = HOURS.format(date) + " " + MINUTES.format(date);
            colonVisible = !colonVisible;
            count = 0;
        }
    }

    @Override
    public void render(@NotNull LuminousLightSignBlockEntity entity, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        String text = entity.getText();
        poseStack.pushPose();
        this.basicsRotation(entity, poseStack);
        if (text == null || text.isEmpty()) {
            this.renderText(poseStack, buffer, lazyTime, entity.getColor(), true);
            if (colonVisible) {
                this.renderText(poseStack, buffer, ":", entity.getColor(), true);
            }
        } else {
            this.renderText(poseStack, buffer, text, entity.getColor(), entity.isCenter());
        }
        poseStack.popPose();
    }

    private void renderText(PoseStack poseStack, MultiBufferSource buffer, String text, int color, boolean center) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.875D, 0.0D);
        poseStack.scale(0.023F, -0.025F, 0.023F);
        int width = this.font.width(text);
        float x = center ? -width / 2.0F + 1.0F : -20.5F;
        this.font.drawInBatch(Component.literal(text), x, 1.0F, color, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        poseStack.popPose();
    }

    private void basicsRotation(LuminousLightSignBlockEntity entity, PoseStack poseStack) {
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

    @Override
    public boolean shouldRenderOffScreen(@NotNull LuminousLightSignBlockEntity entity) {
        return true;
    }
}
