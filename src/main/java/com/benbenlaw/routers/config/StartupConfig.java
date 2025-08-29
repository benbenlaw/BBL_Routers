package com.benbenlaw.routers.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class StartupConfig {


    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> RFPerTick1;
    public static final ModConfigSpec.ConfigValue<Integer> RFPerTick2;
    public static final ModConfigSpec.ConfigValue<Integer> RFPerTick3;
    public static final ModConfigSpec.ConfigValue<Integer> RFPerTick4;

    public static final ModConfigSpec.ConfigValue<Integer> itemPerOperation1;
    public static final ModConfigSpec.ConfigValue<Integer> itemPerOperation2;
    public static final ModConfigSpec.ConfigValue<Integer> itemPerOperation3;
    public static final ModConfigSpec.ConfigValue<Integer> itemPerOperation4;

    public static final ModConfigSpec.ConfigValue<Integer> fluidPerOperation1;
    public static final ModConfigSpec.ConfigValue<Integer> fluidPerOperation2;
    public static final ModConfigSpec.ConfigValue<Integer> fluidPerOperation3;
    public static final ModConfigSpec.ConfigValue<Integer> fluidPerOperation4;

    public static final ModConfigSpec.ConfigValue<Integer> chemicalPerOperation1;
    public static final ModConfigSpec.ConfigValue<Integer> chemicalPerOperation2;
    public static final ModConfigSpec.ConfigValue<Integer> chemicalPerOperation3;
    public static final ModConfigSpec.ConfigValue<Integer> chemicalPerOperation4;

    public static final ModConfigSpec.ConfigValue<Integer> defaultSpeedPerOperation;
    public static final ModConfigSpec.ConfigValue<Integer> speedPerOperation1;
    public static final ModConfigSpec.ConfigValue<Integer> speedPerOperation2;
    public static final ModConfigSpec.ConfigValue<Integer> speedPerOperation3;
    public static final ModConfigSpec.ConfigValue<Integer> speedPerOperation4;

    public static final ModConfigSpec.ConfigValue<Integer> sourcePerOperation1;
    public static final ModConfigSpec.ConfigValue<Integer> sourcePerOperation2;
    public static final ModConfigSpec.ConfigValue<Integer> sourcePerOperation3;
    public static final ModConfigSpec.ConfigValue<Integer> sourcePerOperation4;





    static {
        BUILDER.comment("Routers Config").push("RF Upgrades");

        RFPerTick1 = BUILDER
                .comment("The maximum RF per tick that tier 1 can provide.")
                .defineInRange("RF Per Operation 1",  10, 1, Integer.MAX_VALUE);

        RFPerTick2 = BUILDER
                .comment("The maximum RF per tick that tier 2 can provide.")
                .defineInRange("RF Per Operation 2", 240, 1, Integer.MAX_VALUE);

        RFPerTick3 = BUILDER
                .comment("The maximum RF per tick that tier 3 can provide.")
                .defineInRange("RF Per Operation 3", 12000, 1, Integer.MAX_VALUE);

        RFPerTick4 = BUILDER
                .comment("The maximum RF per tick that tier 4 can provide.")
                .defineInRange("RF Per Operation 4", 50000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Item Upgrades");

        itemPerOperation1 = BUILDER
                .comment("The maximum items per operation that tier 1 can provide.")
                .defineInRange("Items Per Operation 1", 1, 1, Integer.MAX_VALUE);

        itemPerOperation2 = BUILDER
                .comment("The maximum items per operation that tier 2 can provide.")
                .defineInRange("Items Per Operation 2", 8, 1, Integer.MAX_VALUE);

        itemPerOperation3 = BUILDER
                .comment("The maximum items per operation that tier 3 can provide.")
                .defineInRange("Items Per Operation 3", 32, 1, Integer.MAX_VALUE);

        itemPerOperation4 = BUILDER
                .comment("The maximum items per operation that tier 4 can provide.")
                .defineInRange("Items Per Operation 4", 64, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Fluid Upgrades");

        fluidPerOperation1 = BUILDER
                .comment("The maximum mb per operation that tier 1 can provide.")
                .defineInRange("Fluid Per Operation 1", 100, 1, Integer.MAX_VALUE);
        fluidPerOperation2 = BUILDER
                .comment("The maximum mb per operation that tier 2 can provide.")
                .defineInRange("Fluid Per Operation 2", 1000, 1, Integer.MAX_VALUE);
        fluidPerOperation3 = BUILDER
                .comment("The maximum mb per operation that tier 3 can provide.")
                .defineInRange("Fluid Per Operation 3", 10000, 1, Integer.MAX_VALUE);
        fluidPerOperation4 = BUILDER
                .comment("The maximum mb per operation that tier 4 can provide.")
                .defineInRange("Fluid Per Operation 4", 100000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Chemical Upgrades");
        chemicalPerOperation1 = BUILDER
                .comment("The maximum mB per operation that tier 1 can provide.")
                .defineInRange("Chemical Per Operation 1", 10, 1, Integer.MAX_VALUE);
        chemicalPerOperation2 = BUILDER
                .comment("The maximum mB per operation that tier 2 can provide.")
                .defineInRange("Chemical Per Operation 2", 100, 1, Integer.MAX_VALUE);
        chemicalPerOperation3 = BUILDER
                .comment("The maximum mB per operation that tier 3 can provide.")
                .defineInRange("Chemical Per Operation 3", 1000, 1, Integer.MAX_VALUE);
        chemicalPerOperation4 = BUILDER
                .comment("The maximum mB per operation that tier 4 can provide.")
                .defineInRange("Chemical Per Operation 4", 10000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Speed Upgrades");

        defaultSpeedPerOperation = BUILDER
                .comment("The default speed multiplier that is used when no speed upgrades are installed.")
                .defineInRange("Default Speed Per Operation", 40, 1, Integer.MAX_VALUE);
        speedPerOperation1 = BUILDER
                .comment("The speed multiplier that tier 1 can provide.")
                .defineInRange("Speed Per Operation 1", 30, 1, Integer.MAX_VALUE);
        speedPerOperation2 = BUILDER
                .comment("The speed multiplier that tier 2 can provide.")
                .defineInRange("Speed Per Operation 2", 20, 1, Integer.MAX_VALUE);
        speedPerOperation3 = BUILDER
                .comment("The speed multiplier that tier 3 can provide.")
                .defineInRange("Speed Per Operation 3", 10, 1, Integer.MAX_VALUE);
        speedPerOperation4 = BUILDER
                .comment("The speed multiplier that tier 4 can provide.")
                .defineInRange("Speed Per Operation 4", 1, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Source Upgrades");

        sourcePerOperation1 = BUILDER
                .comment("The maximum sources per operation that tier 1 can provide.")
                .defineInRange("Source Per Operation 1", 10, 1, Integer.MAX_VALUE);
        sourcePerOperation2 = BUILDER
                .comment("The maximum sources per operation that tier 2 can provide.")
                .defineInRange("Source Per Operation 2", 100, 1, Integer.MAX_VALUE);
        sourcePerOperation3 = BUILDER
                .comment("The maximum sources per operation that tier 3 can provide.")
                .defineInRange("Source Per Operation 3", 500, 1, Integer.MAX_VALUE);
        sourcePerOperation4 = BUILDER
                .comment("The maximum sources per operation that tier 4 can provide.")
                .defineInRange("Source Per Operation 4", 1000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        SPEC = BUILDER.build();

    }

}
