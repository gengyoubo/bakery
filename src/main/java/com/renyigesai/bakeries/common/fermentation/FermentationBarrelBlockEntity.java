package com.renyigesai.bakeries.common.fermentation;

import com.renyigesai.bakeries.block.entity.ImplementedInventory;
import com.renyigesai.bakeries.init.BakeriesBlockEntities;
import com.renyigesai.bakeries.init.BakeriesRecipeTypes;
import com.renyigesai.bakeries.menu.FermentationBarrelMenu;
import com.renyigesai.bakeries.recipe.FermentationRecipe;
import com.renyigesai.bakeries.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fermentation barrel inventory and processing, ported from the Forge version.
 *
 * <p>Slots 0-5 are inputs, slot 6 stores the recipe container, slot 7 the crafted result buffer,
 * slot 8 the output that the player can take.</p>
 */
public class FermentationBarrelBlockEntity extends BlockEntity implements ImplementedInventory, WorldlyContainer, MenuProvider {
    public static final int SIZE = 9;
    public static final int MAX_PROGRESS = 3600;
    private static final int STORAGE_CONTAINER_SLOT = 6;
    private static final int STORAGE_SLOT = 7;
    private static final int OUTPUT_SLOT = 8;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private int cookingTotalTime;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? FermentationBarrelBlockEntity.this.cookingTotalTime : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                FermentationBarrelBlockEntity.this.cookingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BakeriesBlockEntities.FERMENTATION_BARREL, pos, state);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            this.readItems(tag.getList("Inventory", 10));
        }
        this.cookingTotalTime = tag.getInt("CookingTotalTime");
    }

    private void readItems(net.minecraft.nbt.ListTag list) {
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compound = list.getCompound(i);
            int slot = compound.getByte("Slot") & 255;
            if (slot < this.items.size()) {
                this.items.set(slot, ItemStack.of(compound));
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", net.minecraft.world.ContainerHelper.saveAllItems(new CompoundTag(), this.items, false).getList("Items", 10));
        tag.putInt("CookingTotalTime", this.cookingTotalTime);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.fermentation_barrel");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    public int getCookingTotalTime() {
        return this.cookingTotalTime;
    }

    public ContainerData getMenuData() {
        return this.data;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new FermentationBarrelMenu(syncId, playerInventory, this, this.data);
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(7);
        for (int i = 0; i < 6; i++) {
            container.setItem(i, this.items.get(i));
        }
        container.setItem(6, this.items.get(OUTPUT_SLOT));
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, container);
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < 6; i++) {
            if (!this.items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private Optional<FermentationRecipe> getCurrentRecipe() {
        if (this.level == null) {
            return Optional.empty();
        }
        SimpleContainer container = new SimpleContainer(6);
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }
        for (int i = 0; i < inputs.size(); i++) {
            container.setItem(i, inputs.get(i));
        }
        return this.level.getRecipeManager().getRecipeFor(BakeriesRecipeTypes.FERMENTATION, container, this.level);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationBarrelBlockEntity barrel) {
        if (barrel.hasInput()) {
            barrel.craftItem();
        }
        barrel.takeTick();
        barrel.setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    private void craftItem() {
        if (this.level == null) {
            return;
        }
        Optional<FermentationRecipe> recipeOptional = this.getCurrentRecipe();
        if (recipeOptional.isEmpty()) {
            this.cookingTotalTime = 0;
            if (this.items.get(STORAGE_SLOT).isEmpty()) {
                this.items.set(STORAGE_CONTAINER_SLOT, ItemStack.EMPTY);
            }
            return;
        }

        FermentationRecipe recipe = recipeOptional.get();
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess()).copy();
        ItemStack outputStack = this.items.get(STORAGE_SLOT);
        ItemStack containerStack = recipe.getContainer();
        if (!containerStack.isEmpty()) {
            this.items.set(STORAGE_CONTAINER_SLOT, containerStack);
        } else {
            for (int i = 0; i < 6 && this.items.get(i).isEmpty() && this.items.get(STORAGE_SLOT).isEmpty(); i++) {
                this.items.set(STORAGE_CONTAINER_SLOT, ItemStack.EMPTY);
            }
        }

        if (!this.canCraft(resultItem, outputStack)) {
            this.cookingTotalTime = 0;
            return;
        }

        List<Ingredient> ingredientsToConsume = new ArrayList<>(recipe.getIngredients());
        List<Integer> slotsToConsume = new ArrayList<>();
        outer:
        for (Ingredient ingredient : ingredientsToConsume) {
            for (int i = 0; i < 6; i++) {
                ItemStack stack = this.items.get(i);
                if (!stack.isEmpty() && ingredient.test(stack) && !slotsToConsume.contains(i)) {
                    slotsToConsume.add(i);
                    continue outer;
                }
            }
            this.cookingTotalTime = 0;
            return;
        }

        if (this.cookingTotalTime < MAX_PROGRESS) {
            this.cookingTotalTime++;
            return;
        }

        for (int slot : slotsToConsume) {
            ItemStack stack = this.items.get(slot);
            if (!stack.is(Items.LAVA_BUCKET)) {
                if (stack.getItem().hasCraftingRemainingItem()) {
                    this.ejectIngredientRemainder(new ItemStack(stack.getItem().getCraftingRemainingItem()));
                }
                this.removeItem(slot, 1);
            }
        }
        if (outputStack.isEmpty()) {
            this.items.set(STORAGE_SLOT, resultItem);
        } else {
            outputStack.grow(resultItem.getCount());
        }
        this.cookingTotalTime = 0;
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    private void takeTick() {
        ItemStack storage = this.items.get(STORAGE_SLOT);
        if (storage.isEmpty()) {
            return;
        }
        ItemStack output = this.items.get(OUTPUT_SLOT);
        if (output.isEmpty()) {
            ItemStack copy = storage.copy();
            copy.setCount(1);
            this.items.set(OUTPUT_SLOT, copy);
            this.removeItem(STORAGE_SLOT, 1);
        } else if (output.is(storage.getItem()) && output.getCount() < output.getMaxStackSize()) {
            output.grow(1);
            this.removeItem(STORAGE_SLOT, 1);
        }
    }

    public static boolean isFill(FermentationBarrelBlockEntity barrel) {
        return !barrel.items.get(STORAGE_SLOT).isEmpty() || !barrel.items.get(OUTPUT_SLOT).isEmpty();
    }

    private boolean canCraft(ItemStack result, ItemStack output) {
        return output.isEmpty()
                || result.is(output.getItem()) && output.getCount() != output.getMaxStackSize();
    }

    private void ejectIngredientRemainder(ItemStack remainder) {
        if (this.level == null) {
            return;
        }
        Direction facing = this.getBlockState().getValue(FermentationBarrelBlock.FACING).getOpposite();
        double x = this.worldPosition.getX() + 0.5D + facing.getStepX();
        double z = this.worldPosition.getZ() + 0.5D + facing.getStepZ();
        ItemUtils.spawnItemEntity(this.level, remainder, x, this.worldPosition.getY(), z, Vec3.ZERO);
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        return new int[]{0, 1, 2, 3, 4, 5, OUTPUT_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        return index >= 0 && index <= 5;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return index == OUTPUT_SLOT;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return index <= 5;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
