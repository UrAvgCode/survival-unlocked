package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.feature.Feature;
import com.uravgcode.survivalunlocked.feature.FeatureList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        var logger = getComponentLogger();
        var pluginManager = getServer().getPluginManager();
        var features = FeatureList.getFeatures(this);

        for (var feature : features) {
            var annotation = feature.getClass().getAnnotation(Feature.class);
            if (annotation == null) continue;

            var path = annotation.name();
            if (config.getBoolean("features." + path + ".enabled", false)) {
                pluginManager.registerEvents(feature, this);
                logger.info(Component.text(path + " enabled", NamedTextColor.GREEN));
            } else {
                logger.info(Component.text(path + " disabled", NamedTextColor.RED));
            }
        }
    }
}
