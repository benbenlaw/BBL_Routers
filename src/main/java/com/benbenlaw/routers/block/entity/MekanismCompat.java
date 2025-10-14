package com.benbenlaw.routers.block.entity;

import com.benbenlaw.routers.screen.util.GhostSlot;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class MekanismCompat {

    public static NonNullList<ChemicalStack> createChemicalFilters() {
        return NonNullList.withSize(18, ChemicalStack.EMPTY);
    }

    public static void saveChemicalFilters(CompoundTag tag, NonNullList<ChemicalStack> filters, HolderLookup.@NotNull Provider provider) {

        ListTag listTag = new ListTag();
        for (ChemicalStack stack : filters) {
            CompoundTag stackTag;
            if (!stack.isEmpty()) {
                stackTag = (CompoundTag) stack.saveOptional(provider);
            } else {
                stackTag = new CompoundTag();
            }
            listTag.add(stackTag);
        }
        tag.put("chemicalFilters", listTag);
    }

    public static void loadChemicalFilters(CompoundTag tag, NonNullList<ChemicalStack> filters, HolderLookup.@NotNull Provider provider) {
        for (int i = 0; i < filters.size(); i++) {
            filters.set(i, ChemicalStack.EMPTY);
        }

        if (tag.contains("chemicalFilters", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("chemicalFilters", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size() && i < filters.size(); i++) {
                CompoundTag stackTag = listTag.getCompound(i);
                filters.set(i, ChemicalStack.parseOptional(provider, stackTag));
            }
        }
    }

    public static final Object EMPTY_CHEMICAL = null;

    public static Object createChemicalStack() {
        return ChemicalStack.EMPTY;
    }

    public static boolean isEmpty(Object stack) {
        return stack == null || ((ChemicalStack) stack).isEmpty();
    }

    public static void setGhostChemical(GhostSlot slot, Object stack) {
        slot.ghostChemical = stack;
        slot.setChanged();
    }

    public static Object getGhostChemical(GhostSlot slot) {
        return slot.ghostChemical;
    }

}
