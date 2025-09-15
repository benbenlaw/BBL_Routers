package com.benbenlaw.routers.block.entity.client;

import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.particle.RoutersParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExporterBlockEntityRenderer implements BlockEntityRenderer<ExporterBlockEntity> {
    public ExporterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ExporterBlockEntity exporterBlockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int combinedOverlayIn) {
        Level level = Minecraft.getInstance().level;

        List<BlockPos> importerPositions = exporterBlockEntity.getImporterPositions();
        Vec3 baseExporterPos = Vec3.atCenterOf(exporterBlockEntity.getBlockPos());
        assert level != null;

        for (BlockPos targetPos : importerPositions) {
            Vec3 exporterPos = baseExporterPos; // copy the base position

            Vec3 importerPos = Vec3.atCenterOf(targetPos);

            // Adjust exporter spawn position based on facing
            if (level.getBlockState(exporterBlockEntity.getBlockPos()).getBlock() instanceof ExporterBlock) {
                Direction direction = level.getBlockState(exporterBlockEntity.getBlockPos()).getValue(ExporterBlock.FACING);
                exporterPos = getAdjustedPos(exporterPos, direction);
            }

            // Adjust importer target position based on facing
            if (level.getBlockState(targetPos).getBlock() instanceof ImporterBlock) {
                Direction facing = level.getBlockState(targetPos).getValue(ImporterBlock.FACING);
                importerPos = getAdjustedPos(importerPos, facing);
            }

            // Compute particle position
            long time = System.currentTimeMillis();
            double t = ((time % 1000) / 1000.0);
            Vec3 particlePos = exporterPos.add(importerPos.subtract(exporterPos).scale(t));

            level.addParticle(RoutersParticles.EXPORTER_PARTICLES.get(),
                    particlePos.x, particlePos.y + 0.5, particlePos.z, 0, 0, 0);
        }
    }

    // Helper method to adjust position based on block facing
    private Vec3 getAdjustedPos(Vec3 pos, Direction facing) {
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
