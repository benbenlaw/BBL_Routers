package com.benbenlaw.routers.networking;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.networking.packets.FilterItemUpdate;
import com.benbenlaw.routers.networking.packets.JEISyncToMenu;
import com.benbenlaw.routers.networking.packets.JEISyncToMenuFluid;
import com.benbenlaw.routers.networking.packets.SyncFluidListToClient;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RoutersNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Routers.MOD_ID);

        registrar.playToServer(JEISyncToMenu.TYPE, JEISyncToMenu.STREAM_CODEC, JEISyncToMenu.HANDLER);
        registrar.playToServer(JEISyncToMenuFluid.TYPE, JEISyncToMenuFluid.STREAM_CODEC, JEISyncToMenuFluid.HANDLER);
        registrar.playToServer(FilterItemUpdate.TYPE, FilterItemUpdate.STREAM_CODEC, FilterItemUpdate.HANDLER);

        registrar.playToClient(SyncFluidListToClient.TYPE, SyncFluidListToClient.STREAM_CODEC, SyncFluidListToClient.HANDLER);
    }
}
