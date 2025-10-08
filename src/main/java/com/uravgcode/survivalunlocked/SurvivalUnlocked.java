package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.module.PluginModule;
import com.uravgcode.survivalunlocked.module.PluginModules;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

public final class SurvivalUnlocked extends JavaPlugin {
    private static SurvivalUnlocked plugin = null;

    private List<PluginModule> modules = Collections.emptyList();

    public static SurvivalUnlocked plugin() {
        return plugin;
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

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
                updateResource("config.yml");
                updateResource("heads.yml");
                logger.info("updating config to version {}", pluginVersion);
            } catch (IOException exception) {
                logger.warn("failed to update config: {}", exception.getMessage());
            }
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

    private void updateResource(String resourceName) throws IOException {
        var dataFolder = getDataFolder().toPath();
        var resourcePath = dataFolder.resolve(resourceName);
        var backupPath = dataFolder.resolve(resourceName.replace(".yml", ".old.yml"));

        Files.move(resourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        saveResource(resourceName, false);
    }
}
