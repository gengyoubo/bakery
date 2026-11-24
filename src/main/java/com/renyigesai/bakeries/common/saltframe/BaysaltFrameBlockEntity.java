package com.renyigesai.bakeries.common.saltframe;

import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Salt water tank + salt progress for the {@link BaysaltFrameBlock}.
 */
public class BaysaltFrameBlockEntity extends BlockEntity {
    public static final int MAX_WATER = 2000;
    public static final int MAX_SALTS = 100;
    public static final int MAX_PROGRESS = 200;

    private int saltWater;
    public int salts;
    public int progress;

    public BaysaltFrameBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.BAYSALT_FRAME, pos, state);
    }

    public int getSaltWater() {
        return saltWater;
    }

    public boolean addSaltWater(int amount) {
        if (saltWater + amount > MAX_WATER) {
            return false;
        }
        saltWater += amount;
        this.setChanged();
        this.sync();
        return true;
    }

    public boolean drainSaltWater(int amount) {
        if (saltWater < amount) {
            return false;
        }
        saltWater -= amount;
        this.setChanged();
        this.sync();
        return true;
    }

    public boolean takeSalt() {
        if (salts < 10) {
            return false;
        }
        salts -= 10;
        this.setChanged();
        this.sync();
        return true;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("SaltWater", this.saltWater);
        tag.putInt("Salts", this.salts);
        tag.putInt("Progress", this.progress);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.saltWater = tag.getInt("SaltWater");
        this.salts = tag.getInt("Salts");
        this.progress = tag.getInt("Progress");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void sync() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaysaltFrameBlockEntity frame) {
        int lit = frame.saltWater > 0 ? 1 : frame.salts >= 10 ? 2 : 0;
        if (state.getValue(BaysaltFrameBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(BaysaltFrameBlock.LIT, lit), 3);
        }

        boolean canDry = frame.saltWater >= 10
                && frame.salts < MAX_SALTS
                && level.dimensionType().hasSkyLight()
                && !level.dimensionType().ultraWarm()
                && !level.dimensionType().hasCeiling()
                && level.canSeeSky(pos.above())
                && level.isDay();

        if (canDry) {
            frame.progress++;
            if (frame.progress >= MAX_PROGRESS) {
                frame.salts++;
                frame.saltWater -= 10;
                frame.progress = 0;
                frame.setChanged();
                frame.sync();
            }
        } else if (frame.progress != 0) {
            frame.progress = 0;
            frame.setChanged();
        }
    }
}
