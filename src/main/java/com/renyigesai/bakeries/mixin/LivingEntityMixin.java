package com.renyigesai.bakeries.mixin;

import com.renyigesai.bakeries.common.potion.BerrySourMobEffect;
import com.renyigesai.bakeries.common.potion.SoftMobEffect;
import com.renyigesai.bakeries.init.BakeriesMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float bakeries$applyEnjoyDamage(float amount, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingEntity && livingEntity.hasEffect(BakeriesMobEffects.ENJOY)) {
            MobEffectInstance effect = livingEntity.getEffect(BakeriesMobEffects.ENJOY);
            if (effect != null) {
                return amount + 2.0F * (effect.getAmplifier() + 1);
            }
        }
        return amount;
    }

    /** Soft: bounce the attacker away (Forge: LivingAttackEvent). */
    @Inject(method = "hurt", at = @At("HEAD"))
    private void bakeries$applySoft(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide) {
            SoftMobEffect.onAttacked(self, source);
        }
    }

    /**
     * Berry Sour: scale damage before armor (Forge: LivingHurtEvent). Both LivingEntity#actuallyHurt and
     * Player#actuallyHurt call this method once the hit is known to land, so players are covered too.
     */
    @ModifyVariable(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float bakeries$applyBerrySour(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) {
            return amount;
        }
        return BerrySourMobEffect.modifyDamage(self, source, amount);
    }
}
