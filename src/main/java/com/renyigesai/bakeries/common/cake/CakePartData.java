package com.renyigesai.bakeries.common.cake;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

/**
 * One custom-cake part, loaded from {@code data/<ns>/cake_parts/<id>.json} (same format as the Forge version):
 * <pre>{ "type": "bakeries:cake_base|cake_filling|cake_cream|cake_topping", "ingredient": [ {..} ],
 *   "potion_effect": "ns:id", "effect_duration": ticks, "effect_amplifier": n, "hunger": n, "saturation": f, "load_id": "modid" }</pre>
 */
public record CakePartData(String id, String type, Ingredient ingredient, @Nullable ResourceLocation potionEffect,
                           int effectAmplifier, int effectDuration, int hunger, float saturation) {

    public static CakePartData fromJson(String id, JsonObject obj) {
        String type = obj.get("type").getAsString();
        JsonElement ingElem = obj.get("ingredient");
        if (ingElem == null || !ingElem.isJsonArray()) {
            throw new JsonParseException("Ingredient must be a JSON array");
        }
        JsonArray ingArray = ingElem.getAsJsonArray();
        if (ingArray.size() != 1) {
            throw new JsonParseException("Ingredient must contain exactly one element, but found " + ingArray.size());
        }
        Ingredient ingredient = Ingredient.fromJson(ingArray);
        ResourceLocation potionEffect = null;
        if (obj.has("potion_effect") && !obj.get("potion_effect").isJsonNull()) {
            String effect = obj.get("potion_effect").getAsString();
            if (!effect.isEmpty()) {
                potionEffect = new ResourceLocation(effect);
            }
        }
        int amplifier = obj.has("effect_amplifier") && !obj.get("effect_amplifier").isJsonNull() ? obj.get("effect_amplifier").getAsInt() : 0;
        int duration = 0;
        if (obj.has("effect_duration") && !obj.get("effect_duration").isJsonNull()) {
            duration = obj.get("effect_duration").getAsInt();
        } else if (potionEffect != null) {
            throw new JsonParseException("When potion_effect is specified, effect_duration must also be provided");
        }
        return new CakePartData(id, type, ingredient, potionEffect, amplifier, duration,
                obj.get("hunger").getAsInt(), obj.get("saturation").getAsFloat());
    }
}
