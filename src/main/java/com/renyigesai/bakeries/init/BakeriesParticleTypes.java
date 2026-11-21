package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.BakeriesMod;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class BakeriesParticleTypes {
    public static final SimpleParticleType TOMATO_SAUCE = Registry.register(
            BuiltInRegistries.PARTICLE_TYPE,
            new ResourceLocation(BakeriesMod.MODID, "tomato_sauce"),
            FabricParticleTypes.simple(false)
    );

    private BakeriesParticleTypes() {
    }

    public static void init() {
        BakeriesMod.LOGGER.info("Registered Bakeries particle types.");
    }
}
