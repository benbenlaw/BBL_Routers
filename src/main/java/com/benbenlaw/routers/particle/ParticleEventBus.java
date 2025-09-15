package com.benbenlaw.routers.particle;

import com.benbenlaw.routers.particle.custom.ExporterParticle;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ParticleEventBus {

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(RoutersParticles.EXPORTER_PARTICLES.get(),
                ExporterParticle.Provider::new);

    }
}
