package com.benbenlaw.routers.mixin;

import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import me.desht.pneumaticcraft.common.block.entity.AbstractAirHandlingBlockEntity;
import me.desht.pneumaticcraft.common.block.entity.processing.PressureChamberValveBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Pseudo
@Mixin(PressureChamberValveBlockEntity.class)
public abstract class MixinPressureChamberValve {

    @Inject(method = "checkForAirLeak", at = @At("HEAD"), cancellable = true)
    private void preventLeakIfRouterNearby(CallbackInfo ci) throws Exception {
        PressureChamberValveBlockEntity self = (PressureChamberValveBlockEntity) (Object) this;
        if (self.getLevel() == null) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = self.getBlockPos().relative(dir);
            BlockEntity neighbor = self.getLevel().getBlockEntity(neighborPos);
            if (neighbor instanceof ImporterBlockEntity || neighbor instanceof ExporterBlockEntity) {
                // Access protected field via reflection
                Field airHandlerField = AbstractAirHandlingBlockEntity.class.getDeclaredField("airHandler");
                airHandlerField.setAccessible(true);
                IAirHandlerMachine handler = (IAirHandlerMachine) airHandlerField.get(self);

                // Prevent leak
                handler.setSideLeaking(null);
                ci.cancel();
                return;
            }
        }
    }
}
