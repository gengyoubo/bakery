package com.renyigesai.bakeries.common.pizza;

import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Placed custom pizza: its toppings decide the food value and effects of each slice. */
public class CustomPizzaBlockEntity extends BlockEntity {
    private NonNullList<ItemStack> toppings = NonNullList.withSize(CustomPizzaItem.SLOTS, ItemStack.EMPTY);
    private int nutrition = 4;
    private float saturation = 0.5F;

    public CustomPizzaBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.CUSTOM_PIZZA, pos, state);
    }

    public NonNullList<ItemStack> getToppings() {
        return toppings;
    }

    public void setToppings(NonNullList<ItemStack> toppings) {
        this.toppings = toppings;
        float[] sat = new float[1];
        this.nutrition = CustomPizzaItem.foodValues(toppings, sat)[0];
        this.saturation = sat[0];
        setChanged();
    }

    public int getNutrition() {
        return nutrition;
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", CakeRollProcessingBlockEntity.writeInventory(toppings));
        tag.putInt("Nutrition", nutrition);
        tag.putFloat("SaturationMod", saturation);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        toppings = CakeRollProcessingBlockEntity.readInventory(tag.getCompound("Inventory"), CustomPizzaItem.SLOTS);
        nutrition = tag.contains("Nutrition") ? tag.getInt("Nutrition") : 4;
        saturation = tag.contains("SaturationMod") ? tag.getFloat("SaturationMod") : 0.5F;
    }
}
