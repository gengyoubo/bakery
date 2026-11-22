package com.renyigesai.bakeries.common.cake;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.init.BakeriesMobEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

/** Custom cake parts: datapack loading, ingredient lookup, effect rules and the client sync of part types. */
public final class CakeParts {
    public static final String CAKE_BASE = "bakeries:cake_base";
    public static final String CAKE_FILLING = "bakeries:cake_filling";
    public static final String CAKE_CREAM = "bakeries:cake_cream";
    public static final String CAKE_TOPPING = "bakeries:cake_topping";
    /** Type order = slot order in a custom cake (base, filling, cream, topping). */
    public static final List<String> TYPES = List.of(CAKE_BASE, CAKE_FILLING, CAKE_CREAM, CAKE_TOPPING);
    public static final ResourceLocation SYNC_PACKET = new ResourceLocation(BakeriesMod.MODID, "cake_part_types");

    private static final Map<String, CakePartData> SERVER_PARTS = new LinkedHashMap<>();
    /** Client side: item id -> part type, for tooltips. */
    private static final Map<ResourceLocation, String> CLIENT_PART_TYPES = new HashMap<>();
    private static final List<UnaryOperator<List<MobEffectInstance>>> EFFECT_RULES = new ArrayList<>();

    private CakeParts() {
    }

    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(BakeriesMod.MODID, "cake_parts");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                load(manager);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendTypes(handler.player));
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> sendTypes(player));
        registerRule(CakeParts::tooDisgusting);
        registerRule(CakeParts::amplification);
    }

    private static void load(ResourceManager manager) {
        SERVER_PARTS.clear();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("cake_parts", rl -> rl.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation file = entry.getKey();
            String path = file.getPath();
            String id = new ResourceLocation(file.getNamespace(), path.substring("cake_parts/".length(), path.length() - 5)).toString();
            try (Reader reader = entry.getValue().openAsReader()) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                String loadId = obj.has("load_id") && !obj.get("load_id").isJsonNull() ? obj.get("load_id").getAsString() : "";
                if (!loadId.isEmpty() && !FabricLoader.getInstance().isModLoaded(loadId)) {
                    continue;
                }
                SERVER_PARTS.put(id, CakePartData.fromJson(id, obj));
            } catch (Exception e) {
                BakeriesMod.LOGGER.error("Failed to parse cake part {}", id, e);
            }
        }
        BakeriesMod.LOGGER.info("Loaded {} cake parts", SERVER_PARTS.size());
    }

    public static Map<String, CakePartData> getParts() {
        return SERVER_PARTS;
    }

    public static Optional<CakePartData> find(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        for (CakePartData data : SERVER_PARTS.values()) {
            if (data.ingredient().test(stack)) {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    public static int slotOf(String type) {
        return TYPES.indexOf(type);
    }

    public static Component typeName(String type) {
        return Component.translatable("cake_part.bakeries." + type);
    }

    // ---- client sync (item -> part type) ----

    private static void sendTypes(ServerPlayer player) {
        Map<ResourceLocation, String> map = new HashMap<>();
        for (CakePartData data : SERVER_PARTS.values()) {
            for (ItemStack stack : data.ingredient().getItems()) {
                map.put(BuiltInRegistries.ITEM.getKey(stack.getItem()), data.type());
            }
        }
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeMap(map, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeUtf);
        ServerPlayNetworking.send(player, SYNC_PACKET, buf);
    }

    /** Called on the client when the sync packet arrives. */
    public static void acceptClientTypes(FriendlyByteBuf buf) {
        Map<ResourceLocation, String> map = buf.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readUtf);
        CLIENT_PART_TYPES.clear();
        CLIENT_PART_TYPES.putAll(map);
    }

    public static Map<ResourceLocation, String> getClientPartTypes() {
        return CLIENT_PART_TYPES;
    }

    // ---- effect rules (Forge CakeEffectRules) ----

    public static void registerRule(UnaryOperator<List<MobEffectInstance>> rule) {
        EFFECT_RULES.add(rule);
    }

    public static List<MobEffectInstance> applyRules(List<MobEffectInstance> effects) {
        List<MobEffectInstance> result = new ArrayList<>(effects);
        for (UnaryOperator<List<MobEffectInstance>> rule : EFFECT_RULES) {
            result = rule.apply(result);
        }
        return result;
    }

    /** Berry sour + tea astringent + cocoa mania together: the last effect gets +1 level and double duration. */
    private static List<MobEffectInstance> tooDisgusting(List<MobEffectInstance> list) {
        List<MobEffect> effects = list.stream().map(MobEffectInstance::getEffect).toList();
        if (!list.isEmpty() && effects.contains(BakeriesMobEffects.BERRY_SOUR) && effects.contains(BakeriesMobEffects.TEA_ASTRINGENT)
                && effects.contains(BakeriesMobEffects.COCOA_MANIA)) {
            int end = list.size() - 1;
            MobEffectInstance last = list.get(end);
            list.set(end, new MobEffectInstance(last.getEffect(), last.getDuration() * 2, last.getAmplifier() + 1));
        }
        return list;
    }

    /** Four or more effects: every effect lasts 20% longer. */
    private static List<MobEffectInstance> amplification(List<MobEffectInstance> list) {
        if (list.size() >= 4) {
            for (int i = 0; i < list.size(); i++) {
                MobEffectInstance e = list.get(i);
                list.set(i, new MobEffectInstance(e.getEffect(), e.getDuration() + (int) (e.getDuration() * 0.2), e.getAmplifier()));
            }
        }
        return list;
    }
}
