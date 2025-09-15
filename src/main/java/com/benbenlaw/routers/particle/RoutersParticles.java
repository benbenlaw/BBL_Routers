package com.benbenlaw.routers.particle;

import com.benbenlaw.routers.Routers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RoutersParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Routers.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXPORTER_PARTICLES =
            PARTICLE_TYPES.register("exporter_particles", () -> new SimpleParticleType(true));
}
