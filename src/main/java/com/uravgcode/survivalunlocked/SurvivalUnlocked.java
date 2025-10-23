package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.module.PluginModule;
import com.uravgcode.survivalunlocked.module.PluginModules;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SurvivalUnlocked extends JavaPlugin {
    private static SurvivalUnlocked instance = null;

    private List<PluginModule> modules = Collections.emptyList();

    public static @NotNull SurvivalUnlocked instance() {
        return Objects.requireNonNull(instance, "plugin not initialized");
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!Files.exists(getDataFolder().toPath().resolve("heads.yml"))) {
            saveResource("heads.yml", false);
        }

        var pluginVersion = getPluginMeta().getVersion();
        var configVersion = getConfig().getString("config.version", "0.0.0");
        if (!pluginVersion.equals(configVersion)) {
            updateConfig("config.yml");
            updateConfig("heads.yml");
        }

        modules = PluginModules.modules(this);
        reload();
    }

    public void reload() {
        reloadConfig();
        for (var module : modules) {
            module.reload();
        }
    }

    private void updateConfig(@NotNull String filename) {
        final var logger = getComponentLogger();
        final var dataFolder = getDataFolder().toPath();
        final var configPath = dataFolder.resolve(filename);
        final var backupPath = dataFolder.resolve(filename.replace(".yml", ".old.yml"));

        try (var configStream = getResource(filename)) {
            if (configStream == null) {
                logger.warn("couldn't find resource {}", filename);
                return;
            }

            var currentConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
            var updatedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(configStream));

            for (var key : currentConfig.getKeys(true)) {
                if (key.startsWith("config")) continue;

                var updatedKey = key.replace("features", "modules");
                if (currentConfig.getConfigurationSection(key) != null || updatedConfig.getConfigurationSection(updatedKey) != null) {
                    continue;
                }

                if (updatedConfig.contains(updatedKey)) {
                    updatedConfig.set(updatedKey, currentConfig.get(key));
                }
            }

            Files.move(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            updatedConfig.save(configPath.toFile());
            logger.info("successfully updated {}", filename);
        } catch (IOException exception) {
            logger.warn("failed to update {}: {}", filename, exception.getMessage());
        }
    }
}
