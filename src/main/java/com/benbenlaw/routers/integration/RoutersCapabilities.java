package com.benbenlaw.routers.integration;

import com.benbenlaw.routers.Routers;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

public class RoutersCapabilities {


    public static final BlockCapability<IChemicalHandler, Direction> CHEMICAL_HANDLER =  BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical_handler"), IChemicalHandler.class
    );
}
