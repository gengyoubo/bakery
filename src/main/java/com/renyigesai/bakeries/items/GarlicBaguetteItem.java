package com.renyigesai.bakeries.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

/** Garlic baguette: eating it hurts every undead mob within 4 blocks for 5 damage. */
public class GarlicBaguetteItem extends FoodEffectBlockItem {
    public GarlicBaguetteItem(Block block, Properties properties, FoodEffectTooltip.Entry... effects) {
        super(block, properties, effects);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity eater) {
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(eater.blockPosition()).inflate(4.0),
                e -> e.getMobType() == MobType.UNDEAD)) {
            living.hurt(eater.damageSources().generic(), 5.0F);
            if (level.isClientSide) {
                for (int i = 0; i < 3; i++) {
                    level.addParticle(ParticleTypes.POOF, living.getRandomX(1.0), living.getRandomY(), living.getRandomZ(1.0),
                            living.getRandom().nextGaussian() * 0.02, living.getRandom().nextGaussian() * 0.02, living.getRandom().nextGaussian() * 0.02);
                }
            }
        }
        return super.finishUsingItem(stack, level, eater);
    }
}
