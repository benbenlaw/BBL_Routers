package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.item.FilterItem;
import com.benbenlaw.routers.item.RoutersDataComponents;
import com.benbenlaw.routers.item.RoutersItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigScreen extends AbstractContainerScreen<ConfigMenu> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Routers.MOD_ID, "textures/gui/config_gui.png");

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
            Identifier tag = item.getOrDefault(RoutersDataComponents.TAG_FILTER.get(), Identifier.parse("set:me"));
            this.searchBox.setValue(tag.toString());
        }

        this.addRenderableWidget(this.searchBox);

        this.setInitialFocus(this.searchBox);
        this.searchBox.setFocused(true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

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

    public boolean charTyped(CharacterEvent event) {
        return searchBox.charTyped(event) || super.charTyped(event);
    }

    public boolean keyPressed(KeyEvent event) {
        if (event.input() == Minecraft.getInstance().options.keyInventory.getKey().getValue()) {
            return true;
        }
        return searchBox.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == 1) {
            if (searchBox.isMouseOver(event.x(), event.y())) {
                searchBox.setValue("");
                searchBox.setCursorPosition(0);
                return true;
            }
        }
        return searchBox.mouseClicked(event, isDoubleClick) || super.mouseClicked(event, isDoubleClick);
    }

    private void onSearchChanged(String text) {
        ItemStack item = menu.player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (item.getItem() instanceof FilterItem filterItem) {
            String value = searchBox.getValue();
            boolean valid = false;

            if (item.is(RoutersItems.TAG_FILTER)) {
                Identifier tagLocation = Identifier.tryParse(value);
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
                    filterItem.setTag(item, Identifier.tryParse(""));
                } else if (item.is(RoutersItems.MOD_FILTER)) {
                    filterItem.setMod(item, "");
                }
            } else {
                updatePreviewStacks();
            }

        }
    }


    private boolean tagNotEmpty(TagKey<Item> tagKey) {
        var optionalTag = BuiltInRegistries.ITEM.get(tagKey);
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
            Identifier tagLoc = item.getOrDefault(RoutersDataComponents.TAG_FILTER.get(), Objects.requireNonNull(Identifier.tryParse("minecraft:stone")));
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLoc);
            BuiltInRegistries.ITEM.get(tagKey).ifPresent(tagSet ->
                    tagSet.forEach(holder -> previewStacks.add(new ItemStack(holder.value())))
            );
        } else if (item.is(RoutersItems.MOD_FILTER)) {
            String modId = item.getOrDefault(RoutersDataComponents.MOD_FILTER.get(), "");
            BuiltInRegistries.ITEM.forEach(regItem -> {
                Identifier regName = BuiltInRegistries.ITEM.getKey(regItem);
                if (regName != null && regName.getNamespace().equals(modId)) {
                    previewStacks.add(new ItemStack(regItem));
                }
            });
        }

        previewIndex = 0;
        lastSwitchTime = System.currentTimeMillis();
    }

}
