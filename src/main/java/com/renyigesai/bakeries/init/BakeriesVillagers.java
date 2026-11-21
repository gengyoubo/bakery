package com.renyigesai.bakeries.init;

import com.google.common.collect.ImmutableSet;
import com.renyigesai.bakeries.BakeriesMod;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

/** Baker villager ("pistrinamaster"), working at the dough crafting table, plus extra farmer/wandering trades. */
public final class BakeriesVillagers {
    public static final ResourceLocation DOUGH_CRAFTING_TABLE_POI_ID = new ResourceLocation(BakeriesMod.MODID, "dough_crafting_table_poi");
    public static final ResourceKey<PoiType> DOUGH_CRAFTING_TABLE_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, DOUGH_CRAFTING_TABLE_POI_ID);
    public static final PoiType DOUGH_CRAFTING_TABLE_POI = PointOfInterestHelper.register(DOUGH_CRAFTING_TABLE_POI_ID, 1, 1, BakeriesBlocks.DOUGH_CRAFTING_TABLE);
    public static final VillagerProfession PISTRINA_MASTER = Registry.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            new ResourceLocation(BakeriesMod.MODID, "pistrinamaster"),
            new VillagerProfession(
                    "pistrinamaster",
                    holder -> holder.is(DOUGH_CRAFTING_TABLE_POI_KEY),
                    holder -> holder.is(DOUGH_CRAFTING_TABLE_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(BakeriesBlocks.DOUGH_CRAFTING_TABLE),
                    SoundEvents.VILLAGER_WORK_ARMORER
            )
    );

    private BakeriesVillagers() {
    }

    public static void init() {
        TradeOfferHelper.registerVillagerOffers(PISTRINA_MASTER, 1, f -> {
            f.add(buy(8, BakeriesItems.FLOUR, 2, 16, 1, 0.05F));
            f.add(buy(8, BakeriesItems.WHOLE_WHEAT_FLOUR, 2, 16, 1, 0.05F));
            f.add(buy(4, BakeriesItems.SALT, 2, 16, 2, 0.05F));
        });
        TradeOfferHelper.registerVillagerOffers(PISTRINA_MASTER, 2, f ->
                f.add(buy(6, BakeriesItems.BOTTLE_YEAST, 1, 8, 5, 0.05F)));
        TradeOfferHelper.registerVillagerOffers(PISTRINA_MASTER, 3, f -> {
            f.add(sell(BakeriesItems.BUTTER_CUBE, 4, 2, 4, 10, 0.05F));
            f.add(sell(BakeriesItems.BROWN_SUGAR_CUBE, 4, 1, 8, 5, 0.05F));
            f.add(sell(BakeriesItems.BOTTLE_CREAM, 1, 1, 8, 5, 0.05F));
            f.add(sell(BakeriesItems.SWEET_DOUGH, 1, 3, 2, 10, 0.05F));
        });
        TradeOfferHelper.registerVillagerOffers(PISTRINA_MASTER, 4, f -> {
            f.add(buy(6, BakeriesItems.ROUND_BREAD, 4, 4, 15, 0.0F));
            f.add(buy(3, BakeriesItems.BAGUETTE, 1, 2, 10, 0.0F));
            f.add(buy(5, BakeriesItems.COUNTRY_BREAD, 1, 2, 10, 0.0F));
        });
        TradeOfferHelper.registerVillagerOffers(PISTRINA_MASTER, 5, f -> {
            f.add(buy(16, BakeriesItems.CROISSANT, 3, 2, 30, 0.0F));
            f.add(buy(10, BakeriesItems.BROWN_SUGAR_ROLL, 2, 2, 30, 0.0F));
            f.add(buy(12, BakeriesItems.TOAST, 1, 2, 30, 0.0F));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, f -> {
            f.add(buy(2, BakeriesItems.TOMATO, 4, 16, 1, 0.05F));
            f.add(buy(2, BakeriesItems.TARO, 4, 16, 1, 0.05F));
            f.add(buy(2, BakeriesItems.OLIVE, 2, 16, 1, 0.05F));
        });
        TradeOfferHelper.registerWanderingTraderOffers(1, f -> {
            f.add(buy(2, BakeriesItems.OLIVE, 4, 5, 1, 0.05F));
            f.add(buy(2, BakeriesItems.GROUND_COFFEE, 2, 5, 1, 0.05F));
        });
        BakeriesMod.LOGGER.info("Registered Bakeries villagers.");
    }

    /** Player pays emeralds, receives the item. */
    private static VillagerTrades.ItemListing buy(int emeralds, Item item, int count, int maxUses, int xp, float priceMultiplier) {
        return (trader, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, emeralds), new ItemStack(item, count), maxUses, xp, priceMultiplier);
    }

    /** Player gives the item, receives emeralds. */
    private static VillagerTrades.ItemListing sell(Item item, int count, int emeralds, int maxUses, int xp, float priceMultiplier) {
        return (trader, random) -> new MerchantOffer(new ItemStack(item, count), new ItemStack(Items.EMERALD, emeralds), maxUses, xp, priceMultiplier);
    }
}
