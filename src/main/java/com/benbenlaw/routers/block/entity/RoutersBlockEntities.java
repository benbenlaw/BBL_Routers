package com.benbenlaw.routers.block.entity;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.RoutersBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class RoutersBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Routers.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ImporterBlockEntity>> IMPORTER_BLOCK_ENTITY =
            register("importer_block_entity", () ->
                    BlockEntityType.Builder.of(ImporterBlockEntity::new, RoutersBlocks.IMPORTER_BLOCK.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExporterBlockEntity>> EXPORTER_BLOCK_ENTITY =
            register("exporter_block_entity", () ->
                    BlockEntityType.Builder.of(ExporterBlockEntity::new, RoutersBlocks.EXPORTER_BLOCK.get()));


    public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(@Nonnull String name, @Nonnull Supplier<BlockEntityType.Builder<T>> initializer) {
        return BLOCK_ENTITIES.register(name, () -> initializer.get().build(null));
    }

}
