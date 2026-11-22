package com.renyigesai.bakeries.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.renyigesai.bakeries.init.BakeriesRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

/**
 * Stone kiln recipe (Forge format): {@code {"type":"bakeries:stone_kiln","ingredients":[{..}],"output":{..},"cooking_time":[50,50]}}.
 * Each entry of cooking_time is one stage; between stages the item has to be turned over with the kiln shovel.
 */
public class StoneKilnRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack output;
    private final int[] stages;

    public StoneKilnRecipe(ResourceLocation id, Ingredient ingredient, ItemStack output, int[] stages) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.stages = stages;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    /** Time of every stage, in ticks. */
    public int[] getStages() {
        return stages;
    }

    public int getTotalTime() {
        int total = 0;
        for (int t : stages) {
            total += t;
        }
        return total;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, ingredient);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BakeriesRecipeTypes.STONE_KILN_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return BakeriesRecipeTypes.STONE_KILN;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<StoneKilnRecipe> {
        @Override
        public StoneKilnRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            if (ingredients.isEmpty()) {
                throw new JsonParseException("Stone kiln recipe needs one ingredient");
            }
            int[] stages;
            if (json.has("cooking_time") && json.get("cooking_time").isJsonArray()) {
                JsonArray array = json.getAsJsonArray("cooking_time");
                stages = new int[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    stages[i] = array.get(i).getAsInt();
                }
            } else if (json.has("cooking_time")) {
                stages = new int[]{GsonHelper.getAsInt(json, "cooking_time")};
            } else {
                throw new JsonParseException("Missing cooking_time in stone kiln recipe");
            }
            if (stages.length == 0) {
                throw new JsonParseException("cooking_time must not be empty");
            }
            return new StoneKilnRecipe(id, Ingredient.fromJson(ingredients.get(0)), output, stages);
        }

        @Override
        public StoneKilnRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            int[] stages = buf.readVarIntArray();
            return new StoneKilnRecipe(id, ingredient, output, stages);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, StoneKilnRecipe recipe) {
            recipe.ingredient.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeVarIntArray(recipe.stages);
        }
    }
}
