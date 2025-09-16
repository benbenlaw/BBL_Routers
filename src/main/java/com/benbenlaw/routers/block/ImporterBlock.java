package com.benbenlaw.routers.block;

import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import com.benbenlaw.routers.block.entity.RoutersBlockEntities;
import com.benbenlaw.routers.item.RoutersDataComponents;
import com.benbenlaw.routers.item.RoutersItems;
import com.benbenlaw.routers.networking.packets.SyncFluidListToClient;
import com.benbenlaw.routers.screen.ImporterMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ImporterBlock extends BaseEntityBlock implements SimpleWaterloggedBlock  {

    public static final MapCodec<ImporterBlock> CODEC = simpleCodec(ImporterBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Map<Direction, VoxelShape> SHAPE_MAP = new EnumMap<>(Direction.class);

    static {
        SHAPE_MAP.put(Direction.EAST, buildShape(Direction.WEST));
        SHAPE_MAP.put(Direction.WEST, buildShape(Direction.EAST));

        SHAPE_MAP.put(Direction.NORTH, buildShape(Direction.NORTH));
        SHAPE_MAP.put(Direction.SOUTH, buildShape(Direction.SOUTH));

        SHAPE_MAP.put(Direction.UP, buildShape(Direction.DOWN));
        SHAPE_MAP.put(Direction.DOWN, buildShape(Direction.UP));
    }

    private static VoxelShape buildShape(Direction direction) {
        VoxelShape shape = Shapes.empty();

        // Manually add each element from the Blockbench model
        // Convert Blockbench's [from, to] to Minecraft's [0.0, 1.0]
        shape = Shapes.or(shape, Shapes.box(5/16.0, 5/16.0, 13.5/16.0, 11/16.0, 11/16.0, 14.5/16.0));
        shape = Shapes.or(shape, Shapes.box(5/16.0, 5/16.0, 13/16.0, 11/16.0, 11/16.0, 15/16.0));
        shape = Shapes.or(shape, Shapes.box(4/16.0, 4/16.0, 15/16.0, 12/16.0, 12/16.0, 1.0));
        shape = Shapes.or(shape, Shapes.box(9/16.0, 12/16.0, 15.99/16.0, 10/16.0, 13/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(6/16.0, 12/16.0, 15.99/16.0, 7/16.0, 13/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(6/16.0, 3/16.0, 15.99/16.0, 7/16.0, 4/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(9/16.0, 3/16.0, 15.99/16.0, 10/16.0, 4/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(3/16.0, 9/16.0, 15.99/16.0, 4/16.0, 10/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(3/16.0, 6/16.0, 15.99/16.0, 4/16.0, 7/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(12/16.0, 9/16.0, 15.99/16.0, 13/16.0, 10/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(12/16.0, 6/16.0, 15.99/16.0, 13/16.0, 7/16.0, 15.99/16.0));
        shape = Shapes.or(shape, Shapes.box(7/16.0, 7/16.0, 12/16.0, 9/16.0, 9/16.0, 13/16.0));
        shape = Shapes.or(shape, Shapes.box(5/16.0, 8/16.0, 10/16.0, 11/16.0, 8/16.0, 12/16.0));
        shape = Shapes.or(shape, Shapes.box(8/16.0, 5/16.0, 10/16.0, 8/16.0, 11/16.0, 12/16.0));

        // Rotate shape according to direction
        return rotateShape(direction, shape);
    }

    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        if (Direction.SOUTH == to) return shape;

        VoxelShape rotated = Shapes.empty();

        for (AABB box : shape.toAabbs()) {
            double minX = box.minX;
            double minY = box.minY;
            double minZ = box.minZ;
            double maxX = box.maxX;
            double maxY = box.maxY;
            double maxZ = box.maxZ;

            AABB newBox;

            switch (to) {
                case NORTH -> newBox = new AABB(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ);
                case EAST -> newBox = new AABB(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
                case WEST -> newBox = new AABB(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);

                case UP -> newBox = new AABB(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY); // Z -> Y
                case DOWN -> newBox = new AABB(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY); // Y -> Z

                default -> newBox = box; // SOUTH
            }

            rotated = Shapes.or(rotated, Shapes.box(
                    newBox.minX, newBox.minY, newBox.minZ,
                    newBox.maxX, newBox.maxY, newBox.maxZ
            ));
        }

        return rotated;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public ImporterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_MAP.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {

        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;


        if (stack.is(RoutersItems.ROUTER_CONNECTOR)) {

            if (state.getBlock() instanceof ImporterBlock) {

                BlockPos savedPos = stack.get(RoutersDataComponents.IMPORTER_POSITION);
                if (savedPos != null && savedPos.equals(blockPos)) {
                    player.displayClientMessage(Component.translatable("message.routers.importer.removed_pos"), true);
                    stack.remove(RoutersDataComponents.IMPORTER_POSITION);
                } else {
                    stack.set(RoutersDataComponents.IMPORTER_POSITION, blockPos);
                    player.displayClientMessage(Component.translatable("message.routers.importer.added_pos"), true);
                }

            } else {
                player.sendSystemMessage(Component.literal("Use on an Importer Block to set position"));
            }
        } else {
            ImporterBlockEntity importer = (ImporterBlockEntity) level.getBlockEntity(blockPos);

            if (importer instanceof ImporterBlockEntity) {
                ContainerData data = importer.data;
                player.openMenu(new SimpleMenuProvider(
                        (windowId, playerInventory, playerEntity) -> new ImporterMenu(windowId, playerInventory, blockPos, data),
                        Component.translatable("block.routers.importer_block")), (buf -> buf.writeBlockPos(blockPos)));

                PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFluidListToClient(blockPos, importer.getFluidFilters()));
            }
        }

        return ItemInteractionResult.SUCCESS;

    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    /* FACING */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean water = level.getFluidState(pos).getType() == Fluids.WATER;

        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace().getOpposite())
                .setValue(WATERLOGGED, water);
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING))).setValue(WATERLOGGED, pState.getValue(WATERLOGGED));
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING))).setValue(WATERLOGGED, pState.getValue(WATERLOGGED));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WATERLOGGED);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ImporterBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.routers.importer").withStyle(ChatFormatting.YELLOW));
        } else {
            components.add(Component.translatable("tooltip.routers.hold_shift").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RoutersBlockEntities.IMPORTER_BLOCK_ENTITY.get(),
                (world, blockPos, thisBlockState, blockEntity) -> blockEntity.tick());
    }
}
