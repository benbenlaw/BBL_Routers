package com.benbenlaw.routers.data;

import com.benbenlaw.routers.Routers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Routers.MOD_ID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();


        generator.addProvider(event.includeServer(), new RoutersRecipeProvider(packOutput, event.getLookupProvider()));

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(RoutersLootTableProvider::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));


        RoutersBlockTagsProvider blockTags = new RoutersBlockTagsProvider(packOutput, lookupProvider, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTags);

        RoutersItemTagsProvider itemTags = new RoutersItemTagsProvider(packOutput, lookupProvider, blockTags, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), itemTags);

        generator.addProvider(event.includeClient(), new RoutersItemModelsProvider(packOutput, event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new RoutersLangProvider(packOutput, event.getExistingFileHelper()));



    }


}