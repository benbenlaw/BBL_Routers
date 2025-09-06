package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.item.FilterItem;
import com.benbenlaw.routers.screen.util.GhostSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class ConfigScreen extends AbstractContainerScreen<ConfigMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "textures/gui/config_gui.png");

    private EditBox searchBox;
    private ResourceLocation tagLocation;

    public ConfigScreen(ConfigMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 155;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font, this.leftPos + 30, this.topPos + 28, 120, 18, Component.literal("Search"));
        this.searchBox.setMaxLength(50);
        this.searchBox.setResponder(this::onSearchChanged);
        this.addRenderableWidget(this.searchBox);

        this.setInitialFocus(this.searchBox);
        this.searchBox.setFocused(true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = leftPos;
        int y = topPos;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return searchBox.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Prevent 'E' (Inventory key) from closing the GUI
        if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            return true; // consume the event
        }
        return searchBox.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Right-click clears the search box
        if (button == 1) { // 0 = left, 1 = right
            if (searchBox.isMouseOver(mouseX, mouseY)) {
                searchBox.setValue(""); // clear the text
                searchBox.setCursorPosition(0); // move cursor to start
                return true; // consume the click
            }
        }
        return searchBox.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private void onSearchChanged(String text) {

        tagLocation = ResourceLocation.tryParse(searchBox.getValue());


        assert tagLocation != null;
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLocation);
        ItemStack item = menu.player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (tagNotEmpty(tagKey)) {
            searchBox.setTextColor(0x00FF00);
            System.out.println(item);
            if (item.getItem() instanceof FilterItem filterItem) {
                filterItem.setTag(item, tagLocation);
            }
        }
        else {
            searchBox.setTextColor(0xFF5555); // red
            if (item.getItem() instanceof FilterItem filterItem) {
                filterItem.setTag(item, ResourceLocation.parse(""));
            }
        }
    }

    private boolean tagNotEmpty(TagKey<Item> tagKey) {
        var optionalTag = BuiltInRegistries.ITEM.getTag(tagKey);
        System.out.println("Tag present: " + optionalTag.isPresent() + " for tag " + tagKey.location());

        // Check if tag exists and has items
        return optionalTag.isPresent();
    }

}
