package com.benbenlaw.routers.event;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.block.entity.ExporterBlockEntity;
import com.benbenlaw.routers.networking.packets.SyncFluidListToClient;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Routers.MOD_ID)
public class ServerEvents {

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockEntity entity = level.getBlockEntity(event.getPos());

        if (level.isClientSide) return;

        if (entity instanceof ExporterBlockEntity exporter) {
            PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SyncFluidListToClient(event.getPos(), exporter.getFluidFilters()));
        }
    }

}
