package com.renyigesai.bakeries.common.pizza;

import com.renyigesai.bakeries.common.cake.CakeRollProcessingBlockEntity;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Pizza being assembled: up to 4 toppings, then one cheese on top. */
public class PizzaFlatbreadBlockEntity extends BlockEntity {
    private final NonNullList<ItemStack> toppings = NonNullList.withSize(CustomPizzaItem.SLOTS, ItemStack.EMPTY);
    private ItemStack cheese = ItemStack.EMPTY;

    public PizzaFlatbreadBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.PIZZA_FLATBREAD, pos, state);
    }

    public NonNullList<ItemStack> getToppings() {
        return toppings;
    }

    public ItemStack getCheese() {
        return cheese;
    }

    public boolean hasCheese() {
        return !cheese.isEmpty();
    }

    public boolean hasToppings() {
        return toppings.stream().anyMatch(s -> !s.isEmpty());
    }

    public boolean addTopping(ItemStack stack) {
        for (int i = 0; i < toppings.size(); i++) {
            if (toppings.get(i).isEmpty()) {
                toppings.set(i, stack.copyWithCount(1));
                update();
                return true;
            }
        }
        return false;
    }

    public void setCheese(ItemStack stack) {
        cheese = stack.copyWithCount(1);
        update();
    }

    public void clear() {
        for (int i = 0; i < toppings.size(); i++) {
            toppings.set(i, ItemStack.EMPTY);
        }
        cheese = ItemStack.EMPTY;
        update();
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
        tag.put("Inventory", CakeRollProcessingBlockEntity.writeInventory(toppings));
        tag.put("Cheeses", CakeRollProcessingBlockEntity.writeInventory(NonNullList.of(ItemStack.EMPTY, cheese)));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        NonNullList<ItemStack> loaded = CakeRollProcessingBlockEntity.readInventory(tag.getCompound("Inventory"), CustomPizzaItem.SLOTS);
        for (int i = 0; i < toppings.size(); i++) {
            toppings.set(i, loaded.get(i));
        }
        cheese = CakeRollProcessingBlockEntity.readInventory(tag.getCompound("Cheeses"), 1).get(0);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
