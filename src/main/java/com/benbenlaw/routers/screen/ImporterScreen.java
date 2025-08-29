package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.screen.util.GhostSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class ImporterScreen extends AbstractContainerScreen<ImporterMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "textures/gui/importer_gui.png");

    public ImporterScreen(ImporterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 155;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = leftPos;
        int y = topPos;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        for (Slot slot : this.menu.slots) {
            if (slot instanceof GhostSlot ghostSlot && ghostSlot.hasFluid()) {
                FluidStack fluid = ghostSlot.getGhostFluid();

                if (!fluid.isEmpty()) {
                    renderFluidStack(guiGraphics, fluid, x + slot.x, y + slot.y, 16, 16);
                }
            }
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void renderFluidStack(GuiGraphics guiGraphics, FluidStack fluid, int x, int y, int width, int height) {
        if (fluid.isEmpty()) return;

        var extensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        var texture = extensions.getStillTexture(fluid);
        var atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        var sprite = atlas.apply(texture);

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());

        int color = extensions.getTintColor(fluid);
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderSystem.setShaderColor(r, g, b, 1.0F);

        guiGraphics.blit(x, y, 0, width, height, sprite);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        for (Slot slot : menu.slots) {
            if (isHovering(slot, mouseX, mouseY)) {
                if (slot instanceof GhostSlot ghostSlot) {
                    ItemStack stack = ghostSlot.getItem();

                    if (!stack.isEmpty()) {
                        guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
                    }
                    else if (ghostSlot.hasFluid() && !ghostSlot.getGhostFluid().isEmpty()) {
                        Component fluidName = ghostSlot.getGhostFluid().getHoverName();
                        guiGraphics.renderTooltip(font, fluidName, mouseX, mouseY);
                    }
                    else {
                        Component emptyText = Component.translatable("tooltip.routers.exporter_filter_slots").withStyle(ChatFormatting.GRAY);
                        guiGraphics.renderTooltip(font, emptyText, mouseX, mouseY);
                    }
                }
            }
        }
    }

    private boolean isHovering(Slot slot, double mouseX, double mouseY) {
        return mouseX >= leftPos + slot.x && mouseX < leftPos + slot.x + 16
                && mouseY >= topPos + slot.y && mouseY < topPos + slot.y + 16;
    }

}