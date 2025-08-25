package com.benbenlaw.routers.item;

import com.benbenlaw.routers.Routers;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RoutersItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Routers.MOD_ID);

    public static final DeferredItem<Item> ROUTER_CONNECTOR = ITEMS.register("router_connector",
            () -> new ConnectorItem(new Item.Properties()));
}
