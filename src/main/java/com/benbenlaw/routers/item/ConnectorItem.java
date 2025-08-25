package com.benbenlaw.routers.item;

import com.benbenlaw.routers.block.ExporterBlock;
import com.benbenlaw.routers.block.ImporterBlock;
import com.benbenlaw.routers.block.entity.ImporterBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ConnectorItem extends Item {
    public ConnectorItem(Properties properties) {
        super(properties);
    }



    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {

        BlockPos importerPos = stack.get(RoutersDataComponents.IMPORTER_POSITION.get());
        if (importerPos != null) {
            list.add(Component.literal("Importer Pos: " + importerPos.getX() + ", " + importerPos.getY() + ", " + importerPos.getZ()));
        } else {
            list.add(Component.literal("No Importer Pos Set").withStyle(ChatFormatting.RED));
        }
    }
}
