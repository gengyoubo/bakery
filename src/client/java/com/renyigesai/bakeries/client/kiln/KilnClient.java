package com.renyigesai.bakeries.client.kiln;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.renyigesai.bakeries.common.kiln.StoneKilnBlockEntity;
import com.renyigesai.bakeries.common.pizza.PizzaFlatbreadBlockEntity;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Client side of the stone kiln and pizza: block entity renderers and the kiln progress overlay. */
public final class KilnClient {
    private static final ResourceLocation PROGRESS_BAR = new ResourceLocation("bakeries", "textures/gui/stone_kiln_progress_bar.png");

    private KilnClient() {
    }

    public static void init() {
        BlockEntityRenderers.register(BakeriesBlockEntities.STONE_KILN, StoneKilnRenderer::new);
        BlockEntityRenderers.register(BakeriesBlockEntities.PIZZA_FLATBREAD, FlatbreadRenderer::new);
        HudRenderCallback.EVENT.register((graphics, tickDelta) -> renderKilnOverlay(graphics));
    }

    /** The baking item floats in the kiln, grows while it cooks and spins half a turn when turned over. */
    public static class StoneKilnRenderer implements BlockEntityRenderer<StoneKilnBlockEntity> {
        public StoneKilnRenderer(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(StoneKilnBlockEntity kiln, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            ItemStack stack = kiln.getItem();
            if (stack.isEmpty() || kiln.getLevel() == null) {
                return;
            }
            Direction direction = kiln.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            float scale = 0.5F + kiln.getSize();
            pose.pushPose();
            pose.translate(0.5, 0.63125 + kiln.getSize() / 4.0F, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(-direction.toYRot() + 180.0F * kiln.getTurnProgress(partialTick)));
            pose.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, overlay,
                    pose, buffers, kiln.getLevel(), (int) kiln.getBlockPos().asLong());
            pose.popPose();
        }
    }

    /** Toppings and cheese lying flat on the flatbread (same layout as the Forge renderer). */
    public static class FlatbreadRenderer implements BlockEntityRenderer<PizzaFlatbreadBlockEntity> {
        private static final float[][] TOPPING_POS = {{0.5F, 0.65F}, {0.5F, 0.35F}, {0.65F, 0.5F}, {0.35F, 0.5F}};
        private static final float[] TOPPING_ROT = {15.0F, -20.0F, 10.0F, -10.0F};
        private static final float[][] CHEESE_POS = {{0.46875F, 0.46875F}, {0.7F, 0.7F}, {0.3F, 0.7F}, {0.7F, 0.3F}, {0.3F, 0.3F}};
        private static final float[] CHEESE_ROT = {-23.0F, 12.0F, 10.0F, -15.0F, 5.0F};

        public FlatbreadRenderer(BlockEntityRendererProvider.Context context) {
        }

        @Override
        public void render(PizzaFlatbreadBlockEntity pizza, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            if (pizza.getLevel() == null) {
                return;
            }
            float base = -pizza.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite().toYRot() - 180.0F;
            int seed = (int) pizza.getBlockPos().asLong();
            if (pizza.hasCheese()) {
                for (int i = 0; i < CHEESE_POS.length; i++) {
                    flat(pizza.getCheese(), CHEESE_POS[i], base + CHEESE_ROT[i], 0.25F, pose, buffers, overlay, pizza, seed + i);
                }
            }
            for (int i = 0; i < pizza.getToppings().size(); i++) {
                ItemStack topping = pizza.getToppings().get(i);
                if (!topping.isEmpty()) {
                    flat(topping, TOPPING_POS[i], base + TOPPING_ROT[i], 0.2F, pose, buffers, overlay, pizza, seed + 10 + i);
                }
            }
        }

        private static void flat(ItemStack stack, float[] at, float yRot, float scale, PoseStack pose, MultiBufferSource buffers, int overlay,
                                 PizzaFlatbreadBlockEntity pizza, int seed) {
            pose.pushPose();
            pose.translate(at[0], 0.0625, at[1]);
            pose.mulPose(Axis.YP.rotationDegrees(yRot));
            pose.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose.scale(scale, scale, scale);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, overlay,
                    pose, buffers, pizza.getLevel(), seed);
            pose.popPose();
        }
    }

    /** Looking at a working kiln: progress bar, turns done, and a hint when it needs turning. */
    private static void renderKilnOverlay(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (!(mc.level.getBlockEntity(((BlockHitResult) mc.hitResult).getBlockPos()) instanceof StoneKilnBlockEntity kiln) || kiln.isEmpty()) {
            return;
        }
        int x = graphics.guiWidth() / 2 - 56 - 23;
        int y = graphics.guiHeight() / 2 + 15;
        float scale = 1.5F;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, scale);
        graphics.blit(PROGRESS_BAR, 0, 0, 0.0F, 0.0F, 103, 40, 103, 46);
        int width = 0;
        if (kiln.getMaxStageTime() != 0) {
            width = 80 * kiln.getStageTime() / kiln.getMaxStageTime();
        } else if (kiln.getMaxCookingTime() != 0) {
            width = 80 * Math.min(kiln.getCookingTime(), kiln.getMaxCookingTime()) / kiln.getMaxCookingTime();
        }
        graphics.blit(PROGRESS_BAR, 13, 19, 0.0F, 40.0F, width, 6, 103, 46);
        if (kiln.getMaxTurnOver() != 0) {
            graphics.drawString(mc.font, kiln.getTurnOver() + "/" + kiln.getMaxTurnOver(), 44, 9, 0xFFFFFF);
        }
        if (kiln.needsTurn()) {
            graphics.drawString(mc.font, Component.translatable("tip.bakeries.stone_kiln_stone_kiln_shovel"), 25, 35, 0xFFFFFF);
        }
        graphics.pose().popPose();
    }
}
