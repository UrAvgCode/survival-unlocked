package com.uravgcode.survivalunlocked.module;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PluginModule implements Listener {
    protected final JavaPlugin plugin;

    protected PluginModule(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
