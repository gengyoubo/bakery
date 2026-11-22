package com.renyigesai.bakeries.common.cake;

import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A cake being assembled on silicone paper: up to one part of each type (base, filling, cream, topping),
 * plus an optional candle and a custom name. Stored with the Forge NBT keys so the item can carry it.
 */
public class CustomCakeBlockEntity extends BlockEntity {
    private String partId = "";
    private byte[] partUse = new byte[4];
    private int hunger;
    private float saturation;
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private String candleId = "";
    private String name = "";

    public CustomCakeBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.CUSTOM_CAKE, pos, state);
    }

    // ---- shared NBT format (block entity <-> item) ----

    public void writeCake(CompoundTag tag) {
        tag.putString("PartId", partId);
        tag.putByteArray("PartUse", partUse);
        tag.putInt("Hunger", hunger);
        tag.putFloat("Saturation", saturation);
        ListTag list = new ListTag();
        for (MobEffectInstance effect : effects) {
            list.add(effect.save(new CompoundTag()));
        }
        tag.put("Effects", list);
        tag.putString("Name", name);
    }

    public void readCake(CompoundTag tag) {
        partId = tag.getString("PartId");
        byte[] use = tag.getByteArray("PartUse");
        partUse = use.length == 4 ? use : Arrays.copyOf(use, 4);
        hunger = tag.getInt("Hunger");
        saturation = tag.getFloat("Saturation");
        effects.clear();
        effects.addAll(readEffects(tag));
        name = tag.getString("Name");
    }

    public static List<MobEffectInstance> readEffects(CompoundTag tag) {
        List<MobEffectInstance> list = new ArrayList<>();
        if (tag.contains("Effects", Tag.TAG_LIST)) {
            ListTag effects = tag.getList("Effects", Tag.TAG_COMPOUND);
            for (int i = 0; i < effects.size(); i++) {
                MobEffectInstance loaded = MobEffectInstance.load(effects.getCompound(i));
                if (loaded != null) {
                    list.add(loaded);
                }
            }
        }
        return list;
    }

    public static List<String> splitPartIds(String partId) {
        return partId.isEmpty() ? List.of() : Arrays.stream(partId.split("&")).filter(s -> !s.isEmpty()).toList();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writeCake(tag);
        tag.putString("CandleId", candleId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readCake(tag);
        candleId = tag.getString("CandleId");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ---- parts ----

    public List<String> getPartIds() {
        return splitPartIds(partId);
    }

    public boolean canAdd(CakePartData part) {
        int slot = CakeParts.slotOf(part.type());
        return slot >= 0 && partUse[slot] == 0;
    }

    public void addCakePart(CakePartData part) {
        partId = partId.isEmpty() ? part.id() : partId + "&" + part.id();
        int slot = CakeParts.slotOf(part.type());
        if (slot >= 0) {
            partUse[slot] = 1;
        }
        hunger += part.hunger();
        saturation += part.saturation();
        if (part.potionEffect() != null) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(part.potionEffect());
            if (effect != null) {
                effects.add(new MobEffectInstance(effect, part.effectDuration(), part.effectAmplifier()));
            }
        }
        update();
    }

    public String getCandleId() {
        return candleId;
    }

    public boolean hasCandle() {
        return candleBlock() != Blocks.AIR;
    }

    public Block candleBlock() {
        ResourceLocation id = ResourceLocation.tryParse(candleId);
        return id == null ? Blocks.AIR : BuiltInRegistries.BLOCK.get(id);
    }

    public void setCandleId(String candleId) {
        this.candleId = candleId;
        update();
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public void update() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
