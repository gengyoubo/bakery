package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.items.BreadKnifeItem;
import com.renyigesai.bakeries.items.DescriptionItem;
import com.renyigesai.bakeries.items.FoodEffectBlockItem;
import com.renyigesai.bakeries.items.FoodEffectItem;
import com.renyigesai.bakeries.items.FoodEffectTooltip;
import com.renyigesai.bakeries.items.FlourSieveItem;
import com.renyigesai.bakeries.items.WholeEggItem;
import com.renyigesai.bakeries.items.EternalBaguetteItem;
import com.renyigesai.bakeries.items.ContainerFoodBlockItem;
import com.renyigesai.bakeries.items.GarlicBaguetteItem;
import com.renyigesai.bakeries.items.MouldBlockItem;
import com.renyigesai.bakeries.items.RepeatEatBlockItem;
import com.renyigesai.bakeries.items.RoundMouldItem;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceKey;

@SuppressWarnings("unused")
public final class BakeriesItems {
    public static final CreativeModeTab BAKERIES_TAB;
    public static final CreativeModeTab SEMI_FINISHED_PRODUCT_TAB;
    public static final Item FLOUR, WHOLE_WHEAT_FLOUR, COCOA_POWDER, MATCHA_POWDER, SALT, BOTTLE_YEAST, BOTTLE_MILK, BOTTLE_CREAM, BOTTLE_BUTTER, BUTTER_CUBE, FOAMED_CREAM, CHEESE_CREAM, BUTTER_FLOUR_SAND, HONEY_BUTTER, WHOLE_EGG, RAW_PROTEIN, RAW_EGG_YOLK, SALT_YOLK, CHEESE_CUBE, FRESH_CHEESE_CUBE, BROWN_SUGAR_CUBE, COFFEE_BEAN, GROUND_COFFEE, MOKA_POT_FILL, BEARNAISE, OLIVE_OIL, MEAT_FLOSS, ICE_CUBES, SCONE, OLIVE, TARO, COOKED_TARO, MASHED_TARO, SLICED_TOAST, BAKE_SLICED_TOAST, HONEY_BUTTER_SPREAD_TOAST, SLICED_CHEESE_COCOA_TOAST, COUNTRY_BREAD_SLICE, HONEY_BUTTER_SPREAD_COUNTRY_BREAD, BROWN_SUGAR_LATTE, ICED_AMERICAN, ICED_LATTE, MATCHA_LATTE, ORANGE_AMERICAN, TARO_MILK, SWEET_DOUGH, COCOA_DOUGH, SALTED_DOUGH, WHOLE_WHEAT_DOUGH, COCOA_DOUGH_FERMENTATION, SALTED_DOUGH_FERMENTATION, SWEET_DOUGH_FERMENTATION, WHOLE_WHEAT_DOUGH_FERMENTATION, PASTRY, EGG_TART_SHELL, RAW_EGG_TART, BAGEL_DOUGH, WHOLE_WHEAT_BAGEL_DOUGH, ROUND_BREAD_DOUGH, BROWN_SUGAR_ROLL_DOUGH, PINEAPPLE_BUN_DOUGH, CROISSANT_DOUGH, SALT_CROISSANT_DOUGH, BAGUETTE_DOUGH, CIABATTA_DOUGH, FOCACCIA_DOUGH, COUNTRY_BREAD_DOUGH, MOULD_TOAST_DOUGH, MOULD_CHEESE_COCOA_TOAST_DOUGH, RICE_BREAD_DOUGH, OVEN, TOASTER, BLENDER, FERMENTATION_BOX, FERMENTATION_TANK, DOUGH_CRAFTING_TABLE, CUPBOARD, WOOD_COUNTER, COFFEE_TABLE, CHEESE_TANK, MILK_TANK, YEAST_TANK, MOKA_POT, BREAD_RACK, GLASS_BREAD_RACK, BREAD_BASKET, GLASS_CABINET_DOOR, MENU, MOULD, DRINK_CUP, TOAST, CHEESE_COCOA_TOAST, BAGEL, BAGUETTE, CROISSANT, ROUND_BREAD, RICE_BREAD, WHOLE_WHEAT_BAGEL, PINEAPPLE_BUN, FOCACCIA, CIABATTA, EGG_TART, SALT_CROISSANT, COUNTRY_BREAD, CREAM_BINGLE_COFFEE, MATCHA_PARFAIT, COFFEE_PLANT, TOMATO, MIX_BLOCK, BAGEL_FILLED_SAUCE, BAGUETTE_WITH_FILLING, BAKING_GUIDE, BERRY_BAGEL, BREAD_KNIFE, CASH_REGISTER_COMPUTER, CHEESE_CREAM_BREAD, DEEPSLATE_SALT_ORE, DIRTY_CHOCO_CROISSANT, DOUGH_CRAFTING, FLAT_CROISSANT, FLOUR_BAG, FLOUR_SIEVE, MEAT_FLOSS_BREAD_ROLL, PINEAPPLE_OIL, RAW_COFFEE_BEAN, RAW_SALT_BLOCK, SALT_ORE, SOFA_LIGHT_GRAY, SOFA_RED, SOFA_WHITE, TARO_SALT_YOLK_BREAD, TOMATO_CHEESE_CROISSANT_SANDWICH, WHOLE_WHEAT_FLOUR_BAG, WOOD_TRAY;
    public static final Item BROWN_SUGAR_ROLL;
    // Ported from Forge 1.3.6 (scattered foods, tools and kitchen blocks)
    public static final Item BERRY_BREAD, BERRY_BREAD_DOUGH, GARLIC_FLAVORED_BAGUETTE, SALMON_SANDWICH, DONGPO_PORK_HAMBURG,
            CREAM_MUSHROOM_SOUP_WITH_BAGUETTE, BUTTER_LATTE, MILK_TEA, SWEET_BERRIES_JAM, BOXED_EGG, LONG_EGG_TART,
            CREAM_PUMPKIN_PIE, CREAM_PUMPKIN_PIE_DOUGH, RAW_PUMPKIN_PIE, RAW_TARE_CRUST, CRISPY_DOUGH, SCONE_DOUGH, COARSE_SALT,
            TRAY_SCONE, BREAD_HOLDERS, BLACK_WHITE_CONCRETE, MOULD_TOAST, MOULD_CHEESE_COCOA_TOAST,
            ETERNAL_BAGUETTE, NETHERITE_BREAD_KNIFE, ROUND_MOULD, MUSIC_DISC_BAKING_IN_PROGRESS;
    // Ported from Forge 1.3.6: cake system (part 1)
    public static final Item CAKE_BASE, RED_VELVET_CAKE_BASE, CREAM_CAKE, TIRAMISU, CARROT_CAKE, RED_VELVET_CAKE, MATCHA_CAKE,
            BASQUE_CAKE, NIANGAO_CAKE, TARO_CAKE, POUND_CAKE, CUP_CAKE, PAPER_CUP, SILICONE_PAPER, MOULD_TWO,
            MOULD_CAKE_BASE, MOULD_CARROT_CAKE, MOULD_RED_VELVET_CAKE, MOULD_MATCHA_CAKE, MOULD_BASQUE_CAKE, MOULD_POUND_CAKE,
            CREAM_CAKE_CUBE, CUT_CAKE_BASE, SOAK_COFFEE_CUT_CAKE_BASE, SLICED_POUND_CAKE, PAPER_CUP_CAKE_PASTE, COCOA_CREAM,
            MOULD_CAKE_PASTE, MOULD_CARROT_CAKE_PASTE, MOULD_RED_VELVET_CAKE_PASTE, MOULD_MATCHA_CAKE_PASTE, MOULD_POUND_CAKE_PASTE,
            MOULD_BASQUE_CAKE_PASTE, CAKE_PASTE_BUCKET, EGG_YOLK_PASTE_BUCKET, FOAMED_PROTEIN_BUCKET;
    static {
        BREAD_KNIFE = register("bread_knife", new BreadKnifeItem(new Item.Properties().durability(250)));
        BAKING_GUIDE = register("baking_guide", new BreadKnifeItem(new Item.Properties().durability(250)));
        FLOUR_SIEVE = register("flour_sieve", new FlourSieveItem(new Item.Properties().durability(128)));
        FLOUR = register("flour");
        WHOLE_WHEAT_FLOUR = register("whole_wheat_flour");
        COCOA_POWDER = register("cocoa_powder");
        MATCHA_POWDER = register("matcha_powder");
        SALT = register("salt");
        BROWN_SUGAR_CUBE = register("brown_sugar_cube");
        ICE_CUBES = register("ice_cubes");
        RAW_COFFEE_BEAN = register("raw_coffee_bean", new DescriptionItem(new Item.Properties(), "bakeries.raw_coffee_bean.description"));
        COFFEE_BEAN = register("coffee_bean");
        GROUND_COFFEE = register("ground_coffee");
        OLIVE = register("olive", new DescriptionItem(food(2, 0.5F, false), "bakeries.olive.description"));
        MEAT_FLOSS = register("meat_floss", food(2, 0.8F, false));
        BOTTLE_YEAST = register("bottle_yeast", new DescriptionItem(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE), "bakeries.bottle_yeast.description"));
        BOTTLE_MILK = register("bottle_milk", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
        BOTTLE_CREAM = register("bottle_cream", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
        BOTTLE_BUTTER = register("bottle_butter", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
        BUTTER_CUBE = register("butter_cube");
        FOAMED_CREAM = register("foamed_cream", food(1, 1.0F, true));
        CHEESE_CREAM = register("cheese_cream", food(1, 1.0F, true));
        CHEESE_CUBE = register("cheese_cube", new DescriptionItem(food(1, 1.0F, false), "bakeries.cheese_cube.description"));
        FRESH_CHEESE_CUBE = register("fresh_cheese_cube", food(1, 1.0F, false));
        WHOLE_EGG = register("whole_egg", new WholeEggItem());
        RAW_PROTEIN = register("raw_protein");
        RAW_EGG_YOLK = register("raw_egg_yolk");
        SALT_YOLK = register("salt_yolk");
        BUTTER_FLOUR_SAND = register("butter_flour_sand");
        HONEY_BUTTER = register("honey_butter");
        BEARNAISE = register("bearnaise");
        OLIVE_OIL = register("olive_oil");
        TARO = registerBlock("taro", BakeriesBlocks.TARO);
        COOKED_TARO = register("cooked_taro", food(6, 0.6F, false));
        MASHED_TARO = register("mashed_taro", food(1, 0.5F, false));
        MOKA_POT_FILL = registerBlock("moka_pot_fill", BakeriesBlocks.MOKA_POT_FILL);
        BROWN_SUGAR_LATTE = register("brown_sugar_latte", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0)));
        ICED_AMERICAN = register("iced_american", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0)));
        ICED_LATTE = register("iced_latte", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0)));
        MATCHA_LATTE = register("matcha_latte", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0)));
        ORANGE_AMERICAN = register("orange_american", food(1, 0.5F, true));
        TARO_MILK = register("taro_milk", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0)));
        SWEET_DOUGH = register("sweet_dough");
        COCOA_DOUGH = register("cocoa_dough");
        SALTED_DOUGH = register("salted_dough");
        WHOLE_WHEAT_DOUGH = register("whole_wheat_dough");
        COCOA_DOUGH_FERMENTATION = register("cocoa_dough_fermentation");
        SALTED_DOUGH_FERMENTATION = register("salted_dough_fermentation");
        SWEET_DOUGH_FERMENTATION = register("sweet_dough_fermentation");
        WHOLE_WHEAT_DOUGH_FERMENTATION = register("whole_wheat_dough_fermentation");
        PASTRY = register("pastry");
        EGG_TART_SHELL = register("egg_tart_shell");
        RAW_EGG_TART = register("raw_egg_tart");
        BAGEL_DOUGH = register("bagel_dough");
        WHOLE_WHEAT_BAGEL_DOUGH = register("whole_wheat_bagel_dough");
        ROUND_BREAD_DOUGH = register("round_bread_dough");
        BROWN_SUGAR_ROLL_DOUGH = register("brown_sugar_roll_dough");
        PINEAPPLE_BUN_DOUGH = register("pineapple_bun_dough");
        CROISSANT_DOUGH = register("croissant_dough");
        SALT_CROISSANT_DOUGH = register("salt_croissant_dough");
        BAGUETTE_DOUGH = register("baguette_dough");
        CIABATTA_DOUGH = register("ciabatta_dough");
        FOCACCIA_DOUGH = register("focaccia_dough");
        COUNTRY_BREAD_DOUGH = register("country_bread_dough");
        MOULD_TOAST_DOUGH = register("mould_toast_dough");
        MOULD_CHEESE_COCOA_TOAST_DOUGH = register("mould_cheese_cocoa_toast_dough");
        RICE_BREAD_DOUGH = register("rice_bread_dough");
        DOUGH_CRAFTING = register("dough_crafting");
        OVEN = registerBlock("oven", BakeriesBlocks.OVEN);
        TOASTER = registerBlock("toaster", BakeriesBlocks.TOASTER);
        BLENDER = registerBlock("blender", BakeriesBlocks.BLENDER);
        FERMENTATION_BOX = registerBlock("fermentation_box", BakeriesBlocks.FERMENTATION_BOX);
        FERMENTATION_TANK = registerBlock("fermentation_tank", BakeriesBlocks.FERMENTATION_TANK);
        DOUGH_CRAFTING_TABLE = registerBlock("dough_crafting_table", BakeriesBlocks.DOUGH_CRAFTING_TABLE);
        CUPBOARD = registerBlock("cupboard", BakeriesBlocks.CUPBOARD);
        WOOD_COUNTER = registerBlock("wood_counter", BakeriesBlocks.WOOD_COUNTER);
        COFFEE_TABLE = registerBlock("coffee_table", BakeriesBlocks.COFFEE_TABLE);
        CHEESE_TANK = registerBlock("cheese_tank", BakeriesBlocks.CHEESE_TANK);
        MILK_TANK = registerBlock("milk_tank", BakeriesBlocks.MILK_TANK);
        YEAST_TANK = registerBlock("yeast_tank", BakeriesBlocks.YEAST_TANK);
        MOKA_POT = registerBlock("moka_pot", BakeriesBlocks.MOKA_POT);
        MIX_BLOCK = registerBlock("mix_block", BakeriesBlocks.MIX_BLOCK);
        MOULD = registerBlock("mould", BakeriesBlocks.MOULD);
        DRINK_CUP = registerBlock("drink_cup", BakeriesBlocks.DRINK_CUP, new Item.Properties().stacksTo(1));
        BREAD_RACK = registerBlock("bread_rack", BakeriesBlocks.BREAD_RACK);
        GLASS_BREAD_RACK = registerBlock("glass_bread_rack", BakeriesBlocks.GLASS_BREAD_RACK);
        BREAD_BASKET = registerBlock("bread_basket", BakeriesBlocks.BREAD_BASKET);
        GLASS_CABINET_DOOR = registerBlock("glass_cabinet_door", BakeriesBlocks.GLASS_CABINET_DOOR);
        MENU = registerBlock("menu", BakeriesBlocks.MENU);
        CASH_REGISTER_COMPUTER = registerBlock("cash_register_computer", BakeriesBlocks.CASH_REGISTER_COMPUTER);
        FLOUR_BAG = registerBlock("flour_bag", BakeriesBlocks.FLOUR_BAG);
        WHOLE_WHEAT_FLOUR_BAG = registerBlock("whole_wheat_flour_bag", BakeriesBlocks.WHOLE_WHEAT_FLOUR_BAG);
        WOOD_TRAY = registerBlock("wood_tray", BakeriesBlocks.WOOD_TRAY);
        SOFA_LIGHT_GRAY = registerBlock("sofa_light_gray", BakeriesBlocks.SOFA_LIGHT_GRAY);
        SOFA_RED = registerBlock("sofa_red", BakeriesBlocks.SOFA_RED);
        SOFA_WHITE = registerBlock("sofa_white", BakeriesBlocks.SOFA_WHITE);
        TOAST = registerBlock("toast", BakeriesBlocks.TOAST);
        CHEESE_COCOA_TOAST = registerBlock("cheese_cocoa_toast", BakeriesBlocks.CHEESE_COCOA_TOAST);
        BAGEL = registerBlock("bagel", BakeriesBlocks.BAGEL, food(6, 0.5F, false));
        BAGUETTE = registerBlock("baguette", BakeriesBlocks.BAGUETTE, food(8, 0.25F, false));
        CROISSANT = registerBlockWithEffects("croissant", BakeriesBlocks.CROISSANT, food(6, 1.0F, false, effect(BakeriesMobEffects.ENJOY, 60, 0)), effect(BakeriesMobEffects.ENJOY, 60, 0));
        ROUND_BREAD = registerBlock("round_bread", BakeriesBlocks.ROUND_BREAD, food(3, 0.6F, false));
        RICE_BREAD = registerBlock("rice_bread", BakeriesBlocks.RICE_BREAD, food(12, 0.4F, false));
        WHOLE_WHEAT_BAGEL = registerBlockWithEffects("whole_wheat_bagel", BakeriesBlocks.WHOLE_WHEAT_BAGEL, food(8, 0.35F, false, effect(MobEffects.SATURATION, 1, 0)), effect(MobEffects.SATURATION, 1, 0));
        PINEAPPLE_BUN = registerBlockWithEffects("pineapple_bun", BakeriesBlocks.PINEAPPLE_BUN, food(6, 1.5F, false, effect(BakeriesMobEffects.ENJOY, 60, 1)), effect(BakeriesMobEffects.ENJOY, 60, 1));
        FOCACCIA = registerBlockWithEffects("focaccia", BakeriesBlocks.FOCACCIA, food(8, 1.0F, false, effect(BakeriesMobEffects.ENJOY, 30, 1)), effect(BakeriesMobEffects.ENJOY, 30, 1));
        CIABATTA = registerBlock("ciabatta", BakeriesBlocks.CIABATTA, food(4, 0.4F, false));
        EGG_TART = registerBlock("egg_tart", BakeriesBlocks.EGG_TART, food(8, 0.5F, false));
        SALT_CROISSANT = registerBlockWithEffects("salt_croissant", BakeriesBlocks.SALT_CROISSANT, food(6, 1.0F, false, effect(BakeriesMobEffects.ENJOY, 30, 0), effect(MobEffects.DAMAGE_BOOST, 30, 0)), effect(BakeriesMobEffects.ENJOY, 30, 0), effect(MobEffects.DAMAGE_BOOST, 30, 0));
        COUNTRY_BREAD = registerBlock("country_bread", BakeriesBlocks.COUNTRY_BREAD, food(4, 0.4F, false));
        BROWN_SUGAR_ROLL = registerBlockWithEffects("brown_sugar_roll", BakeriesBlocks.BROWN_SUGAR_ROLL, food(7, 0.7F, false, effect(BakeriesMobEffects.ENJOY, 30, 0)), effect(BakeriesMobEffects.ENJOY, 30, 0));
        BAGEL_FILLED_SAUCE = registerBlock("bagel_filled_sauce", BakeriesBlocks.BAGEL_FILLED_SAUCE, food(8, 0.6F, false));
        BAGUETTE_WITH_FILLING = registerBlock("baguette_with_filling", BakeriesBlocks.BAGUETTE_WITH_FILLING, food(10, 0.6F, false));
        BERRY_BAGEL = registerBlockWithEffects("berry_bagel", BakeriesBlocks.BERRY_BAGEL, food(8, 0.6F, false, effect(MobEffects.REGENERATION, 60, 0), effect(BakeriesMobEffects.BERRY_SOUR, 30, 0)), effect(MobEffects.REGENERATION, 60, 0), effect(BakeriesMobEffects.BERRY_SOUR, 30, 0));
        CHEESE_CREAM_BREAD = registerBlockWithEffects("cheese_cream_bread", BakeriesBlocks.CHEESE_CREAM_BREAD, food(8, 0.6F, false, effect(BakeriesMobEffects.CHEESE_POWER, 30, 0)), effect(BakeriesMobEffects.CHEESE_POWER, 30, 0));
        DIRTY_CHOCO_CROISSANT = registerBlockWithEffects("dirty_choco_croissant", BakeriesBlocks.DIRTY_CHOCO_CROISSANT, food(8, 0.6F, false, effect(BakeriesMobEffects.ENJOY, 60, 0), effect(BakeriesMobEffects.COCOA_MANIA, 60, 0)), effect(BakeriesMobEffects.ENJOY, 60, 0), effect(BakeriesMobEffects.COCOA_MANIA, 60, 0));
        FLAT_CROISSANT = register("flat_croissant", foodEffectItem(food(6, 0.6F, false, effect(BakeriesMobEffects.ENJOY, 30, 0), effect(MobEffects.DAMAGE_RESISTANCE, 30, 0)), effect(BakeriesMobEffects.ENJOY, 30, 0), effect(MobEffects.DAMAGE_RESISTANCE, 30, 0)));
        MEAT_FLOSS_BREAD_ROLL = registerBlock("meat_floss_bread_roll", BakeriesBlocks.MEAT_FLOSS_BREAD_ROLL, food(10, 0.7F, false));
        PINEAPPLE_OIL = registerBlockWithEffects("pineapple_oil", BakeriesBlocks.PINEAPPLE_OIL, food(10, 0.8F, false, effect(BakeriesMobEffects.ENJOY, 90, 1)), effect(BakeriesMobEffects.ENJOY, 90, 1));
        TARO_SALT_YOLK_BREAD = registerBlockWithEffects("taro_salt_yolk_bread", BakeriesBlocks.TARO_SALT_YOLK_BREAD, food(10, 0.7F, false, effect(BakeriesMobEffects.ENJOY, 90, 0)), effect(BakeriesMobEffects.ENJOY, 90, 0));
        TOMATO_CHEESE_CROISSANT_SANDWICH = registerBlockWithEffects("tomato_cheese_croissant_sandwich", BakeriesBlocks.TOMATO_CHEESE_CROISSANT_SANDWICH, food(10, 0.8F, false, effect(BakeriesMobEffects.ENJOY, 300, 2)), effect(BakeriesMobEffects.ENJOY, 300, 2));
        CREAM_BINGLE_COFFEE = registerBlockWithEffects("cream_bingle_coffee", BakeriesBlocks.CREAM_BINGLE_COFFEE, food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0));
        MATCHA_PARFAIT = registerBlockWithEffects("matcha_parfait", BakeriesBlocks.MATCHA_PARFAIT, food(9, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0));
        SCONE = register("scone", food(4, 0.5F, true));
        SLICED_TOAST = register("sliced_toast", food(4, 0.4F, true));
        BAKE_SLICED_TOAST = register("bake_sliced_toast", food(4, 0.4F, true));
        HONEY_BUTTER_SPREAD_TOAST = register("honey_butter_spread_toast", food(8, 0.5F, true));
        SLICED_CHEESE_COCOA_TOAST = register("sliced_cheese_cocoa_toast", food(4, 0.8F, true));
        COUNTRY_BREAD_SLICE = register("country_bread_slice", food(4, 0.4F, false));
        HONEY_BUTTER_SPREAD_COUNTRY_BREAD = register("honey_butter_spread_country_bread", food(6, 0.65F, true));
        COFFEE_PLANT = registerBlock("coffee_plant", BakeriesBlocks.COFFEE_PLANT);
        TOMATO = registerBlock("tomato", BakeriesBlocks.TOMATO, food(2, 0.5F, false));
        RAW_SALT_BLOCK = registerBlock("raw_salt_block", BakeriesBlocks.RAW_SALT_BLOCK);
        SALT_ORE = registerBlock("salt_ore", BakeriesBlocks.SALT_ORE);
        DEEPSLATE_SALT_ORE = registerBlock("deepslate_salt_ore", BakeriesBlocks.DEEPSLATE_SALT_ORE);

        // ---- Ported from Forge 1.3.6: scattered foods, tools and kitchen blocks ----
        BERRY_BREAD = registerBlock("berry_bread", BakeriesBlocks.BERRY_BREAD, food(3, 0.7F, false));
        BERRY_BREAD_DOUGH = register("berry_bread_dough");
        GARLIC_FLAVORED_BAGUETTE = register("garlic_flavored_baguette", new GarlicBaguetteItem(BakeriesBlocks.GARLIC_FLAVORED_BAGUETTE,
                food(8, 0.5F, false, effect(MobEffects.DAMAGE_RESISTANCE, 30, 0)).rarity(BakeriesRarity.ADVANCED), effect(MobEffects.DAMAGE_RESISTANCE, 30, 0)));
        SALMON_SANDWICH = registerBlockWithEffects("salmon_sandwich", BakeriesBlocks.SALMON_SANDWICH,
                food(10, 0.85F, true, effect(MobEffects.REGENERATION, 30, 0)).rarity(BakeriesRarity.ADVANCED), effect(MobEffects.REGENERATION, 30, 0));
        DONGPO_PORK_HAMBURG = registerBlock("dongpo_pork_hamburg", BakeriesBlocks.DONGPO_PORK_HAMBURG, food(15, 0.5F, true).rarity(BakeriesRarity.ADVANCED));
        CREAM_MUSHROOM_SOUP_WITH_BAGUETTE = registerBlock("cream_mushroom_soup_with_baguette", BakeriesBlocks.CREAM_MUSHROOM_SOUP_WITH_BAGUETTE,
                food(20, 0.75F, true).stacksTo(16).craftRemainder(Items.FLOWER_POT));
        BUTTER_LATTE = register("butter_latte", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0),
                new FoodEffectTooltip.Entry(MobEffects.SATURATION, 5, 0)), effect(MobEffects.MOVEMENT_SPEED, 30, 0)));
        MILK_TEA = register("milk_tea", foodEffectItem(food(1, 0.5F, true, effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0)),
                effect(MobEffects.MOVEMENT_SPEED, 30, 0), effect(MobEffects.DIG_SPEED, 30, 0), effect(BakeriesMobEffects.TEA_ASTRINGENT, 60, 0)));
        SWEET_BERRIES_JAM = registerBlock("sweet_berries_jam", BakeriesBlocks.SWEET_BERRIES_JAM, new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
        BOXED_EGG = registerBlock("boxed_egg", BakeriesBlocks.BOXED_EGG);
        LONG_EGG_TART = register("long_egg_tart", new RepeatEatBlockItem(BakeriesBlocks.LONG_EGG_TART, food(8, 0.5F, false), 4));
        CREAM_PUMPKIN_PIE = registerBlock("cream_pumpkin_pie", BakeriesBlocks.CREAM_PUMPKIN_PIE);
        CREAM_PUMPKIN_PIE_DOUGH = register("cream_pumpkin_pie_dough");
        RAW_PUMPKIN_PIE = register("raw_pumpkin_pie");
        RAW_TARE_CRUST = register("raw_tare_crust");
        CRISPY_DOUGH = register("crispy_dough");
        SCONE_DOUGH = register("scone_dough");
        COARSE_SALT = register("coarse_salt");
        TRAY_SCONE = registerBlock("tray_scone", BakeriesBlocks.TRAY_SCONE, new Item.Properties().stacksTo(1).craftRemainder(WOOD_TRAY));
        BREAD_HOLDERS = registerBlock("bread_holders", BakeriesBlocks.BREAD_HOLDERS);
        BLACK_WHITE_CONCRETE = registerBlock("black_white_concrete", BakeriesBlocks.BLACK_WHITE_CONCRETE);
        MOULD_TOAST = register("mould_toast", new MouldBlockItem(BakeriesBlocks.MOULD_TOAST, new Item.Properties()));
        MOULD_CHEESE_COCOA_TOAST = register("mould_cheese_cocoa_toast", new MouldBlockItem(BakeriesBlocks.MOULD_CHEESE_COCOA_TOAST, new Item.Properties()));
        ETERNAL_BAGUETTE = register("eternal_baguette", new EternalBaguetteItem());
        NETHERITE_BREAD_KNIFE = register("netherite_bread_knife", new BreadKnifeItem(0.5F, -0.2F, Tiers.NETHERITE, new Item.Properties()));
        ROUND_MOULD = register("round_mould", new RoundMouldItem(new Item.Properties().durability(64)));
        MUSIC_DISC_BAKING_IN_PROGRESS = register("music_disc_baking_in_progress",
                new RecordItem(15, BakeriesSounds.MUSIC_DISC_BAKING_IN_PROGRESS, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 168) { });

        // ---- Ported from Forge 1.3.6: cake system (part 1) ----
        CAKE_BASE = registerBlock("cake_base", BakeriesBlocks.CAKE_BASE, new Item.Properties().stacksTo(16));
        RED_VELVET_CAKE_BASE = registerBlock("red_velvet_cake_base", BakeriesBlocks.RED_VELVET_CAKE_BASE, new Item.Properties().stacksTo(16));
        CREAM_CAKE = registerBlock("cream_cake", BakeriesBlocks.CREAM_CAKE, new Item.Properties().stacksTo(16));
        TIRAMISU = registerBlock("tiramisu", BakeriesBlocks.TIRAMISU, new Item.Properties().stacksTo(16));
        CARROT_CAKE = registerBlock("carrot_cake", BakeriesBlocks.CARROT_CAKE, new Item.Properties().stacksTo(16));
        RED_VELVET_CAKE = registerBlock("red_velvet_cake", BakeriesBlocks.RED_VELVET_CAKE, new Item.Properties().stacksTo(16));
        MATCHA_CAKE = registerBlock("matcha_cake", BakeriesBlocks.MATCHA_CAKE, new Item.Properties().stacksTo(16));
        BASQUE_CAKE = registerBlock("basque_cake", BakeriesBlocks.BASQUE_CAKE, new Item.Properties().stacksTo(16));
        NIANGAO_CAKE = registerBlock("niangao_cake", BakeriesBlocks.NIANGAO_CAKE, new Item.Properties().stacksTo(16));
        TARO_CAKE = registerBlockWithEffects("taro_cake", BakeriesBlocks.TARO_CAKE,
                food(8, 1.0F, true, effect(BakeriesMobEffects.SOFT, 90, 0)).rarity(BakeriesRarity.TARO), effect(BakeriesMobEffects.SOFT, 90, 0));
        POUND_CAKE = registerBlock("pound_cake", BakeriesBlocks.POUND_CAKE);
        PAPER_CUP = registerBlock("paper_cup", BakeriesBlocks.PAPER_CUP);
        CUP_CAKE = register("cup_cake", new ContainerFoodBlockItem(BakeriesBlocks.CUP_CAKE,
                food(4, 0.5F, true, effect(BakeriesMobEffects.SOFT, 60, 0)).craftRemainder(PAPER_CUP), () -> PAPER_CUP, effect(BakeriesMobEffects.SOFT, 60, 0)));
        SILICONE_PAPER = registerBlock("silicone_paper", BakeriesBlocks.SILICONE_PAPER);
        MOULD_TWO = registerBlock("mould_two", BakeriesBlocks.MOULD_TWO);
        MOULD_CAKE_BASE = register("mould_cake_base", new MouldBlockItem(BakeriesBlocks.MOULD_CAKE_BASE, new Item.Properties()));
        MOULD_CARROT_CAKE = register("mould_carrot_cake", new MouldBlockItem(BakeriesBlocks.MOULD_CARROT_CAKE, new Item.Properties()));
        MOULD_RED_VELVET_CAKE = register("mould_red_velvet_cake", new MouldBlockItem(BakeriesBlocks.MOULD_RED_VELVET_CAKE, new Item.Properties()));
        MOULD_MATCHA_CAKE = register("mould_matcha_cake", new MouldBlockItem(BakeriesBlocks.MOULD_MATCHA_CAKE, new Item.Properties()));
        MOULD_BASQUE_CAKE = register("mould_basque_cake", new MouldBlockItem(BakeriesBlocks.MOULD_BASQUE_CAKE, new Item.Properties()));
        MOULD_POUND_CAKE = register("mould_pound_cake", new MouldBlockItem(BakeriesBlocks.MOULD_POUND_CAKE, new Item.Properties()));
        CREAM_CAKE_CUBE = register("cream_cake_cube", foodEffectItem(food(5, 0.4F, true, effect(BakeriesMobEffects.SOFT, 60, 0), effect(MobEffects.REGENERATION, 60, 0)),
                effect(BakeriesMobEffects.SOFT, 60, 0), effect(MobEffects.REGENERATION, 60, 0)));
        CUT_CAKE_BASE = register("cut_cake_base");
        SOAK_COFFEE_CUT_CAKE_BASE = register("soak_coffee_cut_cake_base");
        SLICED_POUND_CAKE = register("sliced_pound_cake", food(1, 1.0F, true));
        COCOA_CREAM = register("cocoa_cream", foodEffectItem(food(1, 1.0F, true, effect(BakeriesMobEffects.COCOA_MANIA, 20, 0)), effect(BakeriesMobEffects.COCOA_MANIA, 20, 0)));
        PAPER_CUP_CAKE_PASTE = register("paper_cup_cake_paste");
        MOULD_CAKE_PASTE = register("mould_cake_paste");
        MOULD_CARROT_CAKE_PASTE = register("mould_carrot_cake_paste");
        MOULD_RED_VELVET_CAKE_PASTE = register("mould_red_velvet_cake_paste");
        MOULD_MATCHA_CAKE_PASTE = register("mould_matcha_cake_paste");
        MOULD_POUND_CAKE_PASTE = register("mould_pound_cake_paste");
        MOULD_BASQUE_CAKE_PASTE = register("mould_basque_cake_paste");
        CAKE_PASTE_BUCKET = register("cake_paste_bucket", new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
        EGG_YOLK_PASTE_BUCKET = register("egg_yolk_paste_bucket", new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));
        FOAMED_PROTEIN_BUCKET = register("foamed_protein_bucket", new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET));

        BAKERIES_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(BakeriesMod.MODID, "main"),
                FabricItemGroup.builder()
                        .title(Component.translatable("item_group.bakeries.bakeries_tab"))
                        .icon(() -> new ItemStack(OVEN))
                        .displayItems((parameters, entries) -> {
                            for (Item item : allItems()) {
                                entries.accept(item);
                            }
                        })
                        .build()
        );
        SEMI_FINISHED_PRODUCT_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                new ResourceLocation(BakeriesMod.MODID, "semi_finished_product"),
                FabricItemGroup.builder()
                        .title(Component.translatable("item_group.bakeries.bakeries_sfp_tab"))
                        .icon(() -> new ItemStack(SWEET_DOUGH))
                        .displayItems((parameters, entries) -> {
                                    entries.accept(SWEET_DOUGH);
                                    entries.accept(COCOA_DOUGH);
                                    entries.accept(SALTED_DOUGH);
                                    entries.accept(WHOLE_WHEAT_DOUGH);
                                    entries.accept(BAGEL_DOUGH);
                                    entries.accept(WHOLE_WHEAT_BAGEL_DOUGH);
                                    entries.accept(ROUND_BREAD_DOUGH);
                                    entries.accept(BROWN_SUGAR_ROLL_DOUGH);
                                    entries.accept(PINEAPPLE_BUN_DOUGH);
                                    entries.accept(CROISSANT_DOUGH);
                                    entries.accept(SALT_CROISSANT_DOUGH);
                                    entries.accept(BAGUETTE_DOUGH);
                                    entries.accept(CIABATTA_DOUGH);
                                    entries.accept(FOCACCIA_DOUGH);
                                    entries.accept(COUNTRY_BREAD_DOUGH);
                                    entries.accept(MOULD_TOAST_DOUGH);
                                    entries.accept(MOULD_CHEESE_COCOA_TOAST_DOUGH);
                                    entries.accept(RICE_BREAD_DOUGH);
                                    entries.accept(BERRY_BREAD_DOUGH);
                                    entries.accept(SCONE_DOUGH);
                                    entries.accept(CRISPY_DOUGH);
                                    entries.accept(RAW_TARE_CRUST);
                                    entries.accept(RAW_PUMPKIN_PIE);
                                    entries.accept(CREAM_PUMPKIN_PIE_DOUGH);
                                    entries.accept(PAPER_CUP_CAKE_PASTE);
                                    entries.accept(MOULD_CAKE_PASTE);
                                    entries.accept(MOULD_CARROT_CAKE_PASTE);
                                    entries.accept(MOULD_RED_VELVET_CAKE_PASTE);
                                    entries.accept(MOULD_MATCHA_CAKE_PASTE);
                                    entries.accept(MOULD_POUND_CAKE_PASTE);
                                    entries.accept(MOULD_BASQUE_CAKE_PASTE);
                                }
                        )
                        .build()
        );
    }

    private BakeriesItems() {
    }

    public static void init() {
        registerTabEntries(
                CreativeModeTabs.INGREDIENTS,
                FLOUR, WHOLE_WHEAT_FLOUR, COCOA_POWDER, MATCHA_POWDER, SALT,
                BOTTLE_YEAST, BOTTLE_MILK, BOTTLE_CREAM, BOTTLE_BUTTER, BUTTER_CUBE,
                FOAMED_CREAM, CHEESE_CREAM, BUTTER_FLOUR_SAND, HONEY_BUTTER, WHOLE_EGG,
                RAW_PROTEIN, RAW_EGG_YOLK, SALT_YOLK, CHEESE_CUBE, FRESH_CHEESE_CUBE,
                BROWN_SUGAR_CUBE, COFFEE_BEAN, GROUND_COFFEE, MOKA_POT_FILL, BEARNAISE, OLIVE_OIL,
                MEAT_FLOSS, OLIVE, TARO, COOKED_TARO, MASHED_TARO,
                SWEET_DOUGH, COCOA_DOUGH, SALTED_DOUGH, WHOLE_WHEAT_DOUGH,
                COCOA_DOUGH_FERMENTATION, SALTED_DOUGH_FERMENTATION, SWEET_DOUGH_FERMENTATION, WHOLE_WHEAT_DOUGH_FERMENTATION,
                PASTRY,
                EGG_TART_SHELL, RAW_EGG_TART, BAGEL_DOUGH, WHOLE_WHEAT_BAGEL_DOUGH,
                ROUND_BREAD_DOUGH, BROWN_SUGAR_ROLL_DOUGH, PINEAPPLE_BUN_DOUGH,
                CROISSANT_DOUGH, SALT_CROISSANT_DOUGH, BAGUETTE_DOUGH, CIABATTA_DOUGH,
                FOCACCIA_DOUGH, COUNTRY_BREAD_DOUGH,
                MOULD_CHEESE_COCOA_TOAST_DOUGH, RICE_BREAD_DOUGH
        );
        registerTabEntries(
                CreativeModeTabs.FOOD_AND_DRINKS,
                FOAMED_CREAM, CHEESE_CREAM, CHEESE_CUBE, FRESH_CHEESE_CUBE,
                MEAT_FLOSS, ICE_CUBES, SCONE, OLIVE, COOKED_TARO, MASHED_TARO,
                SLICED_TOAST, BAKE_SLICED_TOAST, HONEY_BUTTER_SPREAD_TOAST,
                SLICED_CHEESE_COCOA_TOAST, COUNTRY_BREAD_SLICE, HONEY_BUTTER_SPREAD_COUNTRY_BREAD,
                BROWN_SUGAR_LATTE, ICED_AMERICAN, ICED_LATTE, MATCHA_LATTE, ORANGE_AMERICAN, TARO_MILK
        );

        BakeriesMod.LOGGER.info("Registered Bakeries base items.");
    }

    private static Item[] allItems() {
        return new Item[]{
                BAKING_GUIDE, BREAD_KNIFE, FLOUR_SIEVE,

                FLOUR, WHOLE_WHEAT_FLOUR, COCOA_POWDER, MATCHA_POWDER, SALT,
                BROWN_SUGAR_CUBE, ICE_CUBES, RAW_COFFEE_BEAN, COFFEE_BEAN, GROUND_COFFEE,
                OLIVE, MEAT_FLOSS,

                BOTTLE_YEAST, BOTTLE_MILK, BOTTLE_CREAM, BOTTLE_BUTTER,
                BUTTER_CUBE, FOAMED_CREAM, CHEESE_CREAM, CHEESE_CUBE, FRESH_CHEESE_CUBE,

                WHOLE_EGG, RAW_PROTEIN, RAW_EGG_YOLK, SALT_YOLK,

                BUTTER_FLOUR_SAND, HONEY_BUTTER, BEARNAISE, OLIVE_OIL,

                TARO, COOKED_TARO, MASHED_TARO,

                MOKA_POT_FILL,
                BROWN_SUGAR_LATTE, ICED_AMERICAN, ICED_LATTE, MATCHA_LATTE, ORANGE_AMERICAN, TARO_MILK,

                COCOA_DOUGH_FERMENTATION, SALTED_DOUGH_FERMENTATION,
                SWEET_DOUGH_FERMENTATION, WHOLE_WHEAT_DOUGH_FERMENTATION,
                PASTRY, EGG_TART_SHELL, RAW_EGG_TART,
                DOUGH_CRAFTING,

                OVEN, TOASTER, BLENDER, FERMENTATION_BOX, FERMENTATION_TANK,
                DOUGH_CRAFTING_TABLE, CUPBOARD, WOOD_COUNTER, COFFEE_TABLE,
                CHEESE_TANK, MILK_TANK, YEAST_TANK, MOKA_POT, MIX_BLOCK, MOULD, DRINK_CUP,

                BREAD_RACK, GLASS_BREAD_RACK, BREAD_BASKET, GLASS_CABINET_DOOR, MENU,
                CASH_REGISTER_COMPUTER, FLOUR_BAG, WHOLE_WHEAT_FLOUR_BAG, WOOD_TRAY,
                SOFA_LIGHT_GRAY, SOFA_RED, SOFA_WHITE,

                TOAST, CHEESE_COCOA_TOAST,
                BAGEL, BAGUETTE, CROISSANT, ROUND_BREAD, RICE_BREAD, WHOLE_WHEAT_BAGEL,
                PINEAPPLE_BUN, FOCACCIA, CIABATTA, EGG_TART, SALT_CROISSANT, COUNTRY_BREAD,
                BROWN_SUGAR_ROLL, BAGEL_FILLED_SAUCE, BAGUETTE_WITH_FILLING, BERRY_BAGEL,
                CHEESE_CREAM_BREAD, DIRTY_CHOCO_CROISSANT, FLAT_CROISSANT,
                MEAT_FLOSS_BREAD_ROLL, PINEAPPLE_OIL, TARO_SALT_YOLK_BREAD,
                TOMATO_CHEESE_CROISSANT_SANDWICH, CREAM_BINGLE_COFFEE, MATCHA_PARFAIT,
                SCONE, SLICED_TOAST, BAKE_SLICED_TOAST, HONEY_BUTTER_SPREAD_TOAST,
                SLICED_CHEESE_COCOA_TOAST, COUNTRY_BREAD_SLICE, HONEY_BUTTER_SPREAD_COUNTRY_BREAD,

                COFFEE_PLANT, TOMATO,

                RAW_SALT_BLOCK, SALT_ORE, DEEPSLATE_SALT_ORE, COARSE_SALT,

                NETHERITE_BREAD_KNIFE, ETERNAL_BAGUETTE, ROUND_MOULD, MUSIC_DISC_BAKING_IN_PROGRESS,
                MOULD_TOAST, MOULD_CHEESE_COCOA_TOAST, BREAD_HOLDERS, BLACK_WHITE_CONCRETE, BOXED_EGG, SWEET_BERRIES_JAM,
                BERRY_BREAD, GARLIC_FLAVORED_BAGUETTE, SALMON_SANDWICH, DONGPO_PORK_HAMBURG, LONG_EGG_TART,
                CREAM_PUMPKIN_PIE, CREAM_MUSHROOM_SOUP_WITH_BAGUETTE, TRAY_SCONE, BUTTER_LATTE, MILK_TEA,

                MOULD_TWO, SILICONE_PAPER, PAPER_CUP, CAKE_PASTE_BUCKET, EGG_YOLK_PASTE_BUCKET, FOAMED_PROTEIN_BUCKET, COCOA_CREAM,
                MOULD_CAKE_BASE, MOULD_CARROT_CAKE, MOULD_RED_VELVET_CAKE, MOULD_MATCHA_CAKE, MOULD_BASQUE_CAKE, MOULD_POUND_CAKE,
                CAKE_BASE, RED_VELVET_CAKE_BASE, CUT_CAKE_BASE, SOAK_COFFEE_CUT_CAKE_BASE,
                CREAM_CAKE, CREAM_CAKE_CUBE, TIRAMISU, CARROT_CAKE, RED_VELVET_CAKE, MATCHA_CAKE, BASQUE_CAKE, NIANGAO_CAKE, TARO_CAKE,
                POUND_CAKE, SLICED_POUND_CAKE, CUP_CAKE
        };
    }

    private static Item register(String id) {
        return register(id, new Item.Properties());
    }

    private static Item register(String id, Item.Properties properties) {
        return register(id, new Item(properties));
    }

    private static Item register(String id, Item item) {
        ResourceLocation key = new ResourceLocation(BakeriesMod.MODID, id);
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static Item registerBlock(String id, Block block) {
        return registerBlock(id, block, new Item.Properties());
    }

    private static Item registerBlock(String id, Block block, Item.Properties properties) {
        ResourceLocation key = new ResourceLocation(BakeriesMod.MODID, id);
        return Registry.register(BuiltInRegistries.ITEM, key, new BlockItem(block, properties));
    }

    private static Item registerBlockWithEffects(String id, Block block, Item.Properties properties, FoodEffectTooltip.Entry... effects) {
        ResourceLocation key = new ResourceLocation(BakeriesMod.MODID, id);
        return Registry.register(BuiltInRegistries.ITEM, key, new FoodEffectBlockItem(block, properties, effects));
    }

    private static Item foodEffectItem(Item.Properties properties, FoodEffectTooltip.Entry... effects) {
        return new FoodEffectItem(properties, effects);
    }

    private static FoodEffectTooltip.Entry effect(MobEffect effect, int seconds, int amplifier) {
        return new FoodEffectTooltip.Entry(effect, seconds * 20, amplifier);
    }

    private static void registerTabEntries(ResourceKey<CreativeModeTab> tab, Item... items) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
            for (Item item : items) {
                entries.accept(item);
            }
        });
    }

    private static Item.Properties food(int nutrition, float saturation, boolean alwaysEdible, FoodEffectTooltip.Entry... effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation);
        if (alwaysEdible) {
            builder.alwaysEat();
        }
        for (FoodEffectTooltip.Entry effect : effects) {
            builder.effect(effect.createInstance(), 1.0F);
        }
        return new Item.Properties().food(builder.build());
    }
}
