package com.benbenlaw.routers.data;

import com.benbenlaw.routers.Routers;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RoutersLangProvider extends LanguageProvider {

    public RoutersLangProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Routers.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //Creative Tab
        add("itemGroup.routers", "Routers");

        //Items
        addItemTranslation("router_connector", "Connector");
        addItemTranslation("rf_upgrade_1", "RF Upgrade I");
        addItemTranslation("rf_upgrade_2", "RF Upgrade II");
        addItemTranslation("rf_upgrade_3", "RF Upgrade III");
        addItemTranslation("rf_upgrade_4", "RF Upgrade IV");

        addItemTranslation("item_upgrade_1", "Item Upgrade I");
        addItemTranslation("item_upgrade_2", "Item Upgrade II");
        addItemTranslation("item_upgrade_3", "Item Upgrade III");
        addItemTranslation("item_upgrade_4", "Item Upgrade IV");

        addItemTranslation("fluid_upgrade_1", "Fluid Upgrade I");
        addItemTranslation("fluid_upgrade_2", "Fluid Upgrade II");
        addItemTranslation("fluid_upgrade_3", "Fluid Upgrade III");
        addItemTranslation("fluid_upgrade_4", "Fluid Upgrade IV");

        addItemTranslation("chemical_upgrade_1", "Chemical Upgrade I");
        addItemTranslation("chemical_upgrade_2", "Chemical Upgrade II");
        addItemTranslation("chemical_upgrade_3", "Chemical Upgrade III");
        addItemTranslation("chemical_upgrade_4", "Chemical Upgrade IV");

        addItemTranslation("speed_upgrade_1", "Speed Upgrade I");
        addItemTranslation("speed_upgrade_2", "Speed Upgrade II");
        addItemTranslation("speed_upgrade_3", "Speed Upgrade III");
        addItemTranslation("speed_upgrade_4", "Speed Upgrade IV");


        //Blocks
        addBlockTranslation("importer_block", "Importer");
        addBlockTranslation("exporter_block", "Exporter");

        //Messages
        addMessageTranslation("exporter.remove_importer", "Removed Importer from Exporter");
        addMessageTranslation("exporter.add_importer", "Added Importer to Exporter");
        addMessageTranslation("importer.removed_pos", "Removed Importer position");
        addMessageTranslation("importer.added_pos", "Added Importer position");

        //Tooltips
        addTooltipsTranslation("hold_shift", "Press SHIFT for more info");
        addTooltipsTranslation("rf_upgrade", "Allows Extraction of RF from the Exporter at %s RF/Per Operation");
        addTooltipsTranslation("item_upgrade", "Allows Extraction of Items from the Exporter at %s Items/Per Operation");
        addTooltipsTranslation("fluid_upgrade", "Allows Extraction of Fluids from the Exporter at %s MB/Per Operation");
        addTooltipsTranslation("chemical_upgrade", "Allows Extraction of Chemicals from the Exporter at %s mB/Per Operation");
        addTooltipsTranslation("speed_upgrade", "Allows Extraction of from the Exporter every %s Ticks");


    }

    private void addItemTranslation(String name, String translation) {
        add("item." + Routers.MOD_ID + "." + name, translation);
    }

    private void addBlockTranslation(String name, String translation) {
        add("block." + Routers.MOD_ID + "." + name, translation);
    }

    private void addMessageTranslation(String name, String translation) {
        add("message." + Routers.MOD_ID + "." + name, translation);
    }

    private void addTooltipsTranslation(String name, String translation) {
        add("tooltip." + Routers.MOD_ID + "." + name, translation);
    }
}

