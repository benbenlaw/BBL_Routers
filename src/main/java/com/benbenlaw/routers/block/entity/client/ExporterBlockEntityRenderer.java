package com.benbenlaw.routers.block.entity.client;

import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.util.RoutersTags;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class ExporterBlockEntityRenderer implements BlockEntityRenderer<ExporterBlockEntity> {

    public ExporterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRender(ExporterBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(ExporterBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public AABB getRenderBoundingBox(ExporterBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(32);
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

    @Override
    public void render(ExporterBlockEntity exporter, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(Tags.Items.TOOLS_WRENCH)) return;

        Level level = exporter.getLevel();
        if (level == null) return;

        List<BlockPos> importerPositions = exporter.getImporterPositions();

        // World-space beam start
        Vec3 exporterPos = getBeamStart(exporter.getBlockPos(), level);

        // Block origin (used to convert into block-local space)
        Vec3 exporterBlockOrigin = new Vec3(
                exporter.getBlockPos().getX(),
                exporter.getBlockPos().getY(),
                exporter.getBlockPos().getZ()
        );

        ItemStackHandler handler = exporter.getItemStackHandler();

        // Collect unique beam colors
        Set<String> uniqueKeys = new HashSet<>();
        List<float[]> uniqueColors = new ArrayList<>();

        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            float[] tint = getBeamTint(stack);
            if (tint[0] == 0f && tint[1] == 0f && tint[2] == 0f) continue;

            String key = tint[0] + "," + tint[1] + "," + tint[2];
            if (!uniqueKeys.contains(key)) {
                uniqueKeys.add(key);
                uniqueColors.add(tint);
            }
        }

        if (uniqueColors.isEmpty()) return;

        // Render beams to each importer
        for (BlockPos importerPosRaw : importerPositions) {

            Vec3 importerPos = getBeamStart(importerPosRaw, level);
            Vec3 delta = importerPos.subtract(exporterPos);
            double length = delta.length();
            if (length < 0.001) continue;

            Vec3 dir = delta.normalize();

            // Build perpendicular basis
            Vec3 up = new Vec3(0, 1, 0);
            Vec3 perp = dir.cross(up);
            if (perp.lengthSqr() < 1e-6) {
                perp = dir.cross(new Vec3(1, 0, 0));
            }
            perp = perp.normalize();
            Vec3 side = dir.cross(perp).normalize();

            int count = uniqueColors.size();
            double radius = 0.03;

            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;

                Vec3 offset = perp.scale(Math.cos(angle) * radius)
                        .add(side.scale(Math.sin(angle) * radius));

                Vec3 startPosWorld = exporterPos.add(offset);

                Vec3 startPosLocal = startPosWorld.subtract(exporterBlockOrigin);

                float[] tint = uniqueColors.get(i);

                renderSingleBeam(
                        poseStack,
                        bufferSource,
                        startPosLocal,   // ✅ LOCAL coords now
                        dir,
                        length,
                        packedLight,
                        packedOverlay,
                        tint[0], tint[1], tint[2]
                );
            }
        }

    }

    private void renderSingleBeam(PoseStack poseStack, MultiBufferSource bufferSource,
                                  Vec3 startPosLocal, Vec3 dir, double length, int light, int overlay,
                                  float r, float g, float b) {
        float a = 1f;
        float beamRadius = 0.0075f;
        float time = (System.currentTimeMillis() % 10000L) / 10000.0f;
        float vOffset = -(time * 2f);

        poseStack.pushPose();
        poseStack.translate((float) startPosLocal.x, (float) startPosLocal.y, (float) startPosLocal.z);

        Vector3f from = new Vector3f(0, 1, 0);
        Vector3f to = new Vector3f((float) dir.x, (float) dir.y, (float) dir.z);
        Quaternionf rotation = new Quaternionf().rotationTo(from, to);
        poseStack.mulPose(rotation);

        VertexConsumer consumer = bufferSource.getBuffer(NO_CULL_BEAM);
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
                .setNormal(pose, 0f, 1f, 0f);
    }

    private Vec3 getBeamStart(BlockPos pos, Level level) {
        var state = level.getBlockState(pos);
        Direction facing;
        if (state.hasProperty(ExporterBlock.FACING)) facing = state.getValue(ExporterBlock.FACING);
        else if (state.hasProperty(ImporterBlock.FACING)) facing = state.getValue(ImporterBlock.FACING);
        else facing = Direction.UP;

        VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
        double x = 0.5, y = 0.5, z = 0.5; // default to center
        final double EPS = 0.001; // small inset to avoid integer positions

        if (shape.isEmpty()) {
            return new Vec3(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
        }

        for (AABB box : shape.toAabbs()) {
            switch (facing) {
                case UP ->  { x = (box.minX + box.maxX) / 2.0; y = box.maxY - EPS; z = (box.minZ + box.maxZ) / 2.0; }
                case DOWN ->{ x = (box.minX + box.maxX) / 2.0; y = box.minY + EPS; z = (box.minZ + box.maxZ) / 2.0; }
                case NORTH ->{ x = (box.minX + box.maxX) / 2.0; y = (box.minY + box.maxY) / 2.0; z = box.minZ + EPS;   }
                case SOUTH ->{ x = (box.minX + box.maxX) / 2.0; y = (box.minY + box.maxY) / 2.0; z = box.maxZ - EPS;   }
                case WEST -> { x = box.minX + EPS; y = (box.minY + box.maxY) / 2.0; z = (box.minZ + box.maxZ) / 2.0; }
                case EAST -> { x = box.maxX - EPS; y = (box.minY + box.maxY) / 2.0; z = (box.minZ + box.maxZ) / 2.0; }
            }
        }

        return new Vec3(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }


    private float[] getBeamTint(ItemStack stack) {
        if (stack.isEmpty()) return new float[]{0f, 0f, 0f};

        if (stack.is(RoutersTags.Items.RF_UPGRADES)) return hex("B70000");
        if (stack.is(RoutersTags.Items.ITEM_UPGRADES)) return hex("818181");
        if (stack.is(RoutersTags.Items.FLUID_UPGRADES)) return hex("0A61B8");
        if (stack.is(RoutersTags.Items.CHEMICAL_UPGRADES)) return hex("BC54F9");
        if (stack.is(RoutersTags.Items.SOUL_UPGRADES)) return hex("5EF5FC");
        if (stack.is(RoutersTags.Items.SOURCE_UPGRADES)) return hex("B80AB1");
        if (stack.is(RoutersTags.Items.PRESSURE_UPGRADES)) return hex("57C7B6");

        return new float[]{0f, 0f, 0f};
    }

    private float[] hex(String hex) {
        int color = Integer.parseInt(hex, 16);
        return new float[] {
                ((color >> 16) & 0xFF) / 255f,
                ((color >> 8) & 0xFF) / 255f,
                (color & 0xFF) / 255f
        };
    }

}
