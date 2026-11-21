package com.renyigesai.bakeries.common.potion;

import com.renyigesai.bakeries.init.BakeriesMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

/**
 * Attacks on targets in water or rain deal 1.5x (per level) damage; each hit also has a
 * 20% (per level) chance to deal 2x and call down lightning on the target.
 */
public class BerrySourMobEffect extends BakeriesMobEffect {
    public BerrySourMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -1619356);
    }

    /** Called with the damage about to be applied to {@code target}; returns the new amount. */
    public static float modifyDamage(LivingEntity target, DamageSource source, float amount) {
        if (!(source.getEntity() instanceof LivingEntity attacker) || !attacker.hasEffect(BakeriesMobEffects.BERRY_SOUR)) {
            return amount;
        }
        MobEffectInstance effect = attacker.getEffect(BakeriesMobEffects.BERRY_SOUR);
        int level = effect == null ? 1 : effect.getAmplifier() + 1;
        boolean wet = target.isInWater() || target.level().isRainingAt(target.blockPosition());
        float multiplier = wet ? 1.5F * level : 0.0F;
        boolean lightning = false;
        if (target.getRandom().nextDouble() <= 0.2 * level) {
            multiplier = 2.0F * level;
            lightning = true;
        }
        if (target.level() instanceof ServerLevel serverLevel) {
            if (lightning) {
                strikeLightning(serverLevel, target, attacker);
            }
            for (int i = 0; i < 16; i++) {
                double xo = target.getRandom().nextDouble() * 1.5 - 0.75;
                double yo = target.getRandom().nextDouble() * 1.5 - 0.75;
                double zo = target.getRandom().nextDouble() * 1.5 - 0.75;
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        target.getX() + xo, target.getY(0.5) + yo, target.getZ() + zo, 1, 0.0, 0.0, 0.0, 0.125);
            }
        }
        return multiplier > 0.0F ? amount * multiplier : amount;
    }

    private static void strikeLightning(ServerLevel level, Entity target, Entity attacker) {
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        bolt.setVisualOnly(true);
        bolt.moveTo(target.getX(), target.getY(), target.getZ());
        level.addFreshEntity(bolt);
        AABB area = new AABB(bolt.getX() - 3.0, bolt.getY() - 3.0, bolt.getZ() - 3.0,
                bolt.getX() + 3.0, bolt.getY() + 9.0, bolt.getZ() + 3.0);
        // The Forge version also struck the attacker standing next to the target; spare them here.
        for (Entity entity : level.getEntities(bolt, area, e -> e.isAlive() && e != attacker)) {
            entity.thunderHit(level, bolt);
        }
    }
}
