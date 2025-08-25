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
import org.jetbrains.annotations.NotNull;

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

        for (int row =0; row <2 ; row++) {
            for (int col =0; col <9 ; col++) {
                this.addSlot(new GhostSlot(filterInventory, col + row * 9, 8 + col * 18, 20 + row * 18));
            }
        }

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

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
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = slots.get(slotId);

            ItemStack stack = player.containerMenu.getCarried();

            if (slot instanceof GhostSlot) {

                if (!stack.isEmpty()) {
                    slot.set(stack.copyWithCount(1));
                } else {
                    slot.set(ItemStack.EMPTY);
                }
            }
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
