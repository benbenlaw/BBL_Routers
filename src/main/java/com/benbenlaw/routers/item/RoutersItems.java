package com.benbenlaw.routers.item;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.config.StartupConfig;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RoutersItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Routers.MOD_ID);

    public static final DeferredItem<Item> ROUTER_CONNECTOR = ITEMS.register("router_connector",
            () -> new ConnectorItem(new Item.Properties()));

    public static final DeferredItem<Item> RF_UPGRADE_1 = ITEMS.register("rf_upgrade_1",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.RFPerTick1.get()));
    public static final DeferredItem<Item> RF_UPGRADE_2 = ITEMS.register("rf_upgrade_2",
            () -> new UpgradeItem(new Item.Properties(),  StartupConfig.RFPerTick2.get()));
    public static final DeferredItem<Item> RF_UPGRADE_3 = ITEMS.register("rf_upgrade_3",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.RFPerTick3.get()));
    public static final DeferredItem<Item> RF_UPGRADE_4 = ITEMS.register("rf_upgrade_4",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.RFPerTick4.get()));

    public static final DeferredItem<Item> ITEM_UPGRADE_1 = ITEMS.register("item_upgrade_1",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.itemPerOperation1.get()));
    public static final DeferredItem<Item> ITEM_UPGRADE_2 = ITEMS.register("item_upgrade_2",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.itemPerOperation2.get()));
    public static final DeferredItem<Item> ITEM_UPGRADE_3 = ITEMS.register("item_upgrade_3",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.itemPerOperation3.get()));
    public static final DeferredItem<Item> ITEM_UPGRADE_4 = ITEMS.register("item_upgrade_4",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.itemPerOperation4.get()));

    public static final DeferredItem<Item> FLUID_UPGRADE_1 = ITEMS.register("fluid_upgrade_1",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.fluidPerOperation1.get()));
    public static final DeferredItem<Item> FLUID_UPGRADE_2 = ITEMS.register("fluid_upgrade_2",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.fluidPerOperation2.get()));
    public static final DeferredItem<Item> FLUID_UPGRADE_3 = ITEMS.register("fluid_upgrade_3",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.fluidPerOperation3.get()));
    public static final DeferredItem<Item> FLUID_UPGRADE_4 = ITEMS.register("fluid_upgrade_4",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.fluidPerOperation4.get()));

    public static final DeferredItem<Item> CHEMICAL_UPGRADE_1 = ITEMS.register("chemical_upgrade_1",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.chemicalPerOperation1.get()));
    public static final DeferredItem<Item> CHEMICAL_UPGRADE_2 = ITEMS.register("chemical_upgrade_2",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.chemicalPerOperation2.get()));
    public static final DeferredItem<Item> CHEMICAL_UPGRADE_3 = ITEMS.register("chemical_upgrade_3",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.chemicalPerOperation3.get()));
    public static final DeferredItem<Item> CHEMICAL_UPGRADE_4 = ITEMS.register("chemical_upgrade_4",
            () -> new UpgradeItem(new Item.Properties() , StartupConfig.chemicalPerOperation4.get()));

    public static final DeferredItem<Item> SPEED_UPGRADE_1 = ITEMS.register("speed_upgrade_1",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.speedPerOperation1.get()));
    public static final DeferredItem<Item> SPEED_UPGRADE_2 = ITEMS.register("speed_upgrade_2",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.speedPerOperation2.get()));
    public static final DeferredItem<Item> SPEED_UPGRADE_3 = ITEMS.register("speed_upgrade_3",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.speedPerOperation3.get()));
    public static final DeferredItem<Item> SPEED_UPGRADE_4 = ITEMS.register("speed_upgrade_4",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.speedPerOperation4.get()));

    public static final DeferredItem<Item> SOURCE_UPGRADE_1 = ITEMS.register("source_upgrade_1",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.sourcePerOperation1.get()));
    public static final DeferredItem<Item> SOURCE_UPGRADE_2 = ITEMS.register("source_upgrade_2",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.sourcePerOperation2.get()));
    public static final DeferredItem<Item> SOURCE_UPGRADE_3 = ITEMS.register("source_upgrade_3",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.sourcePerOperation3.get()));
    public static final DeferredItem<Item> SOURCE_UPGRADE_4 = ITEMS.register("source_upgrade_4",
            () -> new UpgradeItem(new Item.Properties(), StartupConfig.sourcePerOperation4.get()));

    public static final DeferredItem<Item> SOUL_UPGRADE = ITEMS.register("soul_upgrade",
            () -> new UpgradeItem(new Item.Properties(), 10));





    public static final DeferredItem<Item> ROUND_ROBIN_UPGRADE = ITEMS.register("round_robin_upgrade",
            () -> new UpgradeItem(new Item.Properties(), 0));


}
