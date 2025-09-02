package com.benbenlaw.routers.data;

import com.benbenlaw.routers.block.RoutersBlocks;
import com.benbenlaw.routers.item.RoutersItems;
import com.buuz135.industrialforegoingsouls.IndustrialForegoingSouls;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import mekanism.common.registries.MekanismItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;

public class RoutersRecipeProvider extends RecipeProvider {

    public RoutersRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {

        //Importer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersBlocks.IMPORTER_BLOCK.get())
                .pattern("BAB")
                .pattern("ACA")
                .pattern("BAB")
                .define('A', ItemTags.LOGS)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.HOPPER)
                .group("strainers")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);

        //Exporter
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersBlocks.EXPORTER_BLOCK.get())
                .pattern("BAB")
                .pattern("A A")
                .pattern("BAB")
                .define('A', ItemTags.LOGS)
                .define('B', Items.IRON_INGOT)
                .group("strainers")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);

        //Connector
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ROUTER_CONNECTOR.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" B ")
                .define('A', Items.IRON_INGOT)
                .define('B', Tags.Items.RODS_WOODEN)
                .group("strainers")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);

        //RF Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.RF_UPGRADE_1.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.REDSTONE)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.IRON_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(consumer);

        //RF Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.RF_UPGRADE_2.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.REDSTONE)
                .define('B', Items.GOLD_INGOT)
                .define('C', Items.GOLD_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(consumer);

        //RF Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.RF_UPGRADE_3.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.REDSTONE)
                .define('B', Items.DIAMOND)
                .define('C', Items.EMERALD)
                .group("strainers")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(consumer);

        //RF Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.RF_UPGRADE_4.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.REDSTONE)
                .define('B', Items.NETHERITE_INGOT)
                .define('C', Items.NETHERITE_SCRAP)
                .group("strainers")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(consumer);

        //Item Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ITEM_UPGRADE_1.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.COPPER_INGOT)
                .define('B', Items.IRON_INGOT)
                .define('C', Items.IRON_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);

        //Item Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ITEM_UPGRADE_2.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.GOLD_INGOT)
                .define('C', Items.GOLD_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(consumer);

        //Item Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ITEM_UPGRADE_3.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.DIAMOND)
                .define('C', Items.EMERALD)
                .group("strainers")
                .unlockedBy("has_item", has(Items.DIAMOND))
                .save(consumer);

        //Item Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ITEM_UPGRADE_4.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.DIAMOND)
                .define('B', Items.NETHERITE_INGOT)
                .define('C', Items.NETHERITE_SCRAP)
                .group("strainers")
                .unlockedBy("has_item", has(Items.NETHERITE_INGOT))
                .save(consumer);

        //Fluid Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.FLUID_UPGRADE_1.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.COPPER_INGOT)
                .define('B', Items.BUCKET)
                .define('C', Items.IRON_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(consumer);

        //Fluid Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.FLUID_UPGRADE_2.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.IRON_INGOT)
                .define('B', Items.BUCKET)
                .define('C', Items.GOLD_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(consumer);

        //Fluid Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.FLUID_UPGRADE_3.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.GOLD_INGOT)
                .define('B', Items.BUCKET)
                .define('C', Items.EMERALD)
                .group("strainers")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(consumer);

        //Fluid Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.FLUID_UPGRADE_4.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.DIAMOND)
                .define('B', Items.BUCKET)
                .define('C', Items.NETHERITE_SCRAP)
                .group("strainers")
                .unlockedBy("has_item", has(Items.BUCKET))
                .save(consumer);

        //Chemical Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.CHEMICAL_UPGRADE_1.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.IRON_INGOT)
                .define('B', MekanismItems.CHEMICAL_UPGRADE)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GLASS))
                .save(consumer.withConditions(new ModLoadedCondition("mekanism")));

        //Chemical Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.CHEMICAL_UPGRADE_2.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.GOLD_INGOT)
                .define('B', MekanismItems.CHEMICAL_UPGRADE)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GLASS))
                .save(consumer.withConditions(new ModLoadedCondition("mekanism")));

        //Chemical Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.CHEMICAL_UPGRADE_3.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.DIAMOND)
                .define('B', MekanismItems.CHEMICAL_UPGRADE)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GLASS))
                .save(consumer.withConditions(new ModLoadedCondition("mekanism")));

        //Chemical Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.CHEMICAL_UPGRADE_4.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.NETHERITE_INGOT)
                .define('B', MekanismItems.CHEMICAL_UPGRADE)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GLASS))
                .save(consumer.withConditions(new ModLoadedCondition("mekanism")));

        //Speed Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SPEED_UPGRADE_1.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" C ")
                .define('A', Items.REDSTONE)
                .define('B', Items.SUGAR)
                .define('C', Items.IRON_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.SUGAR))
                .save(consumer);

        //Speed Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SPEED_UPGRADE_2.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" C ")
                .define('A', Items.REDSTONE)
                .define('B', Items.SUGAR)
                .define('C', Items.GOLD_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.SUGAR))
                .save(consumer);

        //Speed Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SPEED_UPGRADE_3.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" C ")
                .define('A', Items.REDSTONE)
                .define('B', Items.SUGAR)
                .define('C', Items.EMERALD)
                .group("strainers")
                .unlockedBy("has_item", has(Items.SUGAR))
                .save(consumer);

        //Speed Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SPEED_UPGRADE_4.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" C ")
                .define('A', Items.REDSTONE)
                .define('B', Items.SUGAR)
                .define('C', Items.NETHERITE_SCRAP)
                .group("strainers")
                .unlockedBy("has_item", has(Items.SUGAR))
                .save(consumer);

        //Round Robin Upgrade
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.ROUND_ROBIN_UPGRADE.get())
                .pattern("CAC")
                .pattern("ABA")
                .pattern("CAC")
                .define('A', Items.COPPER_INGOT)
                .define('B', Items.REDSTONE)
                .define('C', Items.IRON_NUGGET)
                .group("strainers")
                .unlockedBy("has_item", has(Items.REDSTONE))
                .save(consumer);

        //Source Upgrade 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SOURCE_UPGRADE_1.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.IRON_INGOT)
                .define('B', BlockRegistry.SOURCE_GEM_BLOCK)
                .group("strainers")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer.withConditions(new ModLoadedCondition("ars_nouveau")));

        //Source Upgrade 2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SOURCE_UPGRADE_2.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.GOLD_INGOT)
                .define('B', BlockRegistry.SOURCE_GEM_BLOCK)
                .group("strainers")
                .unlockedBy("has_item", has(Items.GOLD_INGOT))
                .save(consumer.withConditions(new ModLoadedCondition("ars_nouveau")));

        //Source Upgrade 3
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SOURCE_UPGRADE_3.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.DIAMOND)
                .define('B', BlockRegistry.SOURCE_GEM_BLOCK)
                .group("strainers")
                .unlockedBy("has_item", has(Items.DIAMOND))
                .save(consumer.withConditions(new ModLoadedCondition("ars_nouveau")));

        //Source Upgrade 4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SOURCE_UPGRADE_4.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.NETHERITE_INGOT)
                .define('B', BlockRegistry.SOURCE_GEM_BLOCK)
                .group("strainers")
                .unlockedBy("has_item", has(Items.NETHERITE_INGOT))
                .save(consumer.withConditions(new ModLoadedCondition("ars_nouveau")));

        //Soul Upgrade
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RoutersItems.SOUL_UPGRADE.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern(" A ")
                .define('A', Items.ECHO_SHARD)
                .define('B', IndustrialForegoingSouls.SOUL_SURGE_BLOCK.getBlock())
                .group("strainers")
                .unlockedBy("has_item", has(IndustrialForegoingSouls.SOUL_SURGE_BLOCK.getBlock()))
                .save(consumer.withConditions(new ModLoadedCondition("industrialforegoingsouls")));
    }

}