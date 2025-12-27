package com.benbenlaw.routers.block;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RoutersBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Routers.MOD_ID);

    public static final Supplier<BlockEntityType<ImporterBlockEntity>> IMPORTER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("importer_block_entity", () ->
                    new BlockEntityType<>(ImporterBlockEntity::new, RoutersBlocks.IMPORTER.get()));

    public static final Supplier<BlockEntityType<ExporterBlockEntity>> EXPORTER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("exporter_block_entity", () ->
                    new BlockEntityType<>(ExporterBlockEntity::new, RoutersBlocks.EXPORTER.get()));

}
