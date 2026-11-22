package com.renyigesai.bakeries.recipe;

import com.renyigesai.bakeries.common.mooncake.MooncakeItem;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.BakeriesRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Raw mooncake: round mould + one flour + one honey bottle + up to 6 foods as the filling (any shape).
 * The mould is kept (it loses durability), the fillings are written onto the raw mooncake.
 */
public class MooncakeRecipe extends CustomRecipe {
    public static final TagKey<Item> FLOUR = TagKey.create(Registries.ITEM, new ResourceLocation("c", "flour"));
    public static final int MAX_FILLINGS = 6;

    public MooncakeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    private enum Kind { MOULD, FLOUR, HONEY, FILLING, INVALID }

    private static Kind kind(ItemStack stack) {
        if (stack.is(BakeriesItems.ROUND_MOULD)) {
            return Kind.MOULD;
        }
        if (stack.is(FLOUR)) {
            return Kind.FLOUR;
        }
        if (stack.is(Items.HONEY_BOTTLE)) {
            return Kind.HONEY;
        }
        if (stack.getItem().isEdible() && !(stack.getItem() instanceof MooncakeItem)) {
            return Kind.FILLING;
        }
        return Kind.INVALID;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int mould = 0, flour = 0, honey = 0, fillings = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            switch (kind(stack)) {
                case MOULD -> mould++;
                case FLOUR -> flour++;
                case HONEY -> honey++;
                case FILLING -> fillings++;
                default -> {
                    return false;
                }
            }
        }
        return mould == 1 && flour == 1 && honey == 1 && fillings <= MAX_FILLINGS;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        List<Item> fillings = new ArrayList<>();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty() && kind(stack) == Kind.FILLING) {
                fillings.add(stack.getItem());
            }
        }
        ItemStack output = new ItemStack(BakeriesItems.RAW_MOONCAKE);
        MooncakeItem.setFillings(output, fillings);
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BakeriesRecipeTypes.MOONCAKE_SERIALIZER;
    }
}
