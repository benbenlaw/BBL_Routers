package com.benbenlaw.routers.item;

import net.minecraft.world.item.Item;

public class RFUpgradeItem extends Item {

    private final int RFPerTick;

    public RFUpgradeItem(Properties properties, int RFPerTick) {
        super(properties);
        this.RFPerTick = RFPerTick;
    }

    public int getRFPerTick() {
        return RFPerTick;
    }
}
