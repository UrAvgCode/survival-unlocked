package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import com.uravgcode.survivalunlocked.module.PluginModules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
                updateResource("config.yml");
                updateResource("heads.yml");
                reloadConfig();
                logger.info("updating config to version {}", pluginVersion);
            } catch (IOException exception) {
                logger.warn("failed to update config: {}", exception.getMessage());
            }
        }

        var config = getConfig();
        var modules = PluginModules.modules(this);
        var pluginManager = getServer().getPluginManager();

        for (var module : modules) {
            var annotation = module.getClass().getAnnotation(ModuleMeta.class);
            if (annotation == null) continue;

            var path = annotation.name();
            if (config.getBoolean("modules." + path + ".enabled", false)) {
                injectConfigValues(module);
                pluginManager.registerEvents(module, this);
                logger.info(Component.text(path + " enabled", NamedTextColor.GREEN));
            } else {
                logger.info(Component.text(path + " disabled", NamedTextColor.RED));
            }
        }
    }

    private void updateResource(String resourceName) throws IOException {
        var dataFolder = getDataFolder().toPath();
        var resourcePath = dataFolder.resolve(resourceName);
        var backupPath = dataFolder.resolve(resourceName.replace(".yml", ".old.yml"));

        Files.move(resourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        saveResource(resourceName, false);
    }

    private void injectConfigValues(PluginModule module) {
        if (!module.getClass().isAnnotationPresent(ModuleMeta.class)) return;
        var modulePath = module.getClass().getAnnotation(ModuleMeta.class).name();

        var config = getConfig();
        for (var field : module.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigValue.class)) continue;
            var annotation = field.getAnnotation(ConfigValue.class);
            var path = "modules." + modulePath + "." + annotation.name();
            if (!config.contains(path)) continue;

            try {
                field.setAccessible(true);
                var value = config.get(path);
                field.set(module, value);
            } catch (IllegalAccessException e) {
                getLogger().warning("failed to inject config value for " + field.getName());
            }
        }
    }
}
