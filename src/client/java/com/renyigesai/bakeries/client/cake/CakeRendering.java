package com.renyigesai.bakeries.client.cake;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.init.BakeriesBlocks;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Shared drawing of a custom cake: silicone paper, then each part's model, then the candle. */
public final class CakeRendering {
    private CakeRendering() {
    }

    /** Model of a cake part: {@code <ns>:cake_part/<path>} for part id {@code <ns>:<path>}. */
    public static ResourceLocation partModelId(String partId) {
        ResourceLocation id = new ResourceLocation(partId);
        return new ResourceLocation(id.getNamespace(), "cake_part/" + id.getPath());
    }

    public static @Nullable BakedModel partModel(String partId) {
        var manager = Minecraft.getInstance().getModelManager();
        BakedModel model = ((FabricBakedModelManager) manager).getModel(partModelId(partId));
        return model == null || model == manager.getMissingModel() ? null : model;
    }

    public static void renderCake(List<String> partIds, Direction facing, PoseStack pose, MultiBufferSource buffers,
                                  int light, int overlay, boolean withPaper, @Nullable String candleId) {
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (withPaper) {
            blockRenderer.renderSingleBlock(BakeriesBlocks.SILICONE_PAPER.defaultBlockState(), pose, buffers, light, overlay);
        }
        BlockState dummy = BakeriesBlocks.CUSTOM_CAKE.defaultBlockState();
        for (String id : partIds) {
            BakedModel model = partModel(id);
            if (model == null) {
                continue;
            }
            pose.pushPose();
            rotate(pose, facing, 0.0);
            blockRenderer.getModelRenderer().renderModel(pose.last(), buffers.getBuffer(RenderType.cutout()), dummy, model,
                    1.0F, 1.0F, 1.0F, light, overlay);
            pose.popPose();
        }
        if (candleId != null && !candleId.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(candleId);
            Block candle = rl == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(rl);
            if (candle != Blocks.AIR) {
                pose.pushPose();
                pose.translate(0.5, 0.5, 0.5);
                pose.mulPose(Axis.YP.rotationDegrees(yRot(facing)));
                pose.scale(0.495F, 0.495F, 0.495F);
                pose.translate(-0.5, 0.0, -0.5);
                blockRenderer.renderSingleBlock(candle.defaultBlockState(), pose, buffers, light, overlay);
                pose.popPose();
            }
        }
    }

    /** Same rotation the Forge renderer used (east/west get an extra half turn). */
    private static float yRot(Direction facing) {
        float rot = facing.toYRot();
        if (facing == Direction.WEST || facing == Direction.EAST) {
            rot += 180.0F;
        }
        return rot;
    }

    private static void rotate(PoseStack pose, Direction facing, double y) {
        pose.translate(0.5, y, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(yRot(facing)));
        pose.translate(-0.5, 0.0, -0.5);
    }
}
