package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.manager.ModuleManager;
import com.uravgcode.survivalunlocked.update.ConfigUpdater;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.util.Objects;

public final class SurvivalUnlocked extends JavaPlugin {
    private static SurvivalUnlocked instance = null;

    private ConfigUpdater configUpdater = null;
    private ModuleManager moduleManager = null;

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
        if (!Files.exists(getDataPath().resolve("heads.yml"))) {
            saveResource("heads.yml", false);
        }

        configUpdater = new ConfigUpdater(this);
        moduleManager = new ModuleManager(this);
        reload();
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        configUpdater.updateConfigs();
        moduleManager.reloadModules();
    }
}
