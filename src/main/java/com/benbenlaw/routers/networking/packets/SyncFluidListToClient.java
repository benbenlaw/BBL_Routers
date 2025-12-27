package com.benbenlaw.routers.networking.packets;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.screen.ExporterMenu;
import com.benbenlaw.routers.screen.ImporterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record SyncFluidListToClient(BlockPos pos, List<FluidStack> fluidFilters) implements CustomPacketPayload {

    public static final Type<SyncFluidListToClient> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Routers.MOD_ID, "sync_fluid_list_to_client"));

    public static final IPayloadHandler<SyncFluidListToClient> HANDLER = (packet, context) -> {

        if (context.player().containerMenu instanceof ExporterMenu menu) {
            menu.updateFluids(packet.fluidFilters());
        }
        if (context.player().containerMenu instanceof ImporterMenu menu) {
            menu.updateFluids(packet.fluidFilters());
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFluidListToClient> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncFluidListToClient::pos,
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SyncFluidListToClient::fluidFilters,
            SyncFluidListToClient::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
