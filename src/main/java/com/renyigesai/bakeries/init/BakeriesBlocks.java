package com.renyigesai.bakeries.init;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.common.blocks.menu.MenuBlock;
import com.renyigesai.bakeries.common.blocks.sofa.SofaBlock;
import com.renyigesai.bakeries.init.blocks.FoodBlocks;
import com.renyigesai.bakeries.init.blocks.MachineBlocks;
import com.renyigesai.bakeries.init.blocks.StateBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings("unused")
public final class BakeriesBlocks {
    public static final Block OVEN, TOASTER, BLENDER, FERMENTATION_BOX, FERMENTATION_TANK, DOUGH_CRAFTING_TABLE, CUPBOARD, WOOD_COUNTER, COFFEE_TABLE, CHEESE_TANK, MILK_TANK, YEAST_TANK, MOKA_POT, MOKA_POT_FILL, BREAD_RACK, GLASS_BREAD_RACK, BREAD_BASKET, GLASS_CABINET_DOOR, MENU, MOULD, DRINK_CUP, TOAST, CHEESE_COCOA_TOAST, BAGEL, BAGUETTE, CROISSANT, ROUND_BREAD, RICE_BREAD, WHOLE_WHEAT_BAGEL, PINEAPPLE_BUN, FOCACCIA, CIABATTA, EGG_TART, SALT_CROISSANT, COUNTRY_BREAD, CREAM_BINGLE_COFFEE, MATCHA_PARFAIT, COFFEE_PLANT, TARO, TOMATO, MIX_BLOCK,BAGEL_FILLED_SAUCE,BAGUETTE_WITH_FILLING,BERRY_BAGEL;
    public static final Block CASH_REGISTER_COMPUTER;
    public static final Block CHEESE_CREAM_BREAD;
    public static final Block RAW_SALT_BLOCK;
    public static final Block SALT_ORE;
    public static final Block DEEPSLATE_SALT_ORE;
    public static final Block DIRTY_CHOCO_CROISSANT;
    public static final Block FLOUR_BAG;
    public static final Block MEAT_FLOSS_BREAD_ROLL;
    public static final Block SOFA_LIGHT_GRAY;
    public static final Block SOFA_RED;
    public static final Block SOFA_WHITE;
    public static final Block TARO_SALT_YOLK_BREAD;
    public static final Block TOMATO_CHEESE_CROISSANT_SANDWICH;
    public static final Block WHOLE_WHEAT_FLOUR_BAG;
    public static final Block WOOD_TRAY;
    public static final Block PINEAPPLE_OIL;
    public static final Block BROWN_SUGAR_ROLL;
    // Ported from Forge 1.3.6 (scattered foods and kitchen blocks)
    public static final Block BERRY_BREAD, GARLIC_FLAVORED_BAGUETTE, SALMON_SANDWICH, DONGPO_PORK_HAMBURG,
            SWEET_BERRIES_JAM, LONG_EGG_TART, BOXED_EGG, CREAM_PUMPKIN_PIE, CREAM_MUSHROOM_SOUP_WITH_BAGUETTE,
            TRAY_SCONE, BREAD_HOLDERS, BLACK_WHITE_CONCRETE, MOULD_TOAST, MOULD_CHEESE_COCOA_TOAST;
    // Ported from Forge 1.3.6: cake system (part 1)
    public static final Block CAKE_BASE, RED_VELVET_CAKE_BASE, CREAM_CAKE, MULTI_LAYER_CREAM_CAKE, TIRAMISU, CARROT_CAKE,
            RED_VELVET_CAKE, MATCHA_CAKE, BASQUE_CAKE, NIANGAO_CAKE, TARO_CAKE, POUND_CAKE, CUP_CAKE, PAPER_CUP, SILICONE_PAPER,
            MOULD_TWO, MOULD_CAKE_BASE, MOULD_CARROT_CAKE, MOULD_RED_VELVET_CAKE, MOULD_MATCHA_CAKE, MOULD_BASQUE_CAKE, MOULD_POUND_CAKE;
    static {
        CAKE_BASE = register("cake_base", cake(5, 0.4F));
        RED_VELVET_CAKE_BASE = register("red_velvet_cake_base", cake(5, 0.4F));
        MULTI_LAYER_CREAM_CAKE = register("multi_layer_cream_cake", new FoodBlocks.MultiLayerCreamCakeBlock(cakeProperties(), () -> BakeriesItems.CREAM_CAKE_CUBE));
        CREAM_CAKE = register("cream_cake", new FoodBlocks.CreamCakeBlock(cakeProperties(), () -> MULTI_LAYER_CREAM_CAKE, 5, 0.4F,
                () -> new MobEffectInstance(BakeriesMobEffects.SOFT, 1200), () -> new MobEffectInstance(MobEffects.REGENERATION, 1200)));
        TIRAMISU = register("tiramisu", cake(4, 0.5F, () -> new MobEffectInstance(BakeriesMobEffects.CHEESE_POWER, 1200),
                () -> new MobEffectInstance(BakeriesMobEffects.COCOA_MANIA, 1200), () -> new MobEffectInstance(MobEffects.NIGHT_VISION, 1200)));
        CARROT_CAKE = register("carrot_cake", cake(10, 0.25F, () -> new MobEffectInstance(MobEffects.NIGHT_VISION, 1200)));
        RED_VELVET_CAKE = register("red_velvet_cake", cake(5, 0.25F));
        MATCHA_CAKE = register("matcha_cake", cake(10, 0.25F, () -> new MobEffectInstance(BakeriesMobEffects.TEA_ASTRINGENT, 1200)));
        BASQUE_CAKE = register("basque_cake", cake(6, 0.5F, () -> new MobEffectInstance(BakeriesMobEffects.CHEESE_POWER, 1200)));
        NIANGAO_CAKE = register("niangao_cake", cake(6, 0.5F, () -> new MobEffectInstance(BakeriesMobEffects.SOFT, 1200),
                () -> new MobEffectInstance(BakeriesMobEffects.ENJOY, 1200)));
        TARO_CAKE = register("taro_cake", pastryPile(4));
        CUP_CAKE = register("cup_cake", pastryPile(4));
        POUND_CAKE = register("pound_cake", new StateBlocks.ToastBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion()));
        PAPER_CUP = register("paper_cup", new FoodBlocks.PaperCupBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion(),
                () -> BakeriesItems.CAKE_PASTE_BUCKET, () -> BakeriesItems.PAPER_CUP_CAKE_PASTE));
        SILICONE_PAPER = register("silicone_paper", new FoodBlocks.ShapedFacingBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.1F).noOcclusion(), Block.box(1, 0, 1, 15, 1, 15)));
        MOULD_TWO = register("mould_two", new StateBlocks.MouldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F, 0.5F).noOcclusion(), Block.box(2, 0, 2, 14, 7, 14)));
        MOULD_CAKE_BASE = register("mould_cake_base", mouldCake(() -> BakeriesItems.CAKE_BASE));
        MOULD_CARROT_CAKE = register("mould_carrot_cake", mouldCake(() -> BakeriesItems.CARROT_CAKE));
        MOULD_RED_VELVET_CAKE = register("mould_red_velvet_cake", mouldCake(() -> BakeriesItems.RED_VELVET_CAKE_BASE));
        MOULD_MATCHA_CAKE = register("mould_matcha_cake", mouldCake(() -> BakeriesItems.MATCHA_CAKE));
        MOULD_BASQUE_CAKE = register("mould_basque_cake", mouldCake(() -> BakeriesItems.BASQUE_CAKE));
        MOULD_POUND_CAKE = register("mould_pound_cake", new FoodBlocks.MouldToastBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.5F).noOcclusion(), () -> BakeriesItems.POUND_CAKE));
        BERRY_BREAD = register("berry_bread", pastryPile(4));
        GARLIC_FLAVORED_BAGUETTE = register("garlic_flavored_baguette", pastryPile(4));
        SALMON_SANDWICH = register("salmon_sandwich", pastryPile(4));
        DONGPO_PORK_HAMBURG = register("dongpo_pork_hamburg", pastryPile(4));
        LONG_EGG_TART = register("long_egg_tart", pastryPile(4));
        SWEET_BERRIES_JAM = register("sweet_berries_jam", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(0.3F).noOcclusion(), 4));
        BOXED_EGG = register("boxed_egg", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(0.5F).noOcclusion(), 2));
        CREAM_PUMPKIN_PIE = register("cream_pumpkin_pie", new FoodBlocks.CreamPumpkinPieBlock(BlockBehaviour.Properties.copy(Blocks.CAKE)));
        CREAM_MUSHROOM_SOUP_WITH_BAGUETTE = register("cream_mushroom_soup_with_baguette", new FoodBlocks.CreamMushroomSoupBlock(BlockBehaviour.Properties.copy(Blocks.CAKE)));
        TRAY_SCONE = register("tray_scone", new FoodBlocks.ShapedFacingBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), Block.box(2, 0, 2, 14, 2, 14)));
        BREAD_HOLDERS = register("bread_holders", new FoodBlocks.ShapedFacingBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion(), Block.box(0, 6, 0, 16, 14, 16)));
        BLACK_WHITE_CONCRETE = register("black_white_concrete", new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_CONCRETE)));
        MOULD_TOAST = register("mould_toast", new FoodBlocks.MouldToastBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.5F).noOcclusion(), () -> BakeriesItems.TOAST));
        MOULD_CHEESE_COCOA_TOAST = register("mould_cheese_cocoa_toast", new FoodBlocks.MouldToastBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.5F).noOcclusion(), () -> BakeriesItems.CHEESE_COCOA_TOAST));
        BROWN_SUGAR_ROLL = register("brown_sugar_roll", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        PINEAPPLE_OIL = register("pineapple_oil", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        SOFA_LIGHT_GRAY = register("sofa_light_gray", new SofaBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        SOFA_RED = register("sofa_red", new SofaBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        SOFA_WHITE = register("sofa_white", new SofaBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        TARO_SALT_YOLK_BREAD = register("taro_salt_yolk_bread", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        TOMATO_CHEESE_CROISSANT_SANDWICH = register("tomato_cheese_croissant_sandwich", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        WHOLE_WHEAT_FLOUR_BAG = register("whole_wheat_flour_bag", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        WOOD_TRAY = register("wood_tray", new StateBlocks.WoodTrayBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        MEAT_FLOSS_BREAD_ROLL = register("meat_floss_bread_roll", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        FLOUR_BAG = register("flour_bag", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        DIRTY_CHOCO_CROISSANT = register("dirty_choco_croissant", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        CHEESE_CREAM_BREAD = register("cheese_cream_bread", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion(), 4));
        CASH_REGISTER_COMPUTER = register("cash_register_computer", new StateBlocks.LitFacingBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        RAW_SALT_BLOCK = register("raw_salt_block", new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
        SALT_ORE = register("salt_ore", new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
        DEEPSLATE_SALT_ORE = register("deepslate_salt_ore", new DropExperienceBlock(BlockBehaviour.Properties.copy(SALT_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
        OVEN = register("oven", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.of().strength(3.5F).noOcclusion()));
        TOASTER = register("toaster", new StateBlocks.ToasterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
        BLENDER = register("blender", new MachineBlocks.BlenderBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion().isRedstoneConductor((state, level, pos) -> false)));
        FERMENTATION_BOX = register("fermentation_box", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F, 3.5F).requiresCorrectToolForDrops().sound(SoundType.NETHERITE_BLOCK).noOcclusion().isRedstoneConductor((state, level, pos) -> false)));
        FERMENTATION_TANK = register("fermentation_tank", new StateBlocks.FermentationTankBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).randomTicks()));
        DOUGH_CRAFTING_TABLE = register("dough_crafting_table", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));
        CUPBOARD = register("cupboard", new MachineBlocks.FacingMachineBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).requiresCorrectToolForDrops().mapColor(MapColor.COLOR_GRAY).sound(SoundType.CHISELED_BOOKSHELF)));
        WOOD_COUNTER = register("wood_counter", new StateBlocks.WoodCounterBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        COFFEE_TABLE = register("coffee_table", new StateBlocks.RackBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        CHEESE_TANK = register("cheese_tank", new StateBlocks.CheeseTankBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
        MILK_TANK = register("milk_tank", new StateBlocks.MilkTankBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).randomTicks()));
        YEAST_TANK = register("yeast_tank", new StateBlocks.YeastTankBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
        MOKA_POT = register("moka_pot", new StateBlocks.MokaPotBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
        MOKA_POT_FILL = register("moka_pot_fill", new StateBlocks.FilledMokaPotBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
        BREAD_RACK = register("bread_rack", new StateBlocks.RackBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        GLASS_BREAD_RACK = register("glass_bread_rack", new StateBlocks.RackBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion()));
        BREAD_BASKET = register("bread_basket", new StateBlocks.BreadBasketBlock(BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).strength(0.0F, 0.0F).noOcclusion()));
        GLASS_CABINET_DOOR = register("glass_cabinet_door", new StateBlocks.GlassCabinetDoorBlock(BlockBehaviour.Properties.copy(Blocks.GLASS)));
        MENU = register("menu", new MenuBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        MOULD = register("mould", new StateBlocks.MouldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F, 0.5F).sound(SoundType.METAL).noOcclusion()));
        DRINK_CUP = register("drink_cup", new StateBlocks.DrinkCupBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.1F, 0.1F).noOcclusion()));
        TOAST = register("toast", new StateBlocks.ToastBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F)));
        CHEESE_COCOA_TOAST = register("cheese_cocoa_toast", new StateBlocks.ToastBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion()));
        BAGEL = register("bagel", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        BAGUETTE = register("baguette", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        CROISSANT = register("croissant", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        ROUND_BREAD = register("round_bread", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        RICE_BREAD = register("rice_bread", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        WHOLE_WHEAT_BAGEL = register("whole_wheat_bagel", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        PINEAPPLE_BUN = register("pineapple_bun", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        FOCACCIA = register("focaccia", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        CIABATTA = register("ciabatta", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        EGG_TART = register("egg_tart", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        SALT_CROISSANT = register("salt_croissant", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), 4));
        COUNTRY_BREAD = register("country_bread", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F), 2));
        CREAM_BINGLE_COFFEE = register("cream_bingle_coffee", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.1F, 0.1F), 2));
        MATCHA_PARFAIT = register("matcha_parfait", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(0.1F, 0.1F), 2));
        COFFEE_PLANT = register("coffee_plant", new StateBlocks.CropLikeBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), 2));
        TARO = register("taro", new StateBlocks.CropLikeBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), 3));
        TOMATO = register("tomato", new StateBlocks.CropLikeBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT), 7));
        MIX_BLOCK = register("mix_block", new MachineBlocks.MachineBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
        BAGEL_FILLED_SAUCE = register("bagel_filled_sauce", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).strength(0.1F, 0.1F), 2));
        BAGUETTE_WITH_FILLING = register("baguette_with_filling", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).strength(0.1F, 0.1F), 2));
        BERRY_BAGEL = register("berry_bagel", new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE).strength(0.1F, 0.1F), 2));

    }
    private BakeriesBlocks() {
    }

    public static void init() {
        BakeriesMod.LOGGER.info("Registered Bakeries base blocks.");
    }

    /** Forge BCakeBlock.CAKE properties (solid-ish, pastry sound, destroyed by pistons, faint light). */
    private static BlockBehaviour.Properties cakeProperties() {
        return BlockBehaviour.Properties.of().forceSolidOn().strength(0.5F).sound(BakeriesSounds.PASTRY)
                .pushReaction(PushReaction.DESTROY).lightLevel(state -> 1).noOcclusion();
    }

    @SafeVarargs
    private static Block cake(int nutrition, float saturation, java.util.function.Supplier<MobEffectInstance>... effects) {
        return new FoodBlocks.BakeryCakeBlock(cakeProperties(), nutrition, saturation, effects);
    }

    private static Block mouldCake(java.util.function.Supplier<net.minecraft.world.item.Item> content) {
        return new FoodBlocks.MouldCakeBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(0.5F, 0.5F).noOcclusion(), content, () -> MOULD_TWO);
    }

    private static Block pastryPile(int maxPile) {
        return new StateBlocks.FacingPileBlock(BlockBehaviour.Properties.of().sound(BakeriesSounds.PASTRY).strength(0.5F, 0.5F).noOcclusion(), maxPile);
    }

    private static Block register(String id, BlockBehaviour.Properties properties) {
        ResourceLocation key = new ResourceLocation(BakeriesMod.MODID, id);
        return Registry.register(BuiltInRegistries.BLOCK, key, new Block(properties));
    }

    private static Block register(String id, Block block) {
        ResourceLocation key = new ResourceLocation(BakeriesMod.MODID, id);
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
