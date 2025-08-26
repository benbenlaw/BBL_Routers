package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import com.benbenlaw.routers.screen.util.GhostSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImporterMenu extends AbstractContainerMenu {

    protected ImporterBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;
    public SimpleContainer filterInventory;

    public ImporterMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));

    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public ImporterMenu(int containerID, Inventory inventory, BlockPos blockPos, ContainerData data) {
        super(RoutersMenuTypes.IMPORTER_MENU.get(), containerID);
        this.player = inventory.player;
        this.blockPos = blockPos;
        this.level = inventory.player.level();
        this.data = data;
        this.blockEntity = (ImporterBlockEntity) this.level.getBlockEntity(blockPos);

        this.filterInventory = new SimpleContainer(blockEntity.getFilters().size()) {
            @Override
            public void setChanged() {
                super.setChanged();
                blockEntity.setChanged();
            }
        };

        for (int i = 0; i < blockEntity.getFilters().size(); i++) {
            this.filterInventory.setItem(i, blockEntity.getFilters().get(i));
        }

        // Ghost slots for filters
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9;
                GhostSlot slot = new GhostSlot(filterInventory, index, 8 + col * 18, 20 + row * 18);

                if (blockEntity.getFluidFilters().size() > index) {
                    FluidStack fluid = blockEntity.getFluidFilters().get(index);
                    if (!fluid.isEmpty()) {
                        slot.setFluid(fluid);
                    }
                }

                this.addSlot(slot);
            }
        }

        // Add player inventory and hotbar
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    public void updateFluids(List<FluidStack> fluids) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof GhostSlot ghostSlot) {
                FluidStack fluid = i < fluids.size() ? fluids.get(i) : FluidStack.EMPTY;
                ghostSlot.setFluid(fluid);
                System.out.println("Updated slot " + i + " with fluid: " + fluid);
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int i = 0; i < blockEntity.getFilters().size(); i++) {
            blockEntity.getFilters().set(i, filterInventory.getItem(i));
        }
        blockEntity.setChanged();
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId < 0 || slotId >= slots.size()) {
            super.clicked(slotId, dragType, clickType, player);
            return;
        }
        Slot slot = slots.get(slotId);
        if (slot instanceof GhostSlot ghostSlot) {
            ItemStack carried = player.containerMenu.getCarried();
            var fluid = net.neoforged.neoforge.fluids.FluidUtil.getFluidContained(carried);

            if (fluid.isPresent()) {
                blockEntity.getFilters().set(slotId, ItemStack.EMPTY);
                blockEntity.getFluidFilters().set(slotId, fluid.get());
                ghostSlot.set(ItemStack.EMPTY);
                ghostSlot.setFluid(fluid.get());
            } else if (!carried.isEmpty()) {
                blockEntity.getFilters().set(slotId, carried.copyWithCount(1));
                blockEntity.getFluidFilters().set(slotId, FluidStack.EMPTY);
                ghostSlot.set(carried.copyWithCount(1));
                ghostSlot.setFluid(FluidStack.EMPTY);
            } else {
                blockEntity.getFilters().set(slotId, ItemStack.EMPTY);
                blockEntity.getFluidFilters().set(slotId, FluidStack.EMPTY);
                ghostSlot.set(ItemStack.EMPTY);
                ghostSlot.setFluid(FluidStack.EMPTY);
            }
            blockEntity.setChanged();
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {

        return stillValid(ContainerLevelAccess.create(player.level(), blockPos),
                player, RoutersBlocks.IMPORTER_BLOCK.get());
    }


    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 73 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 131));
        }
    }


}
