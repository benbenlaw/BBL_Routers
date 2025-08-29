package com.benbenlaw.routers.data;

import com.benbenlaw.routers.Routers;
import com.benbenlaw.routers.item.RoutersItems;
import com.benbenlaw.routers.util.RoutersTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RoutersItemTagsProvider extends ItemTagsProvider {

    RoutersItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags.contentsGetter(), Routers.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //RF Upgrades
        this.tag(RoutersTags.Items.RF_UPGRADES).add(
                RoutersItems.RF_UPGRADE_1.get(),
                RoutersItems.RF_UPGRADE_2.get(),
                RoutersItems.RF_UPGRADE_3.get(),
                RoutersItems.RF_UPGRADE_4.get()
        );

        //Item Upgrades
        this.tag(RoutersTags.Items.ITEM_UPGRADES).add(
                RoutersItems.ITEM_UPGRADE_1.get(),
                RoutersItems.ITEM_UPGRADE_2.get(),
                RoutersItems.ITEM_UPGRADE_3.get(),
                RoutersItems.ITEM_UPGRADE_4.get()
        );

        //Fluid Upgrades
        this.tag(RoutersTags.Items.FLUID_UPGRADES).add(
                RoutersItems.FLUID_UPGRADE_1.get(),
                RoutersItems.FLUID_UPGRADE_2.get(),
                RoutersItems.FLUID_UPGRADE_3.get(),
                RoutersItems.FLUID_UPGRADE_4.get()
        );

        //Chemical Upgrades
        this.tag(RoutersTags.Items.CHEMICAL_UPGRADES).add(
                RoutersItems.CHEMICAL_UPGRADE_1.get(),
                RoutersItems.CHEMICAL_UPGRADE_2.get(),
                RoutersItems.CHEMICAL_UPGRADE_3.get(),
                RoutersItems.CHEMICAL_UPGRADE_4.get()
        );

        //Speed Upgrades
        this.tag(RoutersTags.Items.SPEED_UPGRADES).add(
                RoutersItems.SPEED_UPGRADE_1.get(),
                RoutersItems.SPEED_UPGRADE_2.get(),
                RoutersItems.SPEED_UPGRADE_3.get(),
                RoutersItems.SPEED_UPGRADE_4.get()
        );

        //Round Robin Upgrade
        this.tag(RoutersTags.Items.ROUND_ROBIN_UPGRADES).add(
                RoutersItems.ROUND_ROBIN_UPGRADE.get()
        );

    }

}