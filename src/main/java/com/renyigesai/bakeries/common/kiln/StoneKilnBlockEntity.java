package com.renyigesai.bakeries.common.kiln;

import com.renyigesai.bakeries.common.pizza.CustomPizzaItem;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import com.renyigesai.bakeries.init.BakeriesItems;
import com.renyigesai.bakeries.init.BakeriesRecipeTypes;
import com.renyigesai.bakeries.recipe.StoneKilnRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Stone kiln: bakes one item while lit. Stone kiln recipes cook in stages and need a turn with the kiln shovel
 * between stages; anything with a smoker recipe also cooks here. The item visibly grows while it bakes.
 */
public class StoneKilnBlockEntity extends BlockEntity {
    private ItemStack item = ItemStack.EMPTY;
    private int cookingTime;
    private int maxCookingTime;
    private int stageTime;
    private int maxStageTime;
    private int turnOver;
    private int maxTurnOver;
    private boolean needsTurn;
    private int nextStage;
    private float size;
    // client-side turn animation
    private float progress = 1.0F;
    private float progressOld = 1.0F;

    public StoneKilnBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.STONE_KILN, pos, state);
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public boolean addItem(ItemStack stack) {
        if (!isEmpty() || stack.isEmpty()) {
            return false;
        }
        item = stack.copyWithCount(1);
        size = 0.0F;
        reset();
        sync();
        return true;
    }

    public ItemStack takeItem() {
        ItemStack out = item;
        item = ItemStack.EMPTY;
        reset();
        sync();
        return out;
    }

    public void reset() {
        cookingTime = 0;
        maxCookingTime = 0;
        stageTime = 0;
        maxStageTime = 0;
        turnOver = 0;
        maxTurnOver = 0;
        needsTurn = false;
        nextStage = 0;
    }

    public boolean needsTurn() {
        return needsTurn;
    }

    public void turnOver() {
        turnOver++;
        needsTurn = false;
        sync();
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), 1, 0);
        }
    }

    // ---- getters for renderer / overlay ----
    public float getSize() { return size; }
    public int getCookingTime() { return cookingTime; }
    public int getMaxCookingTime() { return maxCookingTime; }
    public int getStageTime() { return stageTime; }
    public int getMaxStageTime() { return maxStageTime; }
    public int getTurnOver() { return turnOver; }
    public int getMaxTurnOver() { return maxTurnOver; }

    public float getTurnProgress(float partialTick) {
        return Mth.lerp(partialTick, progressOld, progress);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            progress = 0.0F;
            progressOld = 0.0F;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, StoneKilnBlockEntity kiln) {
        kiln.progressOld = kiln.progress;
        if (kiln.progress < 1.0F) {
            kiln.progress = Math.min(1.0F, kiln.progress + 0.1F);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StoneKilnBlockEntity kiln) {
        if (state.getValue(StoneKilnBlock.LIT) && !kiln.isEmpty()) {
            kiln.cook();
            kiln.sync();
        }
    }

    private void cook() {
        SimpleContainer container = new SimpleContainer(item);
        Optional<StoneKilnRecipe> kilnRecipe = level.getRecipeManager().getRecipeFor(BakeriesRecipeTypes.STONE_KILN, container, level);
        if (kilnRecipe.isPresent()) {
            cookKilnRecipe(kilnRecipe.get());
            return;
        }
        Optional<SmokingRecipe> smoking = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, container, level);
        if (smoking.isPresent()) {
            int time = smoking.get().getCookingTime();
            maxCookingTime = time;
            cookingTime++;
            if (cookingTime >= time) {
                item = cookingTime >= time + 200 ? new ItemStack(Items.COAL) : smoking.get().getResultItem(level.registryAccess()).copy();
            }
            return;
        }
        reset();
    }

    private void cookKilnRecipe(StoneKilnRecipe recipe) {
        int[] stages = recipe.getStages();
        if (maxCookingTime == 0) {
            maxCookingTime = recipe.getTotalTime();
        }
        if (maxTurnOver == 0) {
            maxTurnOver = stages.length - 1;
        }
        boolean done = false;
        if (stages.length == 1) {
            if (cookingTime < stages[0]) {
                cookingTime++;
                size += (float) (0.075 / stages[0]);
            } else {
                done = true;
            }
        } else if (cookingTime >= maxCookingTime) {
            done = true;
        } else {
            maxStageTime = stages[nextStage];
            if (turnOver == nextStage) {
                if (stageTime < maxStageTime) {
                    stageTime++;
                    size += (float) (0.075 / maxStageTime / stages.length);
                } else {
                    cookingTime += stageTime;
                    stageTime = 0;
                    maxStageTime = 0;
                    if (nextStage + 1 < stages.length) {
                        nextStage++;
                    }
                }
            } else {
                needsTurn = true;
            }
        }
        if (done) {
            ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
            // a custom pizza keeps its toppings
            if (item.is(BakeriesItems.RAW_CUSTOM_PIZZA) && result.getItem() instanceof CustomPizzaItem) {
                CustomPizzaItem.setToppings(result, CustomPizzaItem.getToppings(item));
            }
            item = result;
            reset();
        }
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!item.isEmpty()) {
            tag.put("Item", item.save(new CompoundTag()));
        }
        tag.putIntArray("CookingTime", new int[]{cookingTime, maxCookingTime});
        tag.putIntArray("StageCookingTime", new int[]{stageTime, maxStageTime});
        tag.putIntArray("TurnOver", new int[]{turnOver, maxTurnOver});
        tag.putBoolean("IsTurnOver", needsTurn);
        tag.putInt("NextStage", nextStage);
        tag.putFloat("Size", size);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        item = tag.contains("Item") ? ItemStack.of(tag.getCompound("Item")) : ItemStack.EMPTY;
        int[] c = tag.getIntArray("CookingTime");
        int[] s = tag.getIntArray("StageCookingTime");
        int[] t = tag.getIntArray("TurnOver");
        cookingTime = c.length > 0 ? c[0] : 0;
        maxCookingTime = c.length > 1 ? c[1] : 0;
        stageTime = s.length > 0 ? s[0] : 0;
        maxStageTime = s.length > 1 ? s[1] : 0;
        turnOver = t.length > 0 ? t[0] : 0;
        maxTurnOver = t.length > 1 ? t[1] : 0;
        needsTurn = tag.getBoolean("IsTurnOver");
        nextStage = tag.getInt("NextStage");
        size = tag.getFloat("Size");
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
