package com.benbenlaw.routers.block.entity;

import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.config.StartupConfig;
import com.benbenlaw.routers.integration.RoutersCapabilities;
import com.benbenlaw.routers.item.RoutersDataComponents;
import com.benbenlaw.routers.item.RoutersItems;
import com.benbenlaw.routers.item.UpgradeItem;
import com.benbenlaw.routers.screen.ExporterMenu;
import com.benbenlaw.routers.screen.util.FluidContainerHelper;
import com.benbenlaw.routers.util.RoutersTags;
import com.buuz135.industrialforegoingsouls.block.tile.NetworkBlockEntity;
import com.buuz135.industrialforegoingsouls.block_network.SoulNetwork;
import com.buuz135.industrialforegoingsouls.capabilities.ISoulHandler;
import com.buuz135.industrialforegoingsouls.capabilities.SoulCapabilities;
import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.client.recipe_viewer.jei.ChemicalStackHelper;
import mekanism.common.capabilities.holder.chemical.ChemicalTankHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ExporterBlockEntity extends BlockEntity implements MenuProvider, IAttachmentHolder {

    private List<BlockPos> importerPositions;
    public final ContainerData data;
    private String dimension = "";
    private final Map<BlockPos, ImporterBlockEntity> importerCache = new HashMap<>();
    private final NonNullList<ItemStack> filters = NonNullList.withSize(18, ItemStack.EMPTY);
    private final NonNullList<FluidStack> fluidFilters = NonNullList.withSize(18, FluidStack.EMPTY);
    private final NonNullList<?> chemicalFilters;
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

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.is(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                for (int i = 0; i < getSlots(); i++) {
                    if (i != slot && getStackInSlot(i).is(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                        return false;
                    }
                }
                return true;
            } else {
                return stack.is(RoutersTags.Items.UPGRADES);
            }
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

        if (ModList.get().isLoaded("mekanism")) {
            chemicalFilters = MekanismCompat.createChemicalFilters();
        } else {
            chemicalFilters = NonNullList.withSize(18, ItemStack.EMPTY);
        }

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

    public String getDimension() {
        return dimension;
    }

    public String setDimension(String dimension) {
        return this.dimension = dimension;
    }

    public NonNullList<ItemStack> getFilters() {
        return filters;
    }

    public NonNullList<FluidStack> getFluidFilters() {
        return fluidFilters;
    }

    public NonNullList<?> getChemicalFilters() {
        return chemicalFilters;
    }

    public ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    public void toggleImporter(BlockPos importerPos, Player player) {
        if (importerPositions.contains(importerPos)) {
            importerPositions.remove(importerPos);
            player.displayClientMessage(Component.translatable("message.routers.exporter.remove_importer"), true);
        } else {
            importerPositions.add(importerPos);
            player.displayClientMessage(Component.translatable("message.routers.exporter.add_importer"), true);
        }
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
        setChanged();
    }

    private int lastRoundRobinIndex = 0;
    private final Map<BlockPos, Map<BlockPos, Double>> particleProgress = new HashMap<>();

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (level.getGameTime() % 10 == 0) {
            removeInvalidImporters();
        }

        if (dimension == null || dimension.isEmpty()) {
            dimension = level.dimension().location().toString();
            setChanged();
        }

        Direction facing = this.getBlockState().getValue(ImporterBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        assert level != null;
        BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);
        Direction inputDirection = facing.getOpposite();
        int speedPerOperation = getSpeedPerOperation();
        IEnergyStorage targetEnergyStorage = Capabilities.EnergyStorage.BLOCK.getCapability(
                level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);

        // --- Energy ---
        if (targetEnergyStorage != null && hasUpgrade(RoutersTags.Items.RF_UPGRADES)) {
            int maxTransfer = getExtractAmount(RoutersTags.Items.RF_UPGRADES);

            if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                if (!importerPositions.isEmpty()) {
                    int index = lastRoundRobinIndex % importerPositions.size();
                    BlockPos importerPos = importerPositions.get(index);
                    BlockEntity be = findImporter(importerPos);
                    if (be instanceof ImporterBlockEntity importer) {
                        IEnergyStorage importerEnergy = importer.getEnergyStorage();
                        if (importerEnergy != null) {
                            int canReceive = importerEnergy.receiveEnergy(maxTransfer, true);
                            int canExtract = targetEnergyStorage.extractEnergy(maxTransfer, true);
                            int transferAmount = Math.min(canReceive, canExtract);
                            if (transferAmount > 0) {
                                targetEnergyStorage.extractEnergy(transferAmount, false);
                                importerEnergy.receiveEnergy(transferAmount, false);
                                lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                                return;
                            }
                        }
                    }
                    // even if failed, still advance index to keep cycle consistent
                    lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                }
            } else {
                for (BlockPos importerPos : importerPositions) {
                    ImporterBlockEntity importer = findImporter(importerPos);
                    if (importer == null) continue;

                    IEnergyStorage importerEnergy = importer.getEnergyStorage();
                    if (importerEnergy == null) continue;

                    int canReceive = importerEnergy.receiveEnergy(maxTransfer, true);
                    int canExtract = targetEnergyStorage.extractEnergy(maxTransfer, true);
                    int transferAmount = Math.min(canReceive, canExtract);
                    if (transferAmount > 0) {
                        targetEnergyStorage.extractEnergy(transferAmount, false);
                        importerEnergy.receiveEnergy(transferAmount, false);
                        maxTransfer -= transferAmount;
                    }
                }
            }
        }

        if (level.getGameTime() % speedPerOperation == 0 && targetBlockEntity != null) {
            IItemHandler targetItemHandler = Capabilities.ItemHandler.BLOCK.getCapability(
                    level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            IFluidHandler targetFluidHandler = Capabilities.FluidHandler.BLOCK.getCapability(
                    level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection);
            IChemicalHandler targetChemicalHandler = ModList.get().isLoaded("mekanism")
                    ? RoutersCapabilities.CHEMICAL_HANDLER.getCapability(
                    level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection)
                    : null;

            ISourceCap targetSourceHandler = ModList.get().isLoaded("ars_nouveau")
                    ? CapabilityRegistry.SOURCE_CAPABILITY.getCapability(
                    level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection)
                    : null;

            ISoulHandler targetSoulHandler = ModList.get().isLoaded("industrialforegoingsouls")
                    ? SoulCapabilities.BLOCK.getCapability(
                    level, targetPos, level.getBlockState(targetPos), targetBlockEntity, inputDirection)
                    : null;

            Optional<IAirHandlerMachine> targetAirHandler = ModList.get().isLoaded("pneumaticcraft")
                    ? PNCCapabilities.getAirHandler(targetBlockEntity, inputDirection)
                    : Optional.empty();


            Optional<IHeatExchangerLogic> targetHeatHandler = ModList.get().isLoaded("pneumaticcraft")
                    ? PNCCapabilities.getHeatLogic(targetBlockEntity, inputDirection)
                    : Optional.empty();

            // --- Items ---
            if (targetItemHandler != null && hasUpgrade(RoutersTags.Items.ITEM_UPGRADES)) {

                List<Item> expandedExporterFilters = expandFilters(getFilters());

                int maxTransfer = getExtractAmount(RoutersTags.Items.ITEM_UPGRADES);

                for (int slot = 0; slot < targetItemHandler.getSlots(); slot++) {
                    ItemStack extracted = targetItemHandler.extractItem(slot, maxTransfer, true);
                    if (extracted.isEmpty()) continue;

                    // Use expanded filter list here
                    boolean allowByExporter = expandedExporterFilters.isEmpty() ||
                            expandedExporterFilters.contains(extracted.getItem());
                    if (!allowByExporter) continue;

                    if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                        if (!importerPositions.isEmpty()) {
                            int attempts = 0;
                            int index = lastRoundRobinIndex % importerPositions.size();

                            while (attempts < importerPositions.size()) {
                                BlockPos importerPos = importerPositions.get(index);
                                BlockEntity be = findImporter(importerPos);

                                if (be instanceof ImporterBlockEntity importer) {
                                    IItemHandler importerHandler = importer.getTargetHandler();
                                    if (importerHandler != null) {
                                        List<Item> expandedImporterFilters = expandFilters(importer.getFilters());

                                        boolean allowByImporter = expandedImporterFilters.isEmpty() ||
                                                expandedImporterFilters.contains(extracted.getItem());

                                        if (allowByImporter) {
                                            ItemStack remainder = ItemHandlerHelper.insertItem(importerHandler, extracted, false);
                                            int inserted = extracted.getCount() - remainder.getCount();
                                            if (inserted > 0) {
                                                targetItemHandler.extractItem(slot, inserted, false);
                                                lastRoundRobinIndex = (index + 1) % importerPositions.size();
                                                return; // Done transferring this item
                                            }
                                        }
                                    }
                                }

                                // Move to next importer
                                index = (index + 1) % importerPositions.size();
                                attempts++;
                            }

                            // If we get here, item could not be inserted into any importer
                            lastRoundRobinIndex = index;
                        }
                    } else {
                        for (BlockPos importerPos : importerPositions) {
                            ImporterBlockEntity importer = findImporter(importerPos);
                            if (importer == null) continue;

                            IItemHandler importerHandler = importer.getTargetHandler();
                            if (importerHandler == null) continue;

                            NonNullList<ItemStack> importerFilters = importer.getFilters();

                            // Build expanded importer filter list
                            List<Item> expandedImporterFilters = expandFilters(importer.getFilters());


                            boolean allowByImporter = expandedImporterFilters.isEmpty() ||
                                    expandedImporterFilters.contains(extracted.getItem());
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
            }
            // --- Fluids ---
            if (targetFluidHandler != null && hasUpgrade(RoutersTags.Items.FLUID_UPGRADES)) {
                NonNullList<FluidStack> exporterFluidFilters = getFluidFilters();
                int maxTransfer = getExtractAmount(RoutersTags.Items.FLUID_UPGRADES);

                // Check each tank individually
                for (int slot = 0; slot < targetFluidHandler.getTanks(); slot++) {
                    FluidStack tankFluid = targetFluidHandler.getFluidInTank(slot);
                    if (tankFluid.isEmpty()) continue;

                    // Only consider up to maxTransfer amount
                    FluidStack simulatedDrain = new FluidStack(tankFluid.getFluidHolder(), Math.min(maxTransfer, tankFluid.getAmount()));

                    // Exporter filter check
                    boolean allowByExporter = exporterFluidFilters.stream().allMatch(FluidStack::isEmpty) ||
                            exporterFluidFilters.stream().anyMatch(f -> !f.isEmpty() &&
                                    FluidStack.isSameFluidSameComponents(f, simulatedDrain));
                    if (!allowByExporter) continue;

                    // Handle round-robin mode
                    if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                        if (!importerPositions.isEmpty()) {
                            int index = lastRoundRobinIndex % importerPositions.size();
                            BlockPos importerPos = importerPositions.get(index);
                            BlockEntity be = findImporter(importerPos);

                            if (be instanceof ImporterBlockEntity importer) {
                                IFluidHandler importerFluid = importer.getFluidHandler();
                                if (importerFluid != null) {
                                    NonNullList<FluidStack> importerFilters = importer.getFluidFilters();
                                    boolean allowByImporter = importerFilters.stream().allMatch(FluidStack::isEmpty) ||
                                            importerFilters.stream().anyMatch(f -> !f.isEmpty() &&
                                                    FluidStack.isSameFluidSameComponents(f, simulatedDrain));
                                    if (allowByImporter) {
                                        int canReceive = importerFluid.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
                                        if (canReceive > 0) {
                                            // Drain exactly what importer can take
                                            FluidStack request = new FluidStack(simulatedDrain.getFluid(), canReceive);
                                            FluidStack extracted = targetFluidHandler.drain(request, IFluidHandler.FluidAction.EXECUTE);
                                            importerFluid.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

                                            lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                                            return; // stop after successful transfer
                                        }
                                    }
                                }
                            }
                            lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                        }
                    } else {
                        // Normal (non round-robin) mode
                        for (BlockPos importerPos : importerPositions) {
                            ImporterBlockEntity importer = findImporter(importerPos);
                            if (importer == null) continue;

                            IFluidHandler importerFluid = importer.getFluidHandler();
                            if (importerFluid == null) continue;

                            NonNullList<FluidStack> importerFilters = importer.getFluidFilters();
                            boolean allowByImporter = importerFilters.stream().allMatch(FluidStack::isEmpty) ||
                                    importerFilters.stream().anyMatch(f -> !f.isEmpty() &&
                                            FluidStack.isSameFluidSameComponents(f, simulatedDrain));
                            if (!allowByImporter) continue;

                            int canReceive = importerFluid.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
                            if (canReceive <= 0) continue;

                            // Drain exactly what importer can take
                            FluidStack request = new FluidStack(simulatedDrain.getFluid(), canReceive);
                            FluidStack extracted = targetFluidHandler.drain(request, IFluidHandler.FluidAction.EXECUTE);

                            importerFluid.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                            return; // stop after successful transfer
                        }
                    }
                }
            }
            // --- Chemicals ---
            if (targetChemicalHandler != null && hasUpgrade(RoutersTags.Items.CHEMICAL_UPGRADES)) {
                // safe cast; list exists even when Mekanism isn't present
                @SuppressWarnings("unchecked")
                NonNullList<Object> exporterFilters = (NonNullList<Object>) getChemicalFilters();
                int maxTransfer = getExtractAmount(RoutersTags.Items.CHEMICAL_UPGRADES);

                // helper lambdas (local methods would be nicer, but keep inline for single paste)
                final var isEmptyFilter = new java.util.function.Predicate<Object>() {
                    @Override
                    public boolean test(Object f) {
                        if (f == null) return true;
                        // treat the compat empty placeholder as empty if present
                        if (f == MekanismCompat.EMPTY_CHEMICAL) return true;
                        // try reflective isEmpty() if method exists
                        try {
                            java.lang.reflect.Method m = f.getClass().getMethod("isEmpty");
                            Object r = m.invoke(f);
                            if (r instanceof Boolean) return (Boolean) r;
                        } catch (NoSuchMethodException ignored) {
                        } catch (Exception e) {
                            // fallthrough to non-empty
                        }
                        return false;
                    }
                };

                final var matchesFilter = new java.util.function.BiPredicate<Object, Object>() {
                    @Override
                    public boolean test(Object filter, Object chemStack) {
                        // if filter is null/empty => allow (same semantics as fluid/item code)
                        if (filter == null) return true;
                        if (filter == MekanismCompat.EMPTY_CHEMICAL) return true;
                        // chemStack is a ChemicalStack from Mekanism; if it's null, reject
                        if (chemStack == null) return false;

                        // Try direct equals in both directions (covers many compat wrappers)
                        try {
                            if (filter.equals(chemStack)) return true;
                        } catch (Throwable ignored) {}
                        try {
                            if (chemStack.equals(filter)) return true;
                        } catch (Throwable ignored) {}

                        // Fallback: try to call isEmpty() on filter (if not empty we've already failed),
                        // or compare types/names with reflection (best-effort).
                        try {
                            // get something meaningful like "getChemical" / "getType" / "getDefinition" if available
                            java.lang.reflect.Method getTypeMethod = null;
                            for (String name : new String[]{"getType", "getChemical", "getDefinition", "getLeft"}) {
                                try {
                                    getTypeMethod = filter.getClass().getMethod(name);
                                    break;
                                } catch (NoSuchMethodException ignored) {}
                            }
                            java.lang.reflect.Method chemGetTypeMethod = null;
                            Class<?> chemClass = chemStack.getClass();
                            for (String name : new String[]{"getType", "getChemical", "getDefinition", "getLeft"}) {
                                try {
                                    chemGetTypeMethod = chemClass.getMethod(name);
                                    break;
                                } catch (NoSuchMethodException ignored) {}
                            }

                            if (getTypeMethod != null && chemGetTypeMethod != null) {
                                Object fType = getTypeMethod.invoke(filter);
                                Object cType = chemGetTypeMethod.invoke(chemStack);
                                if (fType != null && fType.equals(cType)) return true;
                            }
                        } catch (Throwable ignored) {}

                        return false;
                    }
                };

                // Helper that tests exporter filters the same way your fluids/items do:
                final var exporterAllows = new java.util.function.Predicate<Object>() {
                    @Override
                    public boolean test(Object chemStack) {
                        // If all exporter filters are empty -> allow all
                        boolean allEmpty = true;
                        for (Object f : exporterFilters) {
                            if (!isEmptyFilter.test(f)) { allEmpty = false; break; }
                        }
                        if (allEmpty) return true;

                        // Otherwise, require any filter to match
                        for (Object f : exporterFilters) {
                            if (!isEmptyFilter.test(f) && matchesFilter.test(f, chemStack)) return true;
                        }
                        return false;
                    }
                };

                // ---- round-robin mode ----
                if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                    if (!importerPositions.isEmpty()) {
                        int attempts = 0;
                        int index = lastRoundRobinIndex % importerPositions.size();

                        while (attempts < importerPositions.size()) {
                            BlockPos importerPos = importerPositions.get(index);
                            BlockEntity be = findImporter(importerPos);

                            if (be instanceof ImporterBlockEntity importer) {
                                IChemicalHandler importerChem = importer.getChemicalHandler();
                                if (importerChem != null) {

                                    @SuppressWarnings("unchecked")
                                    NonNullList<Object> importerFilters = (NonNullList<Object>) importer.getChemicalFilters();

                                    for (int tank = 0; tank < targetChemicalHandler.getChemicalTanks(); tank++) {
                                        Object tankStack = targetChemicalHandler.getChemicalInTank(tank);
                                        if (tankStack == null) continue;
                                        // Mek chemical stacks have isEmpty(); try to check that (defensive)
                                        boolean isTankEmpty = false;
                                        try {
                                            java.lang.reflect.Method isEmptyM = tankStack.getClass().getMethod("isEmpty");
                                            Object r = isEmptyM.invoke(tankStack);
                                            if (r instanceof Boolean) isTankEmpty = (Boolean) r;
                                        } catch (Throwable ignored) {}
                                        if (isTankEmpty) continue;

                                        // exporter-level allow / importer-level allow (same semantics as fluids)
                                        if (!exporterAllows.test(tankStack)) continue;

                                        // If all importer filters are empty => importer allows all
                                        boolean importerAllEmpty = true;
                                        for (Object f : importerFilters) {
                                            if (!isEmptyFilter.test(f)) { importerAllEmpty = false; break; }
                                        }
                                        if (!importerAllEmpty) {
                                            boolean anyMatch = false;
                                            for (Object f : importerFilters) {
                                                if (!isEmptyFilter.test(f) && matchesFilter.test(f, tankStack)) { anyMatch = true; break; }
                                            }
                                            if (!anyMatch) continue; // importer rejects this chemical
                                        }

                                        // simulate insertion
                                        // make a copy with amount limited
                                        Object toExtract;
                                        try {
                                            java.lang.reflect.Method copyM = tankStack.getClass().getMethod("copy");
                                            toExtract = copyM.invoke(tankStack);
                                            // setAmount
                                            try {
                                                java.lang.reflect.Method setAmount = toExtract.getClass().getMethod("setAmount", long.class);
                                                long amount = Math.min((long) toExtract.getClass().getMethod("getAmount").invoke(tankStack), (long) maxTransfer);
                                                setAmount.invoke(toExtract, amount);
                                            } catch (NoSuchMethodException ignored) {}
                                        } catch (Throwable t) {
                                            // if reflection fails, skip this tank
                                            continue;
                                        }

                                        // simulate insert
                                        try {
                                            java.lang.reflect.Method insertSim = importerChem.getClass().getMethod("insertChemical", toExtract.getClass(), Action.class);
                                            Object remainder = insertSim.invoke(importerChem, toExtract, Action.SIMULATE);
                                            // remainder amount compare
                                            long beforeAmount = (long) toExtract.getClass().getMethod("getAmount").invoke(toExtract);
                                            long remainderAmount = (long) remainder.getClass().getMethod("getAmount").invoke(remainder);
                                            long inserted = beforeAmount - remainderAmount;
                                            if (inserted > 0) {
                                                // actually extract then insert
                                                java.lang.reflect.Method extractExec = targetChemicalHandler.getClass().getMethod("extractChemical", long.class, Action.class);
                                                Object extracted = extractExec.invoke(targetChemicalHandler, inserted, Action.EXECUTE);
                                                java.lang.reflect.Method insertExec = importerChem.getClass().getMethod("insertChemical", extracted.getClass(), Action.class);
                                                insertExec.invoke(importerChem, extracted, Action.EXECUTE);

                                                lastRoundRobinIndex = (index + 1) % importerPositions.size();
                                                return; // done
                                            }
                                        } catch (Throwable e) {
                                            // simulation failed; skip tank
                                            continue;
                                        }
                                    }
                                }
                            }

                            index = (index + 1) % importerPositions.size();
                            attempts++;
                        }
                        lastRoundRobinIndex = index;
                    }
                }

                // ---- normal (non round-robin) mode ----
                else {
                    for (BlockPos importerPos : importerPositions) {
                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        IChemicalHandler importerChem = importer.getChemicalHandler();
                        if (importerChem == null) continue;

                        @SuppressWarnings("unchecked")
                        NonNullList<Object> importerFilters = (NonNullList<Object>) importer.getChemicalFilters();

                        for (int tank = 0; tank < targetChemicalHandler.getChemicalTanks(); tank++) {
                            Object tankStack = targetChemicalHandler.getChemicalInTank(tank);
                            if (tankStack == null) continue;
                            boolean isTankEmpty = false;
                            try {
                                java.lang.reflect.Method isEmptyM = tankStack.getClass().getMethod("isEmpty");
                                Object r = isEmptyM.invoke(tankStack);
                                if (r instanceof Boolean) isTankEmpty = (Boolean) r;
                            } catch (Throwable ignored) {}
                            if (isTankEmpty) continue;

                            if (!exporterAllows.test(tankStack)) continue;

                            boolean importerAllEmpty = true;
                            for (Object f : importerFilters) {
                                if (!isEmptyFilter.test(f)) { importerAllEmpty = false; break; }
                            }
                            if (!importerAllEmpty) {
                                boolean anyMatch = false;
                                for (Object f : importerFilters) {
                                    if (!isEmptyFilter.test(f) && matchesFilter.test(f, tankStack)) { anyMatch = true; break; }
                                }
                                if (!anyMatch) continue;
                            }

                            // simulate insertion same as above
                            Object toExtract;
                            try {
                                java.lang.reflect.Method copyM = tankStack.getClass().getMethod("copy");
                                toExtract = copyM.invoke(tankStack);
                                try {
                                    java.lang.reflect.Method setAmount = toExtract.getClass().getMethod("setAmount", long.class);
                                    long amount = Math.min((long) toExtract.getClass().getMethod("getAmount").invoke(tankStack), (long) maxTransfer);
                                    setAmount.invoke(toExtract, amount);
                                } catch (NoSuchMethodException ignored) {}
                            } catch (Throwable t) {
                                continue;
                            }

                            try {
                                java.lang.reflect.Method insertSim = importerChem.getClass().getMethod("insertChemical", toExtract.getClass(), Action.class);
                                Object remainder = insertSim.invoke(importerChem, toExtract, Action.SIMULATE);
                                long beforeAmount = (long) toExtract.getClass().getMethod("getAmount").invoke(toExtract);
                                long remainderAmount = (long) remainder.getClass().getMethod("getAmount").invoke(remainder);
                                long inserted = beforeAmount - remainderAmount;
                                if (inserted > 0) {
                                    java.lang.reflect.Method extractExec = targetChemicalHandler.getClass().getMethod("extractChemical", long.class, Action.class);
                                    Object extracted = extractExec.invoke(targetChemicalHandler, inserted, Action.EXECUTE);
                                    java.lang.reflect.Method insertExec = importerChem.getClass().getMethod("insertChemical", extracted.getClass(), Action.class);
                                    insertExec.invoke(importerChem, extracted, Action.EXECUTE);
                                    return;
                                }
                            } catch (Throwable e) {
                                continue;
                            }
                        }
                    }
                }
            }
            // --- Ars Nouveau Source ---
            if (targetSourceHandler != null && hasUpgrade(RoutersTags.Items.SOURCE_UPGRADES)) {
                int maxTransfer = getExtractAmount(RoutersTags.Items.SOURCE_UPGRADES);

                if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                    if (!importerPositions.isEmpty()) {
                        int index = lastRoundRobinIndex % importerPositions.size();
                        BlockPos importerPos = importerPositions.get(index);
                        BlockEntity be = findImporter(importerPos);
                        if (be instanceof ImporterBlockEntity importer) {
                            ISourceCap importerSource = importer.getSourceHandler();
                            if (importerSource != null) {
                                int canExtract = targetSourceHandler.extractSource(maxTransfer, true);
                                int canReceive = importerSource.receiveSource(canExtract, true);
                                int transferAmount = Math.min(canExtract, canReceive);
                                if (transferAmount > 0) {
                                    targetSourceHandler.extractSource(transferAmount, false);
                                    importerSource.receiveSource(transferAmount, false);
                                    lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                                    return;
                                }
                            }
                        }
                        lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                    }
                } else {
                    for (BlockPos importerPos : importerPositions) {
                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        ISourceCap importerSource = importer.getSourceHandler();
                        if (importerSource == null) continue;

                        int canExtract = targetSourceHandler.extractSource(maxTransfer, true);
                        int canReceive = importerSource.receiveSource(canExtract, true);
                        int transferAmount = Math.min(canExtract, canReceive);
                        if (transferAmount > 0) {
                            targetSourceHandler.extractSource(transferAmount, false);
                            importerSource.receiveSource(transferAmount, false);
                            maxTransfer -= transferAmount;
                            if (maxTransfer <= 0) break;
                        }
                    }
                }
            }
            // --- Industrial Forgoing Souls Soul ---
            if (targetSoulHandler != null && hasUpgrade(RoutersTags.Items.SOUL_UPGRADES)) {
                int maxTransfer = getExtractAmount(RoutersTags.Items.SOUL_UPGRADES);

                if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                    if (!importerPositions.isEmpty()) {
                        int index = lastRoundRobinIndex % importerPositions.size();
                        BlockPos importerPos = importerPositions.get(index);
                        BlockEntity be = findImporter(importerPos);
                        if (be instanceof ImporterBlockEntity importer) {
                            SoulNetwork importerSoulNetwork = importer.getSoulNetwork() instanceof SoulNetwork soulNet ? soulNet : null;
                            if (importerSoulNetwork != null) {
                                int canExtract = targetSoulHandler.drain(maxTransfer, ISoulHandler.Action.SIMULATE);
                                int available = importerSoulNetwork.getMaxSouls() - importerSoulNetwork.getSoulAmount();
                                int transferAmount = Math.min(canExtract, available);

                                if (transferAmount > 0) {
                                    int drained = targetSoulHandler.drain(transferAmount, ISoulHandler.Action.EXECUTE);
                                    importerSoulNetwork.addSouls(level, drained);

                                    lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                                    return;
                                }
                            }
                        }
                        lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                    }
                } else {
                    for (BlockPos importerPos : importerPositions) {
                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        SoulNetwork importerSoulNetwork = importer.getSoulNetwork() instanceof SoulNetwork soulNet ? soulNet : null;
                        if (importerSoulNetwork == null) continue;

                        int canExtract = targetSoulHandler.drain(maxTransfer, ISoulHandler.Action.SIMULATE);
                        int available = importerSoulNetwork.getMaxSouls() - importerSoulNetwork.getSoulAmount();
                        int transferAmount = Math.min(canExtract, available);

                        if (transferAmount > 0) {
                            int drained = targetSoulHandler.drain(transferAmount, ISoulHandler.Action.EXECUTE);
                            importerSoulNetwork.addSouls(level, drained);

                            maxTransfer -= drained;
                            if (maxTransfer <= 0) break;
                        }
                    }
                }
            }
            // --- PneumaticCraft Air Transfer ---
            // --- PneumaticCraft Air Transfer ---
            // --- PneumaticCraft Air Transfer ---
            // --- PneumaticCraft Air Transfer ---
            if (!importerPositions.isEmpty() && hasUpgrade(RoutersTags.Items.PRESSURE_UPGRADES) && targetAirHandler.isPresent()) {
                IAirHandlerMachine source = targetAirHandler.get();
                if (source == null) return; // safety check

                float available = source.getAir(); // get actual pressure
                if (available <= 0f) return; // nothing to transfer

                int maxTransfer = getExtractAmount(RoutersTags.Items.PRESSURE_UPGRADES);

                if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                    if (!importerPositions.isEmpty()) {
                        int attempts = 0;
                        int index = lastRoundRobinIndex % importerPositions.size();

                        while (attempts < importerPositions.size()) {
                            BlockPos importerPos = importerPositions.get(index);
                            BlockEntity be = findImporter(importerPos);

                            if (be instanceof ImporterBlockEntity importer) {
                                IAirHandlerMachine importerSource = importer.getPressureHandler();
                                if (importerSource != null) {
                                    // Determine how much we can actually transfer
                                    int transfer = (int)Math.min(maxTransfer, Math.floor(available));
                                    if (transfer > 0) {
                                        source.addAir(-transfer);       // remove from exporter
                                        importerSource.addAir(transfer); // add to importer
                                        importerSource.setConnectableFaces(
                                                Collections.singleton(importer.getBlockState().getValue(ImporterBlock.FACING).getOpposite())
                                        );

                                        lastRoundRobinIndex = (index + 1) % importerPositions.size();
                                        return; // stop after first successful transfer
                                    }
                                }
                            }

                            // move to next importer
                            index = (index + 1) % importerPositions.size();
                            attempts++;
                        }

                        // if nothing transferred, still advance index
                        lastRoundRobinIndex = index;
                    }
                } else {
                    // Normal (non round-robin) mode
                    for (BlockPos importerPos : importerPositions) {
                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        IAirHandlerMachine importerSource = importer.getPressureHandler();
                        if (importerSource == null) continue;

                        int transfer = (int)Math.min(maxTransfer, Math.floor(available));
                        if (transfer > 0) {
                            source.addAir(-transfer);
                            importerSource.addAir(transfer);
                            importerSource.setConnectableFaces(
                                    Collections.singleton(importer.getBlockState().getValue(ImporterBlock.FACING).getOpposite())
                            );
                            break; // stop after first successful transfer
                        }
                    }
                }
            }



            // --- PneumaticCraft Heat Transfer ---
            if (!importerPositions.isEmpty() && hasUpgrade(RoutersTags.Items.HEAT_UPGRADES_PC) && targetHeatHandler.isPresent()) {
                IHeatExchangerLogic sourceHeatHandler = targetHeatHandler.get();
                double maxHeatTransfer = getExtractAmount(RoutersTags.Items.HEAT_UPGRADES_PC);

                if (hasUpgrade(RoutersTags.Items.ROUND_ROBIN_UPGRADES)) {
                    int startIndex = lastRoundRobinIndex % importerPositions.size();

                    for (int i = 0; i < importerPositions.size() && maxHeatTransfer > 0; i++) {
                        int index = (startIndex + i) % importerPositions.size();
                        BlockPos importerPos = importerPositions.get(index);
                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        IHeatExchangerLogic importerHeatHandler = PNCCapabilities.getHeatLogic(importer, null).orElse(null);
                        if (importerHeatHandler == null) continue;

                        double sourceTemp = sourceHeatHandler.getTemperature();
                        double targetTemp = importerHeatHandler.getTemperature();
                        double heatDifference = sourceTemp - targetTemp;

                        if (heatDifference <= 0) continue;

                        double transferAmount = Math.min(maxHeatTransfer, heatDifference);

                        sourceHeatHandler.addHeat(-transferAmount);
                        importerHeatHandler.addHeat(transferAmount);
                        maxHeatTransfer -= transferAmount;

                        lastRoundRobinIndex = (lastRoundRobinIndex + 1) % importerPositions.size();
                        break; // only one per tick in round-robin
                    }

                } else {
                    // Non-round-robin: iterate all importers
                    for (BlockPos importerPos : importerPositions) {
                        if (maxHeatTransfer <= 0) break;

                        ImporterBlockEntity importer = findImporter(importerPos);
                        if (importer == null) continue;

                        IHeatExchangerLogic importerHeatHandler = PNCCapabilities.getHeatLogic(importer, null).orElse(null);
                        if (importerHeatHandler == null) continue;

                        double sourceTemp = sourceHeatHandler.getTemperature();
                        double targetTemp = importerHeatHandler.getTemperature();
                        double heatDifference = sourceTemp - targetTemp;

                        if (heatDifference <= 0) continue;

                        double transferAmount = Math.min(maxHeatTransfer, heatDifference);

                        sourceHeatHandler.addHeat(-transferAmount);
                        importerHeatHandler.addHeat(transferAmount);
                        maxHeatTransfer -= transferAmount;
                    }
                }
            }
        }
    }

    private boolean matchesChemicalFilter(Object filter, ChemicalStack chemical) {
        if (filter == null || chemical == null) return false;
        if (!ModList.get().isLoaded("mekanism")) return false;

        // Safe cast
        @SuppressWarnings("unchecked")
        NonNullList<Object> chemicalFilters = (NonNullList<Object>) this.getChemicalFilters();

        // Compare filter to chemical
        return filter.equals(chemical); // Or use Mekanism utility to compare stacks
    }

    @Nullable
    private ImporterBlockEntity findImporter(BlockPos pos) {
        // Check cache first
        ImporterBlockEntity cached = importerCache.get(pos);
        if (cached != null && !cached.isRemoved()) {
            return cached;
        }

        if (!(this.level instanceof ServerLevel serverLevel)) return null;
        MinecraftServer server = serverLevel.getServer();
        boolean allowCrossDim = hasUpgrade(RoutersTags.Items.DIMENSIONAL_UPGRADES);

        if (allowCrossDim) {
            // Search across all loaded dimensions
            for (ServerLevel candidate : server.getAllLevels()) {
                BlockEntity be = candidate.getBlockEntity(pos);
                if (be instanceof ImporterBlockEntity importer) {
                    // Verify stored importer dimension matches candidate world
                    String importerDim = importer.getDimension();
                    String candidateDim = candidate.dimension().location().toString();
                    if (importerDim.equals(candidateDim)) {
                        importerCache.put(pos, importer); // cache result
                        return importer;
                    }
                }
            }
        } else {
            // Restrict search to current dimension only
            BlockEntity be = serverLevel.getBlockEntity(pos);
            if (be instanceof ImporterBlockEntity importer) {
                String importerDim = importer.getDimension();
                String expectedDim = serverLevel.dimension().location().toString();
                if (importerDim.equals(expectedDim)) {
                    importerCache.put(pos, importer); // cache result
                    return importer;
                }
            }
        }

        return null;
    }


    private List<Item> expandFilters(NonNullList<ItemStack> filters) {
        List<Item> expanded = new ArrayList<>();
        for (ItemStack filter : filters) {
            if (filter.isEmpty()) continue;

            if (filter.is(RoutersItems.TAG_FILTER)) {
                // Expand tag filter
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, Objects.requireNonNull(filter.get(RoutersDataComponents.TAG_FILTER.get())));
                assert level != null;
                level.registryAccess()
                        .registryOrThrow(Registries.ITEM)
                        .getTag(tagKey)
                        .ifPresent(tagSet -> tagSet.forEach(holder -> expanded.add(holder.value())));
            } else if (filter.is(RoutersItems.MOD_FILTER)) {
                String modId = filter.get(RoutersDataComponents.MOD_FILTER.get());
                if (modId != null && !modId.isEmpty()) {
                    assert level != null;
                    level.registryAccess()
                            .registryOrThrow(Registries.ITEM)
                            .forEach(item -> {
                                ResourceLocation id = level.registryAccess().registryOrThrow(Registries.ITEM).getKey(item);
                                if (id != null && id.getNamespace().equals(modId)) {
                                    expanded.add(item);
                                }
                            });
                }
            }else {
                expanded.add(filter.getItem());
            }
        }
        return expanded;
    }


    public int getSpeedPerOperation() {
        if (hasUpgrade(RoutersTags.Items.SPEED_UPGRADES)) {
            int speed = 0;
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.is(RoutersTags.Items.SPEED_UPGRADES) && stack.getItem() instanceof UpgradeItem upgradeItem) {
                    speed += upgradeItem.getExtractAmount();
                    break;
                }
            }
            return speed;
        } else {
            return StartupConfig.defaultSpeedPerOperation.get();
        }
    }

    public boolean hasUpgrade(TagKey<Item> tag) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(tag))
                return true;
            }
        return false;
    }

    public int getExtractAmount(TagKey<Item> tag) {
        int total = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(tag) && stack.getItem() instanceof UpgradeItem upgradeItem) {
                total += upgradeItem.getExtractAmount();
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
    protected void saveAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(compoundTag, provider);

        compoundTag.put("inventory", this.itemHandler.serializeNBT(provider));

        ContainerHelper.saveAllItems(compoundTag, this.filters, provider);
        FluidContainerHelper.saveAllFluids(compoundTag, this.fluidFilters, true, provider);

        if (ModList.get().isLoaded("mekanism")) {
            MekanismCompat.saveChemicalFilters(compoundTag, (NonNullList<ChemicalStack>) chemicalFilters, provider);
        }

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

        compoundTag.putString("dimension", dimension);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(compoundTag, provider);

        this.itemHandler.deserializeNBT(provider, compoundTag.getCompound("inventory"));

        ContainerHelper.loadAllItems(compoundTag, this.filters, provider);
        FluidContainerHelper.loadAllFluids(compoundTag, fluidFilters, provider);

        if (ModList.get().isLoaded("mekanism")) {
            MekanismCompat.loadChemicalFilters(compoundTag, (NonNullList<ChemicalStack>) chemicalFilters, provider);
        }

        importerPositions = new ArrayList<>();
        if (compoundTag.contains("ImporterPositions")) {
            ListTag listTag = compoundTag.getList("ImporterPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag posTag = listTag.getCompound(i);
                BlockPos pos = new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z"));
                importerPositions.add(pos);
            }
        }

        dimension = compoundTag.getString("dimension");
    }

    public List<BlockPos> getImporterPositions() {
        return importerPositions;
    }

    public void removeInvalidImporters() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        MinecraftServer server = serverLevel.getServer();
        boolean allowCrossDim = hasUpgrade(RoutersTags.Items.DIMENSIONAL_UPGRADES);

        importerPositions.removeIf(pos -> {
            if (allowCrossDim) {
                for (ServerLevel candidate : server.getAllLevels()) {
                    if (candidate.getBlockEntity(pos) instanceof ImporterBlockEntity importer &&
                            importer.getDimension().equals(candidate.dimension().location().toString())) {
                        return false;
                    }
                }
                return true;
            } else {
                return !(serverLevel.getBlockEntity(pos) instanceof ImporterBlockEntity importer) ||
                        !importer.getDimension().equals(serverLevel.dimension().location().toString());
            }
        });

        setChanged();
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }


}