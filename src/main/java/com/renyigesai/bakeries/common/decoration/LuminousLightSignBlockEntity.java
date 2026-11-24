package com.renyigesai.bakeries.common.decoration;

import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LuminousLightSignBlockEntity extends BlockEntity {
    private String text;
    private int color = 0xFFFFFF;
    private boolean center = true;

    public LuminousLightSignBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.LUMINOUS_LIGHT_SIGN, pos, state);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCenter() {
        return this.center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Text", this.text == null ? "" : this.text);
        tag.putInt("Color", this.color);
        tag.putBoolean("Center", this.center);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.text = tag.getString("Text");
        this.color = tag.getInt("Color");
        this.center = tag.getBoolean("Center");
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

    public void update() {
        this.setChanged();
        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }
}
