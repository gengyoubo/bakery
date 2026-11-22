package com.renyigesai.bakeries.common.cake;

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

/** Gift box holding a single item, shown inside it. */
public class CakeBoxBlockEntity extends BlockEntity {
    private ItemStack content = ItemStack.EMPTY;

    public CakeBoxBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.CAKE_BOX, pos, state);
    }

    public ItemStack getContent() {
        return content;
    }

    public void setContent(ItemStack stack) {
        this.content = stack;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", CakeRollProcessingBlockEntity.writeInventory(NonNullList.of(ItemStack.EMPTY, content)));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        content = CakeRollProcessingBlockEntity.readInventory(tag.getCompound("Inventory"), 1).get(0);
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
