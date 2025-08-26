package com.benbenlaw.routers.block.entity;

import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.integration.RoutersCapabilities;
import com.benbenlaw.routers.item.RFUpgradeItem;
import com.benbenlaw.routers.screen.ExporterMenu;
import com.benbenlaw.routers.screen.util.FluidContainerHelper;
import com.benbenlaw.routers.util.RoutersTags;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExporterBlockEntity extends BlockEntity implements MenuProvider, IAttachmentHolder {

    private List<BlockPos> importerPositions;
    public final ContainerData data;
    private final NonNullList<ItemStack> filters = NonNullList.withSize(18, ItemStack.EMPTY);
    private final NonNullList<FluidStack> fluidFilters = NonNullList.withSize(18, FluidStack.EMPTY);
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }
    };

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.routers.exporter_block");
    }

    @Override
    public AbstractContainerMenu createMenu(int container, @NotNull Inventory inventory, @NotNull Player player) {
        return new ExporterMenu(container, inventory, this.getBlockPos(), data);
    }

    public ExporterBlockEntity(BlockPos pos, BlockState state) {
        super(RoutersBlockEntities.EXPORTER_BLOCK_ENTITY.get(), pos, state);
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



    public NonNullList<ItemStack> getFilters() {
        return filters;
    }

    public NonNullList<FluidStack> getFluidFilters() {
        return fluidFilters;
    }

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public void addImporterPosition(BlockPos pos) {
        if (!importerPositions.contains(pos)) {
            importerPositions.add(pos);
            setChanged();
        }
    }

    public void tick() {
        Direction facing = this.getBlockState().getValue(ImporterBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        assert level != null;
        BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);
        Direction inputDirection = facing.getOpposite();

        if (targetBlockEntity != null) {
            IItemHandler targetItemHandler = Capabilities.ItemHandler.BLOCK.getCapability(level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            IFluidHandler targetFluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            IEnergyStorage targetEnergyStorage = Capabilities.EnergyStorage.BLOCK.getCapability(level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            IChemicalHandler targetChemicalHandler;

            if (ModList.get().isLoaded("mekanism")) {
                targetChemicalHandler = RoutersCapabilities.CHEMICAL_HANDLER.getCapability(level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            } else {
                targetChemicalHandler = null;
            }

            if (targetItemHandler != null) {
                NonNullList<ItemStack> exporterFilters = getFilters();

                for (int slot = 0; slot < targetItemHandler.getSlots(); slot++) {
                    ItemStack extracted = targetItemHandler.extractItem(slot, 1, true); // simulate
                    if (extracted.isEmpty()) continue;

                    boolean allowByExporter = exporterFilters.stream().allMatch(ItemStack::isEmpty) ||
                            exporterFilters.stream().anyMatch(f -> !f.isEmpty() && ItemStack.isSameItemSameComponents(f, extracted));

                    if (!allowByExporter) continue;

                    for (BlockPos importerPos : importerPositions) {
                        BlockEntity be = level.getBlockEntity(importerPos);
                        if (!(be instanceof ImporterBlockEntity importer)) continue;

                        IItemHandler importerHandler = importer.getTargetHandler();
                        if (importerHandler == null) continue;

                        NonNullList<ItemStack> importerFilters = importer.getFilters();

                        boolean allowByImporter = importerFilters.stream().allMatch(ItemStack::isEmpty) ||
                                importerFilters.stream().anyMatch(f -> !f.isEmpty() && ItemStack.isSameItemSameComponents(f, extracted));

                        if (!allowByImporter) continue;

                        ItemStack remainder = ItemHandlerHelper.insertItem(importerHandler, extracted, false);
                        int inserted = extracted.getCount() - remainder.getCount();

                        if (inserted > 0) {
                            targetItemHandler.extractItem(slot, inserted, false);
                            return;
                        }
                    }
                }
            }

            if (targetFluidHandler != null) {
                NonNullList<FluidStack> exporterFluidFilters = getFluidFilters();

                for (int slot = 0; slot < targetFluidHandler.getTanks(); slot++) {

                    int maxTransfer = 100; // tweak or upgrade value

                    FluidStack simulatedDrain = targetFluidHandler.drain(maxTransfer, IFluidHandler.FluidAction.SIMULATE);
                    if (simulatedDrain.isEmpty()) continue;

                    boolean allowByExporter = exporterFluidFilters.stream().allMatch(FluidStack::isEmpty) ||
                            exporterFluidFilters.stream().anyMatch(f -> !f.isEmpty() && FluidStack.isSameFluidSameComponents(f, simulatedDrain));

                    if (!allowByExporter) continue;

                    for (BlockPos importerPos : importerPositions) {
                        BlockEntity be = level.getBlockEntity(importerPos);
                        if (!(be instanceof ImporterBlockEntity importer)) continue;

                        IFluidHandler importerFluid = importer.getFluidHandler();
                        if (importerFluid == null) continue;

                        NonNullList<FluidStack> importerFilters = importer.getFluidFilters();
                        boolean allowByImporter = importerFilters.stream().allMatch(FluidStack::isEmpty) ||
                                importerFilters.stream().anyMatch(f -> !f.isEmpty() && FluidStack.isSameFluidSameComponents(f, simulatedDrain));
                        if (!allowByImporter) continue;
                        int canReceive = importerFluid.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
                        if (canReceive <= 0) continue;
                        FluidStack extracted = targetFluidHandler.drain(canReceive, IFluidHandler.FluidAction.EXECUTE);
                        importerFluid.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                        return;
                    }
                }
            }

            if (targetEnergyStorage != null && hasRFUpgrade()) {
                for (BlockPos importerPos : importerPositions) {
                    BlockEntity be = level.getBlockEntity(importerPos);
                    if (!(be instanceof ImporterBlockEntity importer)) continue;

                    IEnergyStorage importerEnergy = importer.getEnergyStorage();
                    if (importerEnergy == null) continue;

                    int maxTransfer = getRFExtractAmount();

                    int canReceive = importerEnergy.receiveEnergy(maxTransfer, true);
                    int canExtract = targetEnergyStorage.extractEnergy(maxTransfer, true);
                    int transferAmount = Math.min(canReceive, canExtract);
                    if (transferAmount > 0) {
                        targetEnergyStorage.extractEnergy(transferAmount, false);
                        importerEnergy.receiveEnergy(transferAmount, false);
                        break;
                    }
                }
            }

            if (targetChemicalHandler != null) {
                for (BlockPos importerPos : importerPositions) {
                    BlockEntity be = level.getBlockEntity(importerPos);
                    if (!(be instanceof ImporterBlockEntity importer)) continue;

                    IChemicalHandler importerChemical = importer.getChemicalHandler();
                    if (importerChemical == null) continue;

                    int maxTransfer = 100; // tweak or upgrade value

                    for (int tank = 0; tank < targetChemicalHandler.getChemicalTanks(); tank++) {
                        ChemicalStack stackInTank = targetChemicalHandler.getChemicalInTank(tank);
                        if (stackInTank.isEmpty()) continue;

                        ChemicalStack toExtract = stackInTank.copy();
                        toExtract.setAmount(Math.min(stackInTank.getAmount(), maxTransfer));

                        ChemicalStack remainder = importerChemical.insertChemical(toExtract, Action.SIMULATE);
                        long insertedAmount = toExtract.getAmount() - remainder.getAmount();
                        if (insertedAmount <= 0) continue;

                        ChemicalStack extracted = targetChemicalHandler.extractChemical(insertedAmount, Action.EXECUTE);
                        importerChemical.insertChemical(extracted, Action.EXECUTE);

                        break;
                    }
                }
            }

        }
    }

    public boolean hasRFUpgrade() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof RFUpgradeItem) {
                return true;
            }
        }
        return false;
    }

    public int getRFExtractAmount() {
        int total = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof RFUpgradeItem upgradeItem) {
                total += upgradeItem.getRFPerTick();
            }
        }
        return total;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.put("inventory", this.itemHandler.serializeNBT(provider));

        ContainerHelper.saveAllItems(compoundTag, this.filters, provider);
        FluidContainerHelper.saveAllFluids(compoundTag, this.fluidFilters, true, provider);

        if (importerPositions != null && !importerPositions.isEmpty()) {
            ListTag listTag = new ListTag();
            for (BlockPos pos : importerPositions) {
                CompoundTag posTag = new CompoundTag();
                posTag.putInt("x", pos.getX());
                posTag.putInt("y", pos.getY());
                posTag.putInt("z", pos.getZ());
                listTag.add(posTag);
            }
            compoundTag.put("ImporterPositions", listTag);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_323910_) {
        return saveWithoutMetadata(p_323910_);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(compoundTag, provider);

        this.itemHandler.deserializeNBT(provider, compoundTag.getCompound("inventory"));

        ContainerHelper.loadAllItems(compoundTag, this.filters, provider);
        FluidContainerHelper.loadAllFluids(compoundTag, fluidFilters, provider);

        importerPositions = new ArrayList<>();
        if (compoundTag.contains("ImporterPositions")) {
            ListTag listTag = compoundTag.getList("ImporterPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag posTag = listTag.getCompound(i);
                BlockPos pos = new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
                importerPositions.add(pos);
            }
        }
    }


    public List<BlockPos> getImporterPositions() {
        return importerPositions;
    }
}