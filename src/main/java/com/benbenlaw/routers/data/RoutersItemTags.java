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

public class RoutersItemTags extends ItemTagsProvider {

    RoutersItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
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

    }

}