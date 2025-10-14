package com.benbenlaw.routers.integration;

import com.benbenlaw.routers.networking.packets.JEISyncToMenu;
import com.benbenlaw.routers.networking.packets.JEISyncToMenuChemical;
import com.benbenlaw.routers.networking.packets.JEISyncToMenuFluid;
import com.benbenlaw.routers.screen.ExporterScreen;
import com.benbenlaw.routers.screen.ImporterScreen;
import com.benbenlaw.routers.screen.util.GhostSlot;
import mekanism.api.chemical.ChemicalStack;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GhostIngredientImporterHandler implements IGhostIngredientHandler<ImporterScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(ImporterScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        for (int i = 0; i < screen.getMenu().slots.size(); i++) {
            var slot = screen.getMenu().slots.get(i);

            if (slot instanceof GhostSlot ghostSlot) {
                int finalI = i;
                targets.add(new Target<>() {
                    @Override
                    public Rect2i getArea() {
                        return new Rect2i(
                                screen.getGuiLeft() + slot.x,
                                screen.getGuiTop() + slot.y,
                                16, 16
                        );
                    }

                    @Override
                    public void accept(I ingredientObj) {
                        // only handle items for now
                        if (ingredientObj instanceof ItemStack stack) {
                            PacketDistributor.sendToServer(new JEISyncToMenu(finalI, stack));
                            ghostSlot.set(stack.copyWithCount(1));
                        }

                        if (ingredientObj instanceof FluidStack stack) {
                            PacketDistributor.sendToServer(new JEISyncToMenuFluid(finalI, stack));
                            ghostSlot.setFluid(stack);
                        }

                        if (ModList.get().isLoaded("mekanism")) {
                            if (ingredientObj instanceof ChemicalStack stack) {
                                PacketDistributor.sendToServer(new JEISyncToMenuChemical(finalI, stack));
                                ghostSlot.setChemical(stack);
                            }
                        }
                    }
                });
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {
        // nothing special needed here
    }
}
