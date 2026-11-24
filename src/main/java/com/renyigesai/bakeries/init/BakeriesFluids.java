package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.common.fluid.SaltWaterFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * Salt water fluid (still + flowing) and its liquid block.
 *
 * <p>Registered before blocks/items so that the block and bucket can reference the fluid directly.</p>
 */
public final class BakeriesFluids {
    public static final FlowingFluid SALT_WATER = Registry.register(
            BuiltInRegistries.FLUID,
            new ResourceLocation(BakeriesMod.MODID, "salt_water"),
            new SaltWaterFluid.Source()
    );
    public static final FlowingFluid FLOWING_SALT_WATER = Registry.register(
            BuiltInRegistries.FLUID,
            new ResourceLocation(BakeriesMod.MODID, "flowing_salt_water"),
            new SaltWaterFluid.Flowing()
    );

    private BakeriesFluids() {
    }

    public static void init() {
        BakeriesMod.LOGGER.info("Registered Bakeries fluids.");
    }
}
