package com.benbenlaw.routers.screen;

import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
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
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExporterMenu extends AbstractContainerMenu {

    protected ExporterBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;
    public SimpleContainer filterInventory;

    public ExporterMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(2));
    }

    public ExporterMenu(int containerID, Inventory inventory, BlockPos blockPos, ContainerData data) {
        super(RoutersMenuTypes.EXPORTER_MENU.get(), containerID);
        this.player = inventory.player;
        this.blockPos = blockPos;
        this.level = inventory.player.level();
        this.data = data;
        this.blockEntity = (ExporterBlockEntity) this.level.getBlockEntity(blockPos);

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
                GhostSlot slot = new GhostSlot(filterInventory, index, 8 + col * 18, 18 + row * 18);

                if (blockEntity.getFluidFilters().size() > index) {
                    FluidStack fluid = blockEntity.getFluidFilters().get(index);
                    if (!fluid.isEmpty()) {
                        slot.setFluid(fluid);
                    }
                }

                this.addSlot(slot);
            }
        }

        //Upgrade Slots (real slots from block entity)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new SlotItemHandler(blockEntity.getItemStackHandler(), col, 8 + col * 18, 57 ));
        }

        // Add player inventory and hotbar
        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void updateFluids(List<FluidStack> fluids) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) instanceof GhostSlot ghostSlot) {
                FluidStack fluid = i < fluids.size() ? fluids.get(i) : FluidStack.EMPTY;
                ghostSlot.setFluid(fluid);
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

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 45;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int ghostSlotCount = 18; // 2 rows × 9 columns
        int upgradeSlotCount = 9;

        int upgradeFirst = ghostSlotCount;                 // 18
        int upgradeLast = upgradeFirst + upgradeSlotCount; // 27
        int playerFirst = upgradeLast;                     // 27
        int playerLast = playerFirst + VANILLA_SLOT_COUNT; // 27 + 36 = 63

        if (index >= playerFirst && index < playerLast) {
            // Shift-clicked from player inventory → move into upgrade slots
            if (!moveItemStackTo(sourceStack, upgradeFirst, upgradeLast, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= upgradeFirst && index < upgradeLast) {
            // Shift-clicked from upgrade slots → move into player inventory
            if (!moveItemStackTo(sourceStack, playerFirst, playerLast, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Shift-clicked a ghost slot → ignore
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }



    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos),
                player, RoutersBlocks.EXPORTER_BLOCK.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 90 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
        }
    }
}
