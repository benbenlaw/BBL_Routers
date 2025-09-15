package com.benbenlaw.routers.util;

import com.benbenlaw.routers.Routers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class RoutersTags {

    public static class Items {

        public static final TagKey<Item> UPGRADES = commonTags("upgrades");

        public static final TagKey<Item> RF_UPGRADES = tag("rf_upgrades");
        public static final TagKey<Item> ITEM_UPGRADES = tag("item_upgrades");
        public static final TagKey<Item> FLUID_UPGRADES = tag("fluid_upgrades");
        public static final TagKey<Item> CHEMICAL_UPGRADES = tag("chemical_upgrades");
        public static final TagKey<Item> SPEED_UPGRADES = tag("speed_upgrades");
        public static final TagKey<Item> SOURCE_UPGRADES = tag("source_upgrades");
        public static final TagKey<Item> ROUND_ROBIN_UPGRADES = tag("round_robin_upgrades");
        public static final TagKey<Item> SOUL_UPGRADES = tag("soul_upgrades");
        public static final TagKey<Item> PRESSURE_UPGRADES = tag("pressure_upgrades");
        public static final TagKey<Item> HEAT_UPGRADES_PC = tag("heat_upgrades_pc");
        public static final TagKey<Item> FILTERS = tag("filters");
        public static final TagKey<Item> DIMENSIONAL_UPGRADES = tag("dimensional_upgrades");



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
