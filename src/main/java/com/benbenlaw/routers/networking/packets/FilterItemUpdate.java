package com.benbenlaw.routers.networking.packets;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.screen.ExporterMenu;
import com.benbenlaw.routers.screen.ImporterMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record FilterItemUpdate(ItemStack stack) implements CustomPacketPayload {

    public static final Type<FilterItemUpdate> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "filter_item_update"));

    public static final IPayloadHandler<FilterItemUpdate> HANDLER = (packet, context) -> {

        ServerPlayer player = (ServerPlayer) context.player();
        player.setItemInHand(InteractionHand.MAIN_HAND, packet.stack());
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, FilterItemUpdate> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, FilterItemUpdate::stack,
            FilterItemUpdate::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
