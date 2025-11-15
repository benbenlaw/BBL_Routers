package com.benbenlaw.routers.mixin;

import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import me.desht.pneumaticcraft.common.block.entity.AbstractAirHandlingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(AbstractAirHandlingBlockEntity.class)
public abstract class MixinAbstractAirHandlingBlockEntity {

    @Inject(method = "hasNoConnectedAirHandlers", at = @At("HEAD"), cancellable = true)
    private void preventLeakForRouters(CallbackInfoReturnable<Boolean> cir) {
        AbstractAirHandlingBlockEntity self = (AbstractAirHandlingBlockEntity) (Object) this;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = self.getBlockPos().relative(dir);
            if (self.getLevel() != null) {
                BlockEntity neighbor = self.getLevel().getBlockEntity(neighborPos);
                if (neighbor instanceof ImporterBlockEntity || neighbor instanceof ExporterBlockEntity) {
                    // Treat router block as “connected”
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
