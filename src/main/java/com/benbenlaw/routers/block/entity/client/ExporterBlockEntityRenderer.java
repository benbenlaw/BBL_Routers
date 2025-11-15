package com.benbenlaw.routers.block.entity.client;

import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class ExporterBlockEntityRenderer implements BlockEntityRenderer<ExporterBlockEntity> {

    public ExporterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ExporterBlockEntity exporterBlockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        List<BlockPos> importerPositions = exporterBlockEntity.getImporterPositions();
        Vec3 exporterPos = getBeamStart(exporterBlockEntity.getBlockPos(), level);

        for (BlockPos importerBlockPos : importerPositions) {
            Vec3 importerPos = getBeamStart(importerBlockPos, level);

            Vec3 delta = importerPos.subtract(exporterPos);
            double length = delta.length();
            Vec3 dir = delta.normalize();

            renderSingleBeam(poseStack, bufferSource, exporterPos, dir, length, packedLight, packedOverlay);
        }
    }

    private void renderSingleBeam(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 startPos,
                                  Vec3 dir, double length, int light, int overlay) {

        float beamRadius = 0.05f;
        float time = (System.currentTimeMillis() % 10000L) / 10000.0f;
        float vOffset = -(time * 2f);
        float r = 0f, g = 1f, b = 1f, a = 1f;

        poseStack.pushPose();

        // Translate to block-local coordinates
        poseStack.translate(
                startPos.x - Math.floor(startPos.x),
                startPos.y - Math.floor(startPos.y),
                startPos.z - Math.floor(startPos.z)
        );

        // Align local +Y with the beam direction
        Vector3f from = new Vector3f(0, 1, 0);
        Vector3f to = new Vector3f((float) dir.x, (float) dir.y, (float) dir.z);
        Quaternionf rotation = new Quaternionf().rotationTo(from, to);
        poseStack.mulPose(rotation);

        VertexConsumer consumer = bufferSource.getBuffer(NO_CULL_BEAM);

        // Render 4 quads around local +Y
        for (int i = 0; i < 4; i++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(i * 90f));
            PoseStack.Pose pose = poseStack.last();

            addBeamVertex(consumer, pose, -beamRadius, 0f, -beamRadius, r, g, b, a, 0f, vOffset, light, overlay);
            addBeamVertex(consumer, pose,  beamRadius, 0f, -beamRadius, r, g, b, a, 1f, vOffset, light, overlay);
            addBeamVertex(consumer, pose,  beamRadius, (float) length, -beamRadius, r, g, b, a, 1f, vOffset + (float) length, light, overlay);
            addBeamVertex(consumer, pose, -beamRadius, (float) length, -beamRadius, r, g, b, a, 0f, vOffset + (float) length, light, overlay);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static void addBeamVertex(VertexConsumer consumer, PoseStack.Pose pose,
                                      float x, float y, float z,
                                      float r, float g, float b, float a,
                                      float u, float v,
                                      int light, int overlay) {
        consumer
                .addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, 0.0f, 1.0f, 0.0f);
    }

    private static final RenderType NO_CULL_BEAM = RenderType.create(
            "no_cull_beam",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_BEACON_BEAM_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png"),
                            false,
                            false
                    ))
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderType.NO_CULL)
                    .setLightmapState(RenderType.LIGHTMAP)
                    .setOverlayState(RenderType.NO_OVERLAY)
                    .createCompositeState(false)
    );

    private Vec3 getBeamStart(BlockPos pos, Level level) {
        var state = level.getBlockState(pos);
        Direction facing;
        if (state.hasProperty(ExporterBlock.FACING)) facing = state.getValue(ExporterBlock.FACING);
        else if (state.hasProperty(ImporterBlock.FACING)) facing = state.getValue(ImporterBlock.FACING);
        else facing = Direction.UP;

        VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());

        double x = 0.5, y = 0.0, z = 0.5;

        for (AABB box : shape.toAabbs()) {
            switch (facing) {
                case UP -> { x = (box.minX + box.maxX) / 2; y = box.maxY; z = (box.minZ + box.maxZ) / 2; }
                case DOWN -> { x = (box.minX + box.maxX) / 2; y = box.minY; z = (box.minZ + box.maxZ) / 2; }
                case NORTH -> { x = (box.minX + box.maxX) / 2; y = (box.minY + box.maxY) / 2; z = box.minZ; }
                case SOUTH -> { x = (box.minX + box.maxX) / 2; y = (box.minY + box.maxY) / 2; z = box.maxZ; }
                case WEST -> { x = box.minX; y = (box.minY + box.maxY) / 2; z = (box.minZ + box.maxZ) / 2; }
                case EAST -> { x = box.maxX; y = (box.minY + box.maxY) / 2; z = (box.minZ + box.maxZ) / 2; }
            }
        }

        // fallback for tiny router shape
        if (y <= 0) y = 3.0 / 16.0;

        return new Vec3(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
}