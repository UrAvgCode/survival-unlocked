package com.uravgcode.survivalunlocked.feature;

import com.uravgcode.survivalunlocked.feature.betterarmorstands.ArmorStandListener;
import com.uravgcode.survivalunlocked.feature.customshapedportals.CustomPortalListener;
import com.uravgcode.survivalunlocked.feature.invisibleitemframes.ItemFrameListener;
import com.uravgcode.survivalunlocked.feature.moremobheads.MobHeadDropListener;
import com.uravgcode.survivalunlocked.feature.playerheaddrops.PlayerHeadDropListener;
import com.uravgcode.survivalunlocked.feature.silencemobs.SilenceMobListener;
import com.uravgcode.survivalunlocked.feature.silktouchpaintings.PaintingDropListener;
import com.uravgcode.survivalunlocked.feature.silktouchspawners.SilkTouchSpawnerListener;
import com.uravgcode.survivalunlocked.feature.smoothsleeptransition.SleepListener;
import com.uravgcode.survivalunlocked.feature.throwablefireballs.FireballThrowListener;
import com.uravgcode.survivalunlocked.feature.villagersfollowemeralds.VillagerFollowListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeatureList {
    public static List<@NotNull Listener> getFeatures(@NotNull JavaPlugin plugin) {
        return List.of(
            new SleepListener(plugin),
            new CustomPortalListener(plugin),
            new FireballThrowListener(),
            new ArmorStandListener(plugin),
            new ItemFrameListener(),
            new PaintingDropListener(),
            new SilkTouchSpawnerListener(),
            new VillagerFollowListener(plugin),
            new SilenceMobListener(),
            new MobHeadDropListener(plugin),
            new PlayerHeadDropListener()
        );
    }
}
