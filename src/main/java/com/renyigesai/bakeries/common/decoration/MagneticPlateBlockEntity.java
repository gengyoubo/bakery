package com.renyigesai.bakeries.common.decoration;

import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagneticPlateBlockEntity extends BlockEntity {
    private String blockId = "minecraft:air";
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private final float[] xyo = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
    private int rotationFlag = 1;

    public MagneticPlateBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.MAGNETIC_PLATE, pos, state);
    }

    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public Block getBlock() {
        ResourceLocation id = ResourceLocation.tryParse(this.blockId);
        return id == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(id);
    }

    public float[] getXyo() {
        return this.xyo;
    }

    public void setXyo0(float[] value) {
        if (value.length >= 2) {
            this.xyo[0] = value[0];
            this.xyo[1] = value[1];
        }
    }

    public void setXyo1(float[] value) {
        if (value.length >= 2) {
            this.xyo[2] = value[0];
            this.xyo[3] = value[1];
        }
    }

    public int getRotationFlag() {
        return this.rotationFlag;
    }

    public void setRotationFlag(int rotationFlag) {
        this.rotationFlag = rotationFlag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("BlockId", this.blockId);
        CompoundTag itemsTag = new CompoundTag();
        net.minecraft.world.ContainerHelper.saveAllItems(itemsTag, this.items);
        tag.put("Items", itemsTag);
        int[] raw = new int[this.xyo.length];
        for (int i = 0; i < this.xyo.length; i++) {
            raw[i] = Float.floatToRawIntBits(this.xyo[i]);
        }
        tag.putIntArray("Xyo", raw);
        tag.putInt("RotationFlag", this.rotationFlag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("BlockId")) {
            this.blockId = tag.getString("BlockId");
        }
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        if (tag.contains("Items")) {
            CompoundTag itemsTag = tag.getCompound("Items");
            for (int i = 0; i < itemsTag.getList("Items", 10).size(); i++) {
                CompoundTag compound = itemsTag.getList("Items", 10).getCompound(i);
                int slot = compound.getByte("Slot") & 255;
                if (slot < this.items.size()) {
                    this.items.set(slot, ItemStack.of(compound));
                }
            }
        }
        int[] raw = tag.getIntArray("Xyo");
        for (int i = 0; i < Math.min(raw.length, this.xyo.length); i++) {
            this.xyo[i] = Float.intBitsToFloat(raw[i]);
        }
        this.rotationFlag = tag.getInt("RotationFlag");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(this.items.size());
        for (int i = 0; i < this.items.size(); i++) {
            container.setItem(i, this.items.get(i));
        }
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, container);
        }
    }

    public void update() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }
}
