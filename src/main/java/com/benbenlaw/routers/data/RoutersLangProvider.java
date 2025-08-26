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

        //Blocks
        addBlockTranslation("importer_block", "Importer");
        addBlockTranslation("exporter_block", "Exporter");

        //Messages
        addMessageTranslation("exporter.remove_importer", "Removed Importer from Exporter");
        addMessageTranslation("exporter.add_importer", "Added Importer to Exporter");
        addMessageTranslation("importer.removed_pos", "Removed Importer position");
        addMessageTranslation("importer.added_pos", "Added Importer position");

    }

    private void addItemTranslation(String name, String translation) {
        add("item." + Routers.MOD_ID + "." + name, translation);
    }

    private void addBlockTranslation(String name, String translation) {
        add("block." + Routers.MOD_ID + "." + name, translation);
    }

    private void addMessageTranslation(String name, String translation) {
        add("messages." + Routers.MOD_ID + "." + name, translation);
    }
}

