package com.uravgcode.survivalunlocked.update;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public final class ConfigUpdater {
    private final JavaPlugin plugin;

    public ConfigUpdater(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateConfigs() {
        final var pluginVersion = plugin.getPluginMeta().getVersion();
        final var configVersion = plugin.getConfig().getString("config.version", "0.0.0");
        if (!pluginVersion.equals(configVersion)) {
            updateResource("config.yml");
            updateResource("heads.yml");
            plugin.reloadConfig();
        }
    }

    private void updateResource(@NotNull String filename) {
        final var logger = plugin.getComponentLogger();
        final var dataPath = plugin.getDataPath();
        final var configPath = dataPath.resolve(filename);
        final var backupPath = dataPath.resolve("backups").resolve(filename);

        try (final var resourceStream = Objects.requireNonNull(plugin.getResource(filename))) {
            final var currentConfig = YamlConfiguration.loadConfiguration(configPath.toFile());
            final var updatedConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resourceStream));

            for (final var key : currentConfig.getKeys(true)) {
                if (key.startsWith("config")) continue;

                final var value = currentConfig.get(key);
                if (value == null || value instanceof ConfigurationSection) continue;

                if (updatedConfig.contains(key) && !updatedConfig.isConfigurationSection(key)) {
                    updatedConfig.set(key, value);
                }
            }

            Files.createDirectories(backupPath.getParent());
            Files.move(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            updatedConfig.save(configPath.toFile());
            logger.info("successfully updated {}", filename);
        } catch (IOException exception) {
            logger.warn("failed to update {}: {}", filename, exception.getMessage());
        }
    }
}
