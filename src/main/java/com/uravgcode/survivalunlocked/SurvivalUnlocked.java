package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.Feature;
import com.uravgcode.survivalunlocked.feature.FeatureList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
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
                injectConfigValues(feature, config, this);
                pluginManager.registerEvents(feature, this);
                logger.info(Component.text(path + " enabled", NamedTextColor.GREEN));
            } else {
                logger.info(Component.text(path + " disabled", NamedTextColor.RED));
            }
        }
    }

    public static void injectConfigValues(Listener feature, FileConfiguration config, JavaPlugin plugin) {
        if (!feature.getClass().isAnnotationPresent(Feature.class)) return;
        var featurePath = feature.getClass().getAnnotation(Feature.class).name();

        for (var field : feature.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigValue.class)) continue;
            var annotation = field.getAnnotation(ConfigValue.class);
            var path = "features." + featurePath + "." + annotation.name();
            if (!config.contains(path)) continue;

            try {
                field.setAccessible(true);
                var value = config.get(path);
                field.set(feature, value);
            } catch (IllegalAccessException e) {
                plugin.getLogger().warning("failed to inject config value for " + field.getName());
            }
        }
    }
}
