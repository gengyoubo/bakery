package com.renyigesai.bakeries.client.cake;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.common.cake.CakeBoxBlockEntity;
import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlock;
import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import com.renyigesai.bakeries.common.cake.CustomCakeBlock;
import com.renyigesai.bakeries.common.cake.CustomCakeBlockEntity;
import com.renyigesai.bakeries.common.cake.CustomCakeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Block entity renderers for the custom cake, the cake roll in progress and the cake box. */
public final class CakeRenderers {
    private CakeRenderers() {
    }

    public static class CustomCake implements BlockEntityRenderer<CustomCakeBlockEntity> {
        public CustomCake(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(CustomCakeBlockEntity cake, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            CakeRendering.renderCake(cake.getPartIds(), cake.getBlockState().getValue(CustomCakeBlock.FACING), pose, buffers,
                    light, overlay, true, cake.getCandleId());
        }
    }

    /** Fillings lying on the flat cake base (hidden once it is rolled up). */
    public static class CakeRoll implements BlockEntityRenderer<CakeRollProcessingBlockEntity> {
        private static final float[][] OFFSETS = {{0.1875F, 0.1875F}, {-0.1875F, 0.1875F}, {0.1875F, -0.1875F}, {-0.1875F, -0.1875F}};

        public CakeRoll(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(CakeRollProcessingBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            if (entity.getBlockState().getValue(CakeRollProcessingBlock.ROLL) || entity.getLevel() == null) {
                return;
            }
            Direction direction = entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            NonNullList<ItemStack> items = entity.getItems();
            int seed = (int) entity.getBlockPos().asLong();
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = items.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                pose.pushPose();
                pose.translate(0.5, 0.1865, 0.5);
                pose.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
                pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
                pose.translate(OFFSETS[i][0], OFFSETS[i][1], 0.0);
                pose.scale(0.285F, 0.285F, 0.285F);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                        LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos()), overlay, pose, buffers, entity.getLevel(), seed + i);
                pose.popPose();
            }
        }
    }

    /** Whatever is packed in the box; a custom cake is drawn as the cake itself. */
    public static class CakeBox implements BlockEntityRenderer<CakeBoxBlockEntity> {
        public CakeBox(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(CakeBoxBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            ItemStack stack = entity.getContent();
            if (stack.isEmpty() || entity.getLevel() == null) {
                return;
            }
            Direction direction = entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            if (stack.getItem() instanceof CustomCakeItem) {
                CakeRendering.renderCake(CustomCakeItem.getPartIds(stack), direction, pose, buffers, light, overlay, false, null);
                return;
            }
            boolean block = stack.getItem() instanceof BlockItem;
            pose.pushPose();
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(block ? -direction.toYRot() : -direction.toYRot() - 180.0F));
            pose.translate(0.0, -0.315 + (block ? 0.0 : 0.125), 0.0);
            pose.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, pose, buffers,
                    entity.getLevel(), (int) entity.getBlockPos().asLong());
            pose.popPose();
        }
    }
}
