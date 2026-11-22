package com.renyigesai.bakeries.client.cake;

import com.renyigesai.bakeries.common.cake.CakeParts;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/** Client side of the cake system: renderers, cake part models, custom cake item model, part tooltips. */
public final class CakeClient {
    private static final ModelResourceLocation CUSTOM_CAKE_ITEM = new ModelResourceLocation(new ResourceLocation("bakeries", "custom_cake"), "inventory");

    private CakeClient() {
    }

    public static void init() {
        BlockEntityRenderers.register(BakeriesBlockEntities.CUSTOM_CAKE, CakeRenderers.CustomCake::new);
        BlockEntityRenderers.register(BakeriesBlockEntities.CAKE_ROLL_PROCESSING, CakeRenderers.CakeRoll::new);
        BlockEntityRenderers.register(BakeriesBlockEntities.CAKE_BOX, CakeRenderers.CakeBox::new);

        // every models/cake_part/*.json of every namespace becomes an extra model, wrap the custom cake item model
        PreparableModelLoadingPlugin.register(CakeClient::findPartModels, (partModels, context) -> {
            context.addModels(partModels);
            context.modifyModelAfterBake().register((model, ctx) ->
                    CUSTOM_CAKE_ITEM.equals(ctx.id()) && model != null ? new CustomCakeItemModel(model) : model);
        });

        ClientPlayNetworking.registerGlobalReceiver(CakeParts.SYNC_PACKET, (client, handler, buf, sender) -> {
            FriendlyByteBuf copy = new FriendlyByteBuf(buf.copy());
            client.execute(() -> CakeParts.acceptClientTypes(copy));
        });

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            String type = CakeParts.getClientPartTypes().get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            if (type != null) {
                lines.add(Component.translatable("tooltip.bakeries.cake_part", CakeParts.typeName(type)).withStyle(ChatFormatting.GOLD));
            }
        });
    }

    private static CompletableFuture<List<ResourceLocation>> findPartModels(ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> manager.listResources("models/cake_part", rl -> rl.getPath().endsWith(".json"))
                .keySet().stream()
                .map(rl -> new ResourceLocation(rl.getNamespace(), rl.getPath().substring("models/".length(), rl.getPath().length() - 5)))
                .toList(), executor);
    }
}
