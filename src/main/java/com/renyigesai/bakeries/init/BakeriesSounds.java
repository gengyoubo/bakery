package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.BakeriesMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public final class BakeriesSounds {
    public static final SoundEvent OVEN_DRAW_SLIP = register("block.generic.oven_draw_slip");
    public static final SoundEvent SHAKE = register("entity.generic.shake");
    public static final SoundEvent OVEN_OPEN = register("block.generic.open_oven");
    public static final SoundEvent BLENDER = register("block.generic.blender");
    public static final SoundEvent PUT_ON_ICE = register("block.generic.put_on_ice");
    public static final SoundEvent INSERT_STRAW = register("block.generic.insert_straw");
    public static final SoundEvent MUSIC_DISC_BAKING_IN_PROGRESS = register("item.generic.music_disc_baking_in_progress");
    public static final SoundEvent TOASTER_IN = register("block.generic.toaster_in");
    public static final SoundEvent TOASTER_OUT = register("block.generic.toaster_out");
    public static final SoundEvent PASTRY_PLACE = register("block.generic.pastry_place");
    public static final SoundEvent STEEL_PIPE = register("block.generic.steel_pipe");

    /** Bread/pastry blocks: wool sounds with the custom pastry place sound (same as Forge PileBlock.PASTRY). */
    public static final SoundType PASTRY = new SoundType(1.0F, 1.0F,
            SoundEvents.WOOL_BREAK, SoundEvents.WOOL_STEP, PASTRY_PLACE, SoundEvents.WOOL_HIT, SoundEvents.WOOL_FALL);

    private BakeriesSounds() {
    }

    public static void init() {
        BakeriesMod.LOGGER.info("Registered Bakeries sounds.");
    }

    private static SoundEvent register(String name) {
        ResourceLocation id = new ResourceLocation(BakeriesMod.MODID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createFixedRangeEvent(id, 16.0F));
    }
}
