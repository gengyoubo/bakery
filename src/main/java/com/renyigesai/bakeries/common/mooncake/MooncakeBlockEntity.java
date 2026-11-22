package com.renyigesai.bakeries.common.mooncake;

import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The mooncakes in a pile, one per layer, so each keeps its own filling. */
public class MooncakeBlockEntity extends BlockEntity {
    public static final int SIZE = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public MooncakeBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.MOONCAKE, pos, state);
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        update();
    }

    public void dropAll() {
        if (level == null) {
            return;
        }
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            }
        }
        items.replaceAll(s -> ItemStack.EMPTY);
    }

    public void update() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", CakeRollProcessingBlockEntity.writeInventory(items));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Items")) {
            NonNullList<ItemStack> loaded = CakeRollProcessingBlockEntity.readInventory(tag.getCompound("Items"), SIZE);
            for (int i = 0; i < SIZE; i++) {
                items.set(i, loaded.get(i));
            }
        }
    }
}
