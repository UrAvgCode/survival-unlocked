package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.feature.betterarmorstands.ArmorStandListener;
import com.uravgcode.survivalunlocked.feature.customshapedportals.CustomPortalListener;
import com.uravgcode.survivalunlocked.feature.invisibleitemframes.ItemFrameListener;
import com.uravgcode.survivalunlocked.feature.moremobheads.MobHeadDropListener;
import com.uravgcode.survivalunlocked.feature.playerheaddrops.PlayerHeadDropListener;
import com.uravgcode.survivalunlocked.feature.silencemobs.SilenceMobListener;
import com.uravgcode.survivalunlocked.feature.silktouchpaintings.PaintingDropListener;
import com.uravgcode.survivalunlocked.feature.smoothsleeptransition.SleepListener;
import com.uravgcode.survivalunlocked.feature.throwablefireballs.FireballThrowListener;
import com.uravgcode.survivalunlocked.feature.villagersfollowemeralds.VillagerFollowListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;

public final class SurvivalUnlocked extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!Files.exists(getDataFolder().toPath().resolve("heads.yml"))) {
            saveResource("heads.yml", false);
        }

        var config = getConfig();
        var logger = getLogger();
        var pluginManager = getServer().getPluginManager();

        if (config.getBoolean("smooth-sleep-transition.enabled", false)) {
            pluginManager.registerEvents(new SleepListener(this), this);
            logger.info("smooth sleep transition enabled");
        }

        if (config.getBoolean("custom-shaped-portals.enabled", false)) {
            pluginManager.registerEvents(new CustomPortalListener(this), this);
            logger.info("custom shaped nether portals enabled");
        }

        if (config.getBoolean("throwable-fireballs.enabled", false)) {
            pluginManager.registerEvents(new FireballThrowListener(), this);
            logger.info("throwable fireballs enabled");
        }

        if (config.getBoolean("invisible-item-frames.enabled", false)) {
            pluginManager.registerEvents(new ItemFrameListener(), this);
            logger.info("invisible item frames enabled");
        }

        if (config.getBoolean("silk-touch-paintings.enabled", false)) {
            pluginManager.registerEvents(new PaintingDropListener(), this);
            logger.info("silk touch paintings enabled");
        }

        if (config.getBoolean("better-armor-stands.enabled", false)) {
            pluginManager.registerEvents(new ArmorStandListener(this), this);
            logger.info("better armor stands enabled");
        }

        if (config.getBoolean("villagers-follow-emeralds.enabled", false)) {
            pluginManager.registerEvents(new VillagerFollowListener(this), this);
            logger.info("villagers follow emeralds enabled");
        }

        if (config.getBoolean("silence-mobs.enabled", false)) {
            pluginManager.registerEvents(new SilenceMobListener(), this);
            logger.info("silence mobs enabled");
        }

        if (config.getBoolean("more-mob-heads.enabled", false)) {
            pluginManager.registerEvents(new MobHeadDropListener(this), this);
            logger.info("more mob heads enabled");
        }

        if (config.getBoolean("player-head-drops.enabled", false)) {
            pluginManager.registerEvents(new PlayerHeadDropListener(), this);
            logger.info("player head drops enabled");
        }
    }
}
