package com.benbenlaw.routers.event;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.block.particle.RoutersParticles;
import com.benbenlaw.routers.item.RoutersItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Routers.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {

            if (!mc.player.getMainHandItem().is(RoutersItems.ROUTER_CONNECTOR.get()) &&
                    !mc.player.getOffhandItem().is(RoutersItems.ROUTER_CONNECTOR.get())) return;

            Level level = mc.level;
            BlockPos playerPos = mc.player.blockPosition();
            int radius = 10;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = playerPos.offset(x, y, z);
                        BlockEntity be = level.getBlockEntity(pos);
                        if (!(be instanceof ExporterBlockEntity exporter)) continue;

                        for (BlockPos targetPos : exporter.getImporterPositions()) {
                            Vec3 exporterPos = Vec3.atCenterOf(exporter.getBlockPos());
                            Vec3 importerPos = Vec3.atCenterOf(targetPos);

                            if (exporter.getBlockState().getBlock() instanceof ExporterBlock) {
                                Direction exporterFacing = exporter.getBlockState().getValue(ExporterBlock.FACING);
                                exporterPos = getAdjustedPos(exporterPos, exporterFacing);
                            } else {
                                return;
                            }
                            if ( level.getBlockState(targetPos).getBlock() instanceof ImporterBlock) {
                                Direction importerFacing = level.getBlockState(targetPos).getValue(ImporterBlock.FACING);
                                importerPos = getAdjustedPos(importerPos, importerFacing);
                            } else {
                                return;
                            }

                            // Spawn particle
                            long time = level.getGameTime();
                            double t = ((time % 20) / 20.0);
                            Vec3 particlePos = exporterPos.add(importerPos.subtract(exporterPos).scale(t));

                            level.addParticle(RoutersParticles.EXPORTER_PARTICLES.get(),
                                    particlePos.x, particlePos.y + 0.5, particlePos.z, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    private static Vec3 getAdjustedPos(Vec3 pos, Direction facing) {
        return switch (facing) {
            case DOWN -> pos.add(0, -0.7, 0);
            case UP -> pos.add(0, -0.3, 0);
            case NORTH -> pos.add(0, -0.5, -0.3);
            case SOUTH -> pos.add(0, -0.5, 0.3);
            case EAST -> pos.add(0.3, -0.5, 0);
            case WEST -> pos.add(-0.3, -0.5, 0);
        };
    }

}
