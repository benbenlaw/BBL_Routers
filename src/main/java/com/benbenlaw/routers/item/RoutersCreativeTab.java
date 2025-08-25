package com.benbenlaw.routers.item;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.RoutersBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RoutersCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Routers.MOD_ID);

    public static final Supplier<CreativeModeTab> CASTING_TAB = CREATIVE_MODE_TABS.register("routers", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RoutersItems.ROUTER_CONNECTOR.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.routers"))
            .displayItems((parameters, output) -> {

                output.accept(RoutersBlocks.EXPORTER_BLOCK.get().asItem());
                output.accept(RoutersBlocks.IMPORTER_BLOCK.get().asItem());
                output.accept(RoutersItems.ROUTER_CONNECTOR.get().asItem());


            }).build());
}
