package com.benbenlaw.routers.item;

import com.benbenlaw.routers.Routers;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RoutersItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Routers.MOD_ID);

    public static final DeferredItem<Item> ROUTER_CONNECTOR = ITEMS.register("router_connector",
            () -> new ConnectorItem(new Item.Properties()));

    public static final DeferredItem<Item> RF_UPGRADE_1 = ITEMS.register("rf_upgrade_1",
            () -> new RFUpgradeItem(new Item.Properties().stacksTo(1), 100));
    public static final DeferredItem<Item> RF_UPGRADE_2 = ITEMS.register("rf_upgrade_2",
            () -> new RFUpgradeItem(new Item.Properties().stacksTo(1), 1000));
    public static final DeferredItem<Item> RF_UPGRADE_3 = ITEMS.register("rf_upgrade_3",
            () -> new RFUpgradeItem(new Item.Properties().stacksTo(1), 10000));
    public static final DeferredItem<Item> RF_UPGRADE_4 = ITEMS.register("rf_upgrade_4",
            () -> new RFUpgradeItem(new Item.Properties().stacksTo(1), 100000));

}
