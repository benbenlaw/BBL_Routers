package com.benbenlaw.routers.item;

import com.benbenlaw.routers.Routers;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RoutersDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Routers.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> EXPORTER_POSITION =
            COMPONENTS.register("exporter_position", () ->
                    DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> IMPORTER_POSITION =
            COMPONENTS.register("importer_position", () ->
                    DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Identifier>> TAG_FILTER =
            COMPONENTS.register("tag_filter", () ->
                    DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MOD_FILTER =
            COMPONENTS.register("mod_filter", () ->
                    DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());



}
