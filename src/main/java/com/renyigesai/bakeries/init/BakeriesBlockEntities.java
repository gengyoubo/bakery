package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.common.cake.CakeBoxBlockEntity;
import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import com.renyigesai.bakeries.common.cake.CustomCakeBlockEntity;
import com.renyigesai.bakeries.common.decoration.LightBoxBlockEntity;
import com.renyigesai.bakeries.common.decoration.LuminousLightSignBlockEntity;
import com.renyigesai.bakeries.common.decoration.MagneticPlateBlockEntity;
import com.renyigesai.bakeries.common.fermentation.FermentationBarrelBlockEntity;
import com.renyigesai.bakeries.common.saltframe.BaysaltFrameBlockEntity;
import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.block.entity.MachineBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BakeriesBlockEntities {
    public static final BlockEntityType<MachineBlockEntity> MACHINE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(BakeriesMod.MODID, "machine"),
            BlockEntityType.Builder.of(
                    MachineBlockEntity::new,
                    BakeriesBlocks.OVEN,
                    BakeriesBlocks.TOASTER,
                    BakeriesBlocks.BLENDER,
                    BakeriesBlocks.FERMENTATION_BOX,
                    BakeriesBlocks.DOUGH_CRAFTING_TABLE,
                    BakeriesBlocks.CUPBOARD,
                    BakeriesBlocks.MENU,
                    BakeriesBlocks.MOKA_POT,
                    BakeriesBlocks.DRINK_CUP,
                    BakeriesBlocks.MIX_BLOCK
            ).build(null)
    );

    public static final BlockEntityType<CustomCakeBlockEntity> CUSTOM_CAKE = register("custom_cake",
            BlockEntityType.Builder.of(CustomCakeBlockEntity::new, BakeriesBlocks.CUSTOM_CAKE));
    public static final BlockEntityType<CakeRollProcessingBlockEntity> CAKE_ROLL_PROCESSING = register("cake_roll_processing",
            BlockEntityType.Builder.of(CakeRollProcessingBlockEntity::new, BakeriesBlocks.CAKE_ROLL_PROCESSING));
    public static final BlockEntityType<CakeBoxBlockEntity> CAKE_BOX = register("cake_box",
            BlockEntityType.Builder.of(CakeBoxBlockEntity::new, BakeriesBlocks.CAKE_BOX));

    public static final BlockEntityType<com.renyigesai.bakeries.common.kiln.StoneKilnBlockEntity> STONE_KILN = register("stone_kiln",
            BlockEntityType.Builder.of(com.renyigesai.bakeries.common.kiln.StoneKilnBlockEntity::new, BakeriesBlocks.STONE_KILN));
    public static final BlockEntityType<com.renyigesai.bakeries.common.pizza.PizzaFlatbreadBlockEntity> PIZZA_FLATBREAD = register("pizza_flatbread",
            BlockEntityType.Builder.of(com.renyigesai.bakeries.common.pizza.PizzaFlatbreadBlockEntity::new, BakeriesBlocks.PIZZA_FLATBREAD));
    public static final BlockEntityType<com.renyigesai.bakeries.common.pizza.CustomPizzaBlockEntity> CUSTOM_PIZZA = register("custom_pizza",
            BlockEntityType.Builder.of(com.renyigesai.bakeries.common.pizza.CustomPizzaBlockEntity::new, BakeriesBlocks.CUSTOM_PIZZA));

    public static final BlockEntityType<com.renyigesai.bakeries.common.mooncake.MooncakeBlockEntity> MOONCAKE = register("mooncake",
            BlockEntityType.Builder.of(com.renyigesai.bakeries.common.mooncake.MooncakeBlockEntity::new, BakeriesBlocks.MOONCAKE));

    public static final BlockEntityType<BaysaltFrameBlockEntity> BAYSALT_FRAME = register("baysalt_frame",
            BlockEntityType.Builder.of(BaysaltFrameBlockEntity::new, BakeriesBlocks.BAYSALT_FRAME));
    public static final BlockEntityType<FermentationBarrelBlockEntity> FERMENTATION_BARREL = register("fermentation_barrel",
            BlockEntityType.Builder.of(FermentationBarrelBlockEntity::new, BakeriesBlocks.FERMENTATION_BARREL));
    public static final BlockEntityType<LuminousLightSignBlockEntity> LUMINOUS_LIGHT_SIGN = register("luminous_light_sign",
            BlockEntityType.Builder.of(LuminousLightSignBlockEntity::new, BakeriesBlocks.LUMINOUS_LIGHT_SIGN));
    public static final BlockEntityType<LightBoxBlockEntity> LIGHT_BOX = register("light_box",
            BlockEntityType.Builder.of(LightBoxBlockEntity::new, BakeriesBlocks.LIGHT_BOX));
    public static final BlockEntityType<MagneticPlateBlockEntity> MAGNETIC_PLATE = register("magnetic_plate",
            BlockEntityType.Builder.of(MagneticPlateBlockEntity::new, BakeriesBlocks.MAGNETIC_PLATE));

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(BakeriesMod.MODID, id), builder.build(null));
    }

    private BakeriesBlockEntities() {
    }

    public static void init() {
        BakeriesMod.LOGGER.info("Registered Bakeries block entities.");
    }
}
