package com.renyigesai.bakeries.screen;

import com.renyigesai.bakeries.BakeriesMod;
import com.renyigesai.bakeries.menu.FermentationBarrelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentationBarrelScreen extends BaseMachineScreen<FermentationBarrelMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BakeriesMod.MODID, "textures/gui/fermentor_gui.png");

    public FermentationBarrelScreen(FermentationBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;
        int time = this.menu.getCookingTotalTime();
        if (time > 0) {
            int progressWidth = (int) (18.0F * ((float) time / this.menu.getMaxCookingTime()));
            guiGraphics.blit(TEXTURE, leftPos + 93, topPos + 51, 176, 25, progressWidth, 4);
            guiGraphics.blit(TEXTURE, leftPos + 98, topPos + 23, 176, 0, 8, 25);
        }
    }
}
