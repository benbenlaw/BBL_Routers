package com.benbenlaw.routers;

import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.block.entity.RoutersBlockEntities;
import com.benbenlaw.routers.item.RoutersCreativeTab;
import com.benbenlaw.routers.item.RoutersDataComponents;
import com.benbenlaw.routers.item.RoutersItems;
import com.benbenlaw.routers.networking.RoutersNetworking;
import com.benbenlaw.routers.screen.ExporterScreen;
import com.benbenlaw.routers.screen.ImporterScreen;
import com.benbenlaw.routers.screen.RoutersMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Routers.MOD_ID)
public class Routers {
    public static final String MOD_ID = "routers";
    public static final Logger LOGGER = LogManager.getLogger();

    public Routers(final IEventBus eventBus, final ModContainer modContainer) {

        RoutersItems.ITEMS.register(eventBus);
        RoutersDataComponents.COMPONENTS.register(eventBus);
        RoutersBlocks.BLOCKS.register(eventBus);
        RoutersBlockEntities.BLOCK_ENTITIES.register(eventBus);
        RoutersMenuTypes.MENUS.register(eventBus);
        RoutersCreativeTab.CREATIVE_MODE_TABS.register(eventBus);

        eventBus.addListener(this::commonSetup);
    }

    public void commonSetup(RegisterPayloadHandlersEvent event) {
        RoutersNetworking.registerNetworking(event);

    }

    @EventBusSubscriber(modid = Routers.MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(RoutersMenuTypes.EXPORTER_MENU.get(), ExporterScreen::new);
            event.register(RoutersMenuTypes.IMPORTER_MENU.get(), ImporterScreen::new);

        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            //event.registerBlockEntityRenderer(RoutersBlockEntities.EXPORTER_BLOCK_ENTITY.get(), ExporterBlockEntityRenderer::new);
        }
    }
}
