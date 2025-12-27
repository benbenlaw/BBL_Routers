package com.benbenlaw.routers.block.entity.client;

/*
public class ExporterBlockEntityRenderer implements BlockEntityRenderer<ExporterBlockEntity> {

    public ExporterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

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
                            Identifier.withDefaultNamespace("textures/entity/beacon_beam.png"),
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
            addBeamVertex(consumer, pose, beamRadius, 0f, -beamRadius, r, g, b, a, 1f, vOffset, light, overlay);
            addBeamVertex(consumer, pose, beamRadius, (float) length, -beamRadius, r, g, b, a, 1f, vOffset + (float) length, light, overlay);
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
        if (state.hasProperty(ExporterBlock.FACING))
            facing = state.getValue(ExporterBlock.FACING);
        else if (state.hasProperty(ImporterBlock.FACING))
            facing = state.getValue(ImporterBlock.FACING);
        else
            facing = Direction.UP;

        VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
        AABB bounds = shape.isEmpty() ? new AABB(0,0,0,1,1,1) : shape.bounds();

        // Center of block (used for X/Z or fallback)
        double cx = (bounds.minX + bounds.maxX) * 0.5;
        double cy = (bounds.minY + bounds.maxY) * 0.5;
        double cz = (bounds.minZ + bounds.maxZ) * 0.5;

        final double EPS = 0.001;
        final double INSET = 0.2;  // how far inward to move

        switch (facing) {
            case UP -> {
                cy = bounds.maxY - INSET;
            }
            case DOWN -> {
                cy = bounds.minY + INSET;
            }
            case NORTH -> {
                cz = bounds.minZ + INSET;
            }
            case SOUTH -> {
                cz = bounds.maxZ - INSET;
            }
            case WEST -> {
                cx = bounds.minX + INSET;
            }
            case EAST -> {
                cx = bounds.maxX - INSET;
            }
        }

        return new Vec3(
                pos.getX() + cx,
                pos.getY() + cy,
                pos.getZ() + cz
        );
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
        return new float[]{
                ((color >> 16) & 0xFF) / 255f,
                ((color >> 8) & 0xFF) / 255f,
                (color & 0xFF) / 255f
        };
    }

}

 */