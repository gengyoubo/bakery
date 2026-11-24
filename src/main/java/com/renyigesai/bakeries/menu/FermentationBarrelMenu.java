package com.renyigesai.bakeries.menu;

import com.renyigesai.bakeries.common.fermentation.FermentationBarrelBlockEntity;
import com.renyigesai.bakeries.init.BakeriesMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FermentationBarrelMenu extends AbstractMachineMenu {
    private static final int SLOT_COUNT = 9;
    private static final int MACHINE_SLOT_COUNT = 8;

    public FermentationBarrelMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, null, new SimpleContainerData(1));
    }

    public FermentationBarrelMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(BakeriesMenuTypes.FERMENTATION_BARREL, syncId, playerInventory,
                container == null ? new SimpleContainer(SLOT_COUNT) : container, MACHINE_SLOT_COUNT, data);

        int inputStartX = 31;
        int inputStartY = 21;
        int borderSlotSize = 18;
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 3; column++) {
                this.addSlot(new Slot(this.container, row * 3 + column,
                        inputStartX + column * borderSlotSize, inputStartY + row * borderSlotSize));
            }
        }

        this.addSlot(new Slot(this.container, 7, 122, 24) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(@NotNull Player player) {
                return false;
            }
        });
        this.addSlot(new Slot(this.container, 8, 122, 53));
        this.addPlayerInventorySlots(playerInventory);
    }

    public int getCookingTotalTime() {
        return this.data.get(0);
    }

    public int getMaxCookingTime() {
        return FermentationBarrelBlockEntity.MAX_PROGRESS;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (slotIndex < 6) {
                if (!this.moveItemStackTo(stack, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex == 7) {
                if (!this.moveItemStackTo(stack, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 6, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
