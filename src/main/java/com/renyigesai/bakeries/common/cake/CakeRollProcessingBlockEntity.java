package com.renyigesai.bakeries.common.cake;

import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Up to four fillings laid on a cut cake base before it is rolled up. */
public class CakeRollProcessingBlockEntity extends BlockEntity {
    public static final int SLOTS = 4;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    public CakeRollProcessingBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.CAKE_ROLL_PROCESSING, pos, state);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean addItem(ItemStack stack) {
        for (int i = 0; i < SLOTS; i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copyWithCount(1));
                update();
                return true;
            }
        }
        return false;
    }

    /** Same layout as an ItemStackHandler ("Items" + "Size"), as used by the Forge version. */
    public static CompoundTag writeInventory(NonNullList<ItemStack> items) {
        CompoundTag inv = new CompoundTag();
        ContainerHelper.saveAllItems(inv, items);
        inv.putInt("Size", items.size());
        return inv;
    }

    public static NonNullList<ItemStack> readInventory(CompoundTag inv, int size) {
        NonNullList<ItemStack> list = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(inv, list);
        return list;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", writeInventory(items));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        NonNullList<ItemStack> loaded = readInventory(tag.getCompound("Inventory"), SLOTS);
        for (int i = 0; i < SLOTS; i++) {
            items.set(i, loaded.get(i));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void update() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
