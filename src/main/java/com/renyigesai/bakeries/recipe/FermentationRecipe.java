package com.renyigesai.bakeries.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Shapeless multi-ingredient fermentation recipe used by the fermentation barrel.
 * Ported from the Forge {@code FermentationRecipe}.
 */
public class FermentationRecipe implements Recipe<Container> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ItemStack container;
    private final ResourceLocation id;

    public FermentationRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, ResourceLocation id) {
        this.inputItems = inputItems;
        this.output = output;
        this.container = container == null ? ItemStack.EMPTY : container;
        this.id = id;
    }

    @Override
    public boolean matches(Container container, @NotNull Level level) {
        if (level.isClientSide) {
            return false;
        }
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }
        if (inputs.size() != this.inputItems.size()) {
            return false;
        }
        List<Ingredient> remaining = new ArrayList<>(this.inputItems);
        for (ItemStack input : inputs) {
            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).test(input)) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registryAccess) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return this.output.copy();
    }

    public ItemStack getContainer() {
        return this.container.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return BakeriesRecipeTypes.FERMENTATION_SERIALIZER;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return BakeriesRecipeTypes.FERMENTATION;
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {
        @Override
        public @NotNull FermentationRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.create();
            for (int i = 0; i < ingredientsJson.size(); i++) {
                inputs.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }
            ItemStack container = GsonHelper.isValidNode(json, "container")
                    ? ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "container"))
                    : ItemStack.EMPTY;
            return new FermentationRecipe(inputs, output, container, id);
        }

        @Override
        public @Nullable FermentationRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
            int count = buffer.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack container = buffer.readItem();
            ItemStack output = buffer.readItem();
            return new FermentationRecipe(inputs, output, container, id);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull FermentationRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());
            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.container);
            buffer.writeItem(recipe.output);
        }
    }
}
