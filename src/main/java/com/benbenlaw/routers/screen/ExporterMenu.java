package com.benbenlaw.routers.screen;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.screen.util.GhostSlot;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ExporterMenu extends SimpleAbstractContainerMenu {

    protected ExporterBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public ExporterMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public ExporterMenu(int containerID, Inventory inventory, BlockPos blockPos, ContainerData data) {
        super(RoutersMenuTypes.EXPORTER_MENU.get(), containerID, inventory, blockPos, 9);
        this.player = inventory.player;
        this.blockPos = blockPos;
        this.level = inventory.player.level();
        this.blockEntity = (ExporterBlockEntity) this.level.getBlockEntity(blockPos);
        this.data = data;

        ExporterBlockEntity entity = (ExporterBlockEntity) this.level.getBlockEntity(blockPos);

        checkContainerSize(inventory, 9);
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        assert entity != null;

        for (int i = 0; i < 3; i++) {
            this.addSlot(new InputSlot(blockEntity.getInputHandler(), blockEntity.getInputHandler()::set,
                    i, 8, 17 + i * 18));
        }

        for (int i = 0; i < 3; i++) {
            this.addSlot(new InputSlot(blockEntity.getFilterItemHandler(), blockEntity.getUpgradeHandler()::set,
                    i, 35 + i * 18, 53));
        }

        int outputSlot = 0;

        for (int col = 0; col < 4; col++) {
            this.addSlot(new InputSlot(blockEntity.getOutputHandler(), blockEntity.getOutputHandler()::set, outputSlot++, 98 + col * 18,17));
        }

        for (int col = 0; col < 4; col++) {
            this.addSlot(new InputSlot(blockEntity.getOutputHandler(), blockEntity.getOutputHandler()::set, outputSlot++,98 + col * 18,35));
        }

        for (int col = 0; col < 4; col++) {
            this.addSlot(new InputSlot(blockEntity.getOutputHandler(), blockEntity.getOutputHandler()::set, outputSlot++, 98 + col * 18, 53));
        }

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0 ;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 24; // This is the height in pixels of your arrow
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}