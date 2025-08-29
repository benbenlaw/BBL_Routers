package com.benbenlaw.routers.item;

import com.benbenlaw.routers.util.RoutersTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class UpgradeItem extends Item {

    private final int extractValuePerOperation;

    public UpgradeItem(Properties properties, int extractValuePerOperation) {
        super(properties);
        this.extractValuePerOperation = extractValuePerOperation;
    }

    public int getExtractAmount() {
        return extractValuePerOperation;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {

        if (Screen.hasShiftDown()) {

            if (stack.is(RoutersTags.Items.RF_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.rf_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.ITEM_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.item_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.FLUID_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.fluid_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.CHEMICAL_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.chemical_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.SPEED_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.speed_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.SOURCE_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.source_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
            if (stack.is(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                components.add(Component.translatable("tooltip.routers.round_robin_upgrade", extractValuePerOperation).withStyle(ChatFormatting.YELLOW));
            }
        } else {
            components.add(Component.translatable("tooltip.routers.hold_shift").withStyle(ChatFormatting.YELLOW));
        }

    }
}
