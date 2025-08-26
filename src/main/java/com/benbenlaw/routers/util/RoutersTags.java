package com.benbenlaw.routers.util;

import com.benbenlaw.routers.Routers;
import de.maxhenkel.pipez.corelib.tag.ItemTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RoutersTags {

    public static class Items {
        public static final TagKey<Item> RF_UPGRADES = tag("rf_upgrades");


        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Routers.MOD_ID, name));
        }

        private static TagKey<Item> commonTags(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
        }

    }



    public static class Blocks {

    }
}
