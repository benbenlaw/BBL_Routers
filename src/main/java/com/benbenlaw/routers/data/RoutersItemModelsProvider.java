package com.benbenlaw.routers.data;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.item.RoutersItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

public class RoutersItemModelsProvider extends ItemModelProvider {

    public RoutersItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Routers.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        simpleItem(RoutersItems.ROUTER_CONNECTOR);
        simpleItem(RoutersItems.RF_UPGRADE_1);
        simpleItem(RoutersItems.RF_UPGRADE_2);
        simpleItem(RoutersItems.RF_UPGRADE_3);
        simpleItem(RoutersItems.RF_UPGRADE_4);

    }

    private void simpleItem(DeferredItem<Item> item) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, "item/" + item.getId().getPath()));
    }

    @Override
    public String getName() {
        return Routers.MOD_ID + " Item Models";
    }
}
