package com.renyigesai.bakeries.common.potion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Halves the horizontal speed of every moving entity within 3.5 blocks (own projectiles excepted). */
public class TeaAstringentMobEffect extends BakeriesMobEffect {
    private static final double RANGE = 3.5;

    public TeaAstringentMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -10769635);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity owner, int amplifier) {
        super.applyEffectTick(owner, amplifier);
        double x = owner.getX();
        double y = owner.getY();
        double z = owner.getZ();
        AABB area = new AABB(x - RANGE, y - RANGE, z - RANGE, x + RANGE, y + RANGE, z + RANGE);
        RandomSource random = owner.level().random;
        for (Entity entity : owner.level().getEntities(owner, area, Entity::isAlive)) {
            if (entity instanceof ItemEntity) {
                continue;
            }
            if (entity instanceof Projectile projectile && projectile.getOwner() == owner) {
                continue;
            }
            Vec3 motion = entity.getDeltaMovement();
            double horizontalSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            if (horizontalSpeed < 0.01) {
                continue;
            }
            entity.setDeltaMovement(motion.x * 0.5, motion.y, motion.z * 0.5);
            if (owner.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                        entity.getX() + random.nextDouble(), entity.getY() + 0.7, entity.getZ() + random.nextDouble(),
                        1, 0.0, 0.0, 0.0, 0.25);
            }
        }
    }
}
