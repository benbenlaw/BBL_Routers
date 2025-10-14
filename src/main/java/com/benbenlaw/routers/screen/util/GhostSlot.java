package com.benbenlaw.routers.screen.util;

import com.benbenlaw.routers.block.entity.MekanismCompat;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class GhostSlot extends Slot {


    public GhostSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    private ItemStack ghostItem = ItemStack.EMPTY;
    private FluidStack ghostFluid = FluidStack.EMPTY;
    public Object ghostChemical = null;

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }


    public void setItem(ItemStack stack) {
        this.ghostItem = stack;
        setChanged();
    }

    public void setFluid(FluidStack stack) {
        this.ghostFluid = stack;
        setChanged();
    }

    public void setChemical(Object stack) {
        ghostChemical = stack;
        setChanged();
    }

    public ItemStack getGhostItem() { return ghostItem; }
    public FluidStack getGhostFluid() { return ghostFluid; }
    public Object getGhostChemical() { return ghostChemical; }

    public boolean hasItem() { return !ghostItem.isEmpty(); }
    public boolean hasFluid() { return !ghostFluid.isEmpty(); }
    public boolean hasChemical() { return ghostChemical != null && !MekanismCompat.isEmpty(ghostChemical); }

}
