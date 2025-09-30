package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.Feature;
import com.uravgcode.survivalunlocked.feature.FeatureList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;

public final class SurvivalUnlocked extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!Files.exists(getDataFolder().toPath().resolve("heads.yml"))) {
            saveResource("heads.yml", false);
        }

        var logger = getComponentLogger();
        var pluginVersion = getPluginMeta().getVersion();
        var configVersion = getConfig().getString("config.version", "0.0.0");

        if (!pluginVersion.equals(configVersion)) {
            try {
                updateConfig();
                reloadConfig();
                logger.info("updating config to version {}", pluginVersion);
            } catch (IOException exception) {
                logger.warn("failed to update config: {}", exception.getMessage());
            }
        }

        var config = getConfig();
        var features = FeatureList.getFeatures(this);
        var pluginManager = getServer().getPluginManager();

        for (var feature : features) {
            var annotation = feature.getClass().getAnnotation(Feature.class);
            if (annotation == null) continue;

            var path = annotation.name();
            if (config.getBoolean("features." + path + ".enabled", false)) {
                injectConfigValues(feature);
                pluginManager.registerEvents(feature, this);
                logger.info(Component.text(path + " enabled", NamedTextColor.GREEN));
            } else {
                logger.info(Component.text(path + " disabled", NamedTextColor.RED));
            }
        }
    }

    private void updateConfig() throws IOException {
        var currentConfig = getConfig();
        var defaultConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(Objects.requireNonNull(getResource("config.yml")))
        );

        for (String key : defaultConfig.getKeys(true)) {
            if (key.startsWith("config")) continue;
            if (currentConfig.contains(key)) {
                defaultConfig.set(key, currentConfig.get(key));
                defaultConfig.setComments(key, currentConfig.getComments(key));
            }
        }

        defaultConfig.save(new File(getDataFolder(), "config.yml"));
    }

    private void injectConfigValues(Listener feature) {
        if (!feature.getClass().isAnnotationPresent(Feature.class)) return;
        var featurePath = feature.getClass().getAnnotation(Feature.class).name();

        var config = getConfig();
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
                getLogger().warning("failed to inject config value for " + field.getName());
            }
        }
    }
}
