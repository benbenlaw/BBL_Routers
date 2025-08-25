package com.benbenlaw.routers.networking;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.networking.packets.JEISyncToMenu;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class RoutersNetworking {

    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Routers.MOD_ID);

        registrar.playToServer(JEISyncToMenu.TYPE, JEISyncToMenu.STREAM_CODEC, JEISyncToMenu.HANDLER);
    }
}
