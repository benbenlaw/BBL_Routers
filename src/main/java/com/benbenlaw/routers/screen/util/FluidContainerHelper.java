package com.benbenlaw.routers.screen.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidContainerHelper {

    public static void saveAllFluids(CompoundTag compoundTag, NonNullList<FluidStack> fluids, boolean saveEmpty, HolderLookup.Provider provider) {
        ListTag listtag = new ListTag();

        for (int i = 0; i < fluids.size(); i++) {
            FluidStack fluidStack = fluids.get(i);
            if (!fluidStack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                listtag.add(fluidStack.save(provider, compoundtag));
            }
        }

        if (!listtag.isEmpty() || saveEmpty) {
            compoundTag.put("Fluids", listtag);
        }
    }

    public static void loadAllFluids(CompoundTag compoundTag, NonNullList<FluidStack> fluids, HolderLookup.Provider provider) {
        ListTag listtag = compoundTag.getList("Fluids", 10);

        for (int i = 0; i < listtag.size(); i++) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j < fluids.size()) {
                fluids.set(j, FluidStack.parse(provider, compoundtag).orElse(FluidStack.EMPTY));
            }
        }
    }
}
