package com.benbenlaw.routers.integration;

import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class RoutersCapabilities {

    public static final BlockCapability<IChemicalHandler, Direction> CHEMICAL_HANDLER =  BlockCapability.createSided(
            Identifier.fromNamespaceAndPath("mekanism", "chemical_handler"), IChemicalHandler.class
    );
}
