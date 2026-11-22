package com.renyigesai.bakeries;

import com.renyigesai.bakeries.screen.BlenderScreen;
import com.renyigesai.bakeries.screen.CupboardScreen;
import com.renyigesai.bakeries.screen.DoughCraftingTableScreen;
import com.renyigesai.bakeries.screen.FermentationBoxScreen;
import com.renyigesai.bakeries.screen.OvenScreen;
import com.renyigesai.bakeries.key.BakeriesKeyMapping;
import com.renyigesai.bakeries.init.BakeriesBlocks;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import com.renyigesai.bakeries.init.BakeriesMenuTypes;
import com.renyigesai.bakeries.init.BakeriesParticleTypes;
import com.renyigesai.bakeries.client.particle.TomatoSauceParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import com.renyigesai.bakeries.renderer.MokaPotBlockEntityRenderer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@SuppressWarnings("unused")
public final class BakeriesClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerRenderLayers();
        MenuScreens.register(BakeriesMenuTypes.OVEN, OvenScreen::new);
        MenuScreens.register(BakeriesMenuTypes.BLENDER, BlenderScreen::new);
        MenuScreens.register(BakeriesMenuTypes.FERMENTATION_BOX, FermentationBoxScreen::new);
        MenuScreens.register(BakeriesMenuTypes.DOUGH_CRAFTING_TABLE, DoughCraftingTableScreen::new);
        MenuScreens.register(BakeriesMenuTypes.CUPBOARD, CupboardScreen::new);
        BlockEntityRenderers.register(BakeriesBlockEntities.MACHINE, MokaPotBlockEntityRenderer::new);
        BakeriesKeyMapping.init();
        ParticleFactoryRegistry.getInstance().register(BakeriesParticleTypes.TOMATO_SAUCE, TomatoSauceParticle.Provider::new);
    }

    private static void registerRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.cutout(),
                BakeriesBlocks.TOASTER,
                BakeriesBlocks.FERMENTATION_TANK,
                BakeriesBlocks.WOOD_COUNTER,
                BakeriesBlocks.COFFEE_TABLE,
                BakeriesBlocks.CHEESE_TANK,
                BakeriesBlocks.MILK_TANK,
                BakeriesBlocks.YEAST_TANK,
                BakeriesBlocks.BREAD_RACK,
                BakeriesBlocks.GLASS_BREAD_RACK,
                BakeriesBlocks.BREAD_BASKET,
                BakeriesBlocks.GLASS_CABINET_DOOR,
                BakeriesBlocks.MOULD,
                BakeriesBlocks.DRINK_CUP,
                BakeriesBlocks.TOAST,
                BakeriesBlocks.CHEESE_COCOA_TOAST,
                BakeriesBlocks.BAGEL,
                BakeriesBlocks.BAGUETTE,
                BakeriesBlocks.CROISSANT,
                BakeriesBlocks.ROUND_BREAD,
                BakeriesBlocks.RICE_BREAD,
                BakeriesBlocks.WHOLE_WHEAT_BAGEL,
                BakeriesBlocks.PINEAPPLE_BUN,
                BakeriesBlocks.FOCACCIA,
                BakeriesBlocks.CIABATTA,
                BakeriesBlocks.EGG_TART,
                BakeriesBlocks.SALT_CROISSANT,
                BakeriesBlocks.COUNTRY_BREAD,
                BakeriesBlocks.CREAM_BINGLE_COFFEE,
                BakeriesBlocks.MATCHA_PARFAIT,
                BakeriesBlocks.COFFEE_PLANT,
                BakeriesBlocks.TARO,
                BakeriesBlocks.TOMATO,
                BakeriesBlocks.BERRY_BREAD,
                BakeriesBlocks.GARLIC_FLAVORED_BAGUETTE,
                BakeriesBlocks.SALMON_SANDWICH,
                BakeriesBlocks.DONGPO_PORK_HAMBURG,
                BakeriesBlocks.LONG_EGG_TART,
                BakeriesBlocks.SWEET_BERRIES_JAM,
                BakeriesBlocks.BOXED_EGG,
                BakeriesBlocks.CREAM_PUMPKIN_PIE,
                BakeriesBlocks.CREAM_MUSHROOM_SOUP_WITH_BAGUETTE,
                BakeriesBlocks.TRAY_SCONE,
                BakeriesBlocks.BREAD_HOLDERS,
                BakeriesBlocks.MOULD_TOAST,
                BakeriesBlocks.MOULD_CHEESE_COCOA_TOAST,
                BakeriesBlocks.CAKE_BASE,
                BakeriesBlocks.RED_VELVET_CAKE_BASE,
                BakeriesBlocks.CREAM_CAKE,
                BakeriesBlocks.MULTI_LAYER_CREAM_CAKE,
                BakeriesBlocks.TIRAMISU,
                BakeriesBlocks.CARROT_CAKE,
                BakeriesBlocks.RED_VELVET_CAKE,
                BakeriesBlocks.MATCHA_CAKE,
                BakeriesBlocks.BASQUE_CAKE,
                BakeriesBlocks.NIANGAO_CAKE,
                BakeriesBlocks.TARO_CAKE,
                BakeriesBlocks.POUND_CAKE,
                BakeriesBlocks.CUP_CAKE,
                BakeriesBlocks.PAPER_CUP,
                BakeriesBlocks.SILICONE_PAPER,
                BakeriesBlocks.MOULD_TWO,
                BakeriesBlocks.MOULD_CAKE_BASE,
                BakeriesBlocks.MOULD_CARROT_CAKE,
                BakeriesBlocks.MOULD_RED_VELVET_CAKE,
                BakeriesBlocks.MOULD_MATCHA_CAKE,
                BakeriesBlocks.MOULD_BASQUE_CAKE,
                BakeriesBlocks.MOULD_POUND_CAKE
        );
    }
}
