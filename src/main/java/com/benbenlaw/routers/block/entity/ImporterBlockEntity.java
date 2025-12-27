package com.benbenlaw.routers.block.entity;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;
import com.benbenlaw.core.block.entity.handler.fluid.FilterFluidHandler;
import com.benbenlaw.core.block.entity.handler.item.FilterItemHandler;
import com.benbenlaw.core.block.entity.handler.item.InputItemHandler;
import com.benbenlaw.routers.block.RoutersBlockEntities;
import com.benbenlaw.routers.screen.ImporterMenu;
import com.benbenlaw.routers.util.RoutersTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImporterBlockEntity extends SyncableBlockEntity implements MenuProvider, IAttachmentHolder {

    private List<BlockPos> importerPositions;
    public final ContainerData data;
    public final GlobalPos importerPos = GlobalPos.of(this.level.dimension(), this.worldPosition);
    private FilterItemHandler filterItemHandler = new FilterItemHandler(this, 9);
    private FilterFluidHandler filterFluidHandler = new FilterFluidHandler(this, 9);
    private final InputItemHandler upgradeItemHandler = new InputItemHandler(this, 9, (i, stack) -> stack.is(RoutersTags.Items.UPGRADES));

    public ImporterBlockEntity(BlockPos pos, BlockState state) {
        super(RoutersBlockEntities.IMPORTER_BLOCK_ENTITY.get(), pos, state);
        this.importerPositions = new ArrayList<>();

        this.data = new ContainerData() {;
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }



    public void tick() {

    }

    public FilterItemHandler getFilterItemHandler() {
        return filterItemHandler;
    }

    public InputItemHandler getUpgradeItemHandler() {
        return upgradeItemHandler;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.routers.importer");
    }

    @Override
    public AbstractContainerMenu createMenu(int container, @NotNull Inventory inventory, @NotNull Player player) {
        return new ImporterMenu(container, inventory, this.getBlockPos(), data);
    }


}