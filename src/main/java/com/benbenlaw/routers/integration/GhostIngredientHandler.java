package com.benbenlaw.routers.integration;

import com.benbenlaw.routers.networking.packets.JEISyncToMenu;
import com.benbenlaw.routers.screen.ExporterScreen;
import com.benbenlaw.routers.screen.util.GhostSlot;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler implements IGhostIngredientHandler<ExporterScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(ExporterScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
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
