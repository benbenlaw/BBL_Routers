package com.benbenlaw.routers.networking.packets;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.screen.ExporterMenu;
import com.benbenlaw.routers.screen.ImporterMenu;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record SyncChemicalListToClient(BlockPos pos, List<ChemicalStack> chemicalFilters) implements CustomPacketPayload {

    public static final Type<SyncChemicalListToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "sync_chemical_list_to_client"));

    public static final IPayloadHandler<SyncChemicalListToClient> HANDLER = (packet, context) -> {

        if (context.player().containerMenu instanceof ExporterMenu menu) {
            menu.updateChemicals(packet.chemicalFilters());
        }
        if (context.player().containerMenu instanceof ImporterMenu menu) {
            menu.updateChemicals(packet.chemicalFilters());
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncChemicalListToClient> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncChemicalListToClient::pos,
            ChemicalStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SyncChemicalListToClient::chemicalFilters,
            SyncChemicalListToClient::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
