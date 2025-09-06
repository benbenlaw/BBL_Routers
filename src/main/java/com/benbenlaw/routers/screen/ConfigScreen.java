package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.item.FilterItem;
import com.benbenlaw.routers.item.RoutersDataComponents;
import com.benbenlaw.routers.item.RoutersItems;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConfigScreen extends AbstractContainerScreen<ConfigMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "textures/gui/config_gui.png");

    private EditBox searchBox;
    private final List<ItemStack> previewStacks = new ArrayList<>();
    private int previewIndex = 0;
    private long lastSwitchTime = 0;
    private static final long SWITCH_INTERVAL = 600;

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

        ItemStack item = menu.player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.is(RoutersItems.MOD_FILTER)) {
            this.searchBox.setValue(item.getOrDefault(RoutersDataComponents.MOD_FILTER.get(), ""));
        } else if (item.is(RoutersItems.TAG_FILTER)) {
            ResourceLocation tag = item.getOrDefault(RoutersDataComponents.TAG_FILTER.get(), ResourceLocation.parse("set:me"));
            this.searchBox.setValue(tag.toString());
        }

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

        // Render preview stack
        if (!previewStacks.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSwitchTime >= SWITCH_INTERVAL) {
                previewIndex = (previewIndex + 1) % previewStacks.size();
                lastSwitchTime = currentTime;
            }

            ItemStack stackToRender = previewStacks.get(previewIndex);
            guiGraphics.renderItem(stackToRender, leftPos + 8, topPos + 29);
        }
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
        if (keyCode == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            return true;
        }
        return searchBox.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            if (searchBox.isMouseOver(mouseX, mouseY)) {
                searchBox.setValue("");
                searchBox.setCursorPosition(0);
                return true;
            }
        }
        return searchBox.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private void onSearchChanged(String text) {
        ItemStack item = menu.player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (item.getItem() instanceof FilterItem filterItem) {
            String value = searchBox.getValue();
            boolean valid = false;

            if (item.is(RoutersItems.TAG_FILTER)) {
                ResourceLocation tagLocation = ResourceLocation.tryParse(value);
                if (tagLocation != null) {
                    TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLocation);
                    if (tagNotEmpty(tagKey)) {
                        valid = true;
                        filterItem.setTag(item, tagLocation);
                    }
                }
            } else if (item.is(RoutersItems.MOD_FILTER)) {
                if (modExists(value)) {
                    valid = true;
                    filterItem.setMod(item, value);
                }
            }

            searchBox.setTextColor(valid ? 0x00FF00 : 0xFF5555);

            if (!valid) {
                if (item.is(RoutersItems.TAG_FILTER)) {
                    filterItem.setTag(item, ResourceLocation.tryParse(""));
                } else if (item.is(RoutersItems.MOD_FILTER)) {
                    filterItem.setMod(item, "");
                }
            } else {
                updatePreviewStacks();
            }
        }
    }



    private boolean tagNotEmpty(TagKey<Item> tagKey) {
        var optionalTag = BuiltInRegistries.ITEM.getTag(tagKey);
        return optionalTag.isPresent();
    }

    private boolean modExists(String modId) {
        for (var mod : ModList.get().getMods()) {
            if (mod.getModId().equals(modId)) {
                return true;
            }
        }
        return false;
    }

    private void updatePreviewStacks() {
        previewStacks.clear();

        ItemStack item = menu.player.getItemBySlot(EquipmentSlot.MAINHAND);
        if (item.is(RoutersItems.TAG_FILTER)) {
            ResourceLocation tagLoc = item.getOrDefault(RoutersDataComponents.TAG_FILTER.get(), Objects.requireNonNull(ResourceLocation.tryParse("minecraft:stone")));
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLoc);
            BuiltInRegistries.ITEM.getTag(tagKey).ifPresent(tagSet ->
                    tagSet.forEach(holder -> previewStacks.add(new ItemStack(holder.value())))
            );
        } else if (item.is(RoutersItems.MOD_FILTER)) {
            String modId = item.getOrDefault(RoutersDataComponents.MOD_FILTER.get(), "");
            BuiltInRegistries.ITEM.forEach(regItem -> {
                ResourceLocation regName = BuiltInRegistries.ITEM.getKey(regItem);
                if (regName != null && regName.getNamespace().equals(modId)) {
                    previewStacks.add(new ItemStack(regItem));
                }
            });
        }

        previewIndex = 0;
        lastSwitchTime = System.currentTimeMillis();
    }

}
