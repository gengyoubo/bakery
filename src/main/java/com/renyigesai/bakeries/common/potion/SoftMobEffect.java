package com.renyigesai.bakeries.common.potion;

import com.renyigesai.bakeries.init.BakeriesMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Armor + knockback resistance (attribute modifiers) and bounces the attacker away. */
public class SoftMobEffect extends BakeriesMobEffect {
    public SoftMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -3972305);
    }

    /** Called when {@code target} is attacked (start of LivingEntity#hurt, server side). */
    public static void onAttacked(LivingEntity target, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null || !target.hasEffect(BakeriesMobEffects.SOFT)) {
            return;
        }
        Vec3 motion = target.getDeltaMovement();
        double strength = 1.5;
        float yaw = target.getYRot() * (float) (Math.PI / 180.0);
        Vec3 push = new Vec3(Mth.sin(yaw), 0.8, -Mth.cos(yaw)).normalize().scale(strength);
        attacker.setDeltaMovement(
                motion.x / 2.0 - push.x,
                target.onGround() ? Math.min(0.4, motion.y / 2.0 + strength) : motion.y,
                motion.z / 2.0 - push.z
        );
        // Without this the velocity change never reaches player clients.
        attacker.hurtMarked = true;
    }
}
