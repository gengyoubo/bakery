package com.renyigesai.bakeries.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.renyigesai.bakeries.init.BakeriesMobEffects;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Eternal baguette (reward of the "very hard" advancement): unbreakable 7-damage club that knocks targets back
 * and slows them. While the holder has Enjoy, each level adds 2 attack damage (stored as "AddDamage").
 * Extends SwordItem only so that weapon enchantments are offered; it never takes durability damage.
 */
public class EternalBaguetteItem extends SwordItem implements FabricItem {
    public static final String ADD_DAMAGE = "AddDamage";
    public static final double DAMAGE_PER_ENJOY_LEVEL = 2.0;
    private static final float BASE_DAMAGE = 7.0F;
    private static final UUID KNOCKBACK_UUID = UUID.fromString("b8837973-aca5-4956-9212-e4664fb468fa");

    private static final Tier UNBREAKABLE = new Tier() {
        @Override public int getUses() { return 0; }
        @Override public float getSpeed() { return 1.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };

    public EternalBaguetteItem() {
        super(UNBREAKABLE, 0, -2.5F, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Vec3 motion = target.getDeltaMovement();
        double strength = 0.85;
        float yaw = attacker.getYRot() * (float) (Math.PI / 180.0);
        Vec3 push = new Vec3(Mth.sin(yaw), 0.8, -Mth.cos(yaw)).normalize().scale(strength);
        target.setDeltaMovement(motion.x / 2.0 - push.x,
                target.onGround() ? Math.min(0.4, motion.y / 2.0 + strength) : motion.y,
                motion.z / 2.0 - push.z);
        target.hurtMarked = true;
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 2));
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getDefaultAttributeModifiers(slot);
        }
        float addDamage = stack.hasTag() ? stack.getTag().getFloat(ADD_DAMAGE) : 0.0F;
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", BASE_DAMAGE + addDamage, AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.5, AttributeModifier.Operation.ADDITION));
        modifiers.put(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(KNOCKBACK_UUID, "Tool modifier", 2.0, AttributeModifier.Operation.ADDITION));
        return modifiers;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity living && !living.hasEffect(BakeriesMobEffects.ENJOY)
                && stack.hasTag() && stack.getTag().getFloat(ADD_DAMAGE) != 0.0F) {
            stack.getTag().putFloat(ADD_DAMAGE, 0.0F);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    /** Called by the Enjoy effect every tick for whoever holds the baguette. */
    public static void applyEnjoyBonus(ItemStack stack, int amplifier) {
        float bonus = (float) (DAMAGE_PER_ENJOY_LEVEL * (amplifier + 1));
        if (stack.getOrCreateTag().getFloat(ADD_DAMAGE) != bonus) {
            stack.getOrCreateTag().putFloat(ADD_DAMAGE, bonus);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.bakeries.eternal_baguette.tips").withStyle(ChatFormatting.GRAY));
    }
}
