package com.uravgcode.survivalunlocked.feature.moremobheads.variant;

import org.bukkit.craftbukkit.entity.CraftCopperGolem;
import org.bukkit.entity.CopperGolem;

public class CopperGolemState {
    public static String name(CopperGolem golem) {
        if (!(golem instanceof CraftCopperGolem craftCopperGolem)) return null;
        return craftCopperGolem.getHandle().getWeatherState().name().toLowerCase();
    }
}
