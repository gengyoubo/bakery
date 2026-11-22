package com.renyigesai.bakeries.client.cake;

import com.renyigesai.bakeries.common.cake.CustomCakeItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item model of the custom cake: keeps the plain cake model for an empty cake, otherwise merges the
 * quads of every part's model (like the Forge CustomCakeModel), using the plain model's transforms.
 */
public class CustomCakeItemModel implements BakedModel {
    private final BakedModel original;
    private final Map<List<String>, BakedModel> cache = new HashMap<>();
    // ItemOverrides' no-arg constructor is not accessible; an empty override list behaves the same
    private final ItemOverrides overrides = new ItemOverrides((net.minecraft.client.resources.model.ModelBaker) null,
            (net.minecraft.client.renderer.block.model.BlockModel) null, List.of()) {
        @Override
        public @Nullable BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            List<String> parts = CustomCakeItem.getPartIds(stack);
            return parts.isEmpty() ? original : cache.computeIfAbsent(parts, p -> new Composite(original, p));
        }
    };

    public CustomCakeItemModel(BakedModel original) {
        this.original = original;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
        return original.getQuads(state, side, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return original.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return original.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return original.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrides;
    }

    private static final class Composite implements BakedModel {
        private final BakedModel base;
        private final List<BakedQuad> general = new ArrayList<>();
        private final Map<Direction, List<BakedQuad>> faces = new EnumMap<>(Direction.class);

        Composite(BakedModel base, List<String> parts) {
            this.base = base;
            for (Direction d : Direction.values()) {
                faces.put(d, new ArrayList<>());
            }
            RandomSource random = RandomSource.create(42L);
            for (String id : parts) {
                BakedModel part = CakeRendering.partModel(id);
                if (part == null) {
                    continue;
                }
                general.addAll(part.getQuads(null, null, random));
                for (Direction d : Direction.values()) {
                    faces.get(d).addAll(part.getQuads(null, d, random));
                }
            }
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
            return side == null ? general : faces.get(side);
        }

        @Override public boolean useAmbientOcclusion() { return base.useAmbientOcclusion(); }
        @Override public boolean isGui3d() { return base.isGui3d(); }
        @Override public boolean usesBlockLight() { return base.usesBlockLight(); }
        @Override public boolean isCustomRenderer() { return false; }
        @Override public TextureAtlasSprite getParticleIcon() { return base.getParticleIcon(); }
        @Override public ItemTransforms getTransforms() { return base.getTransforms(); }
        @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    }
}
