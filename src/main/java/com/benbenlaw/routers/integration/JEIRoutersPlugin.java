package com.benbenlaw.routers.integration;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.screen.ExporterScreen;
import com.benbenlaw.routers.screen.ImporterMenu;
import com.benbenlaw.routers.screen.ImporterScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIRoutersPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "jei_plugin");
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(ExporterScreen.class, new GhostIngredientExporterHandler());
        registration.addGhostIngredientHandler(ImporterScreen.class, new GhostIngredientImporterHandler());

    }

}
