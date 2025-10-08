package com.uravgcode.survivalunlocked.module;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PluginModule implements Listener {
    private boolean enabled = false;

    protected final String name;
    protected final JavaPlugin plugin;

    protected PluginModule(@NotNull JavaPlugin plugin) {
        this.name = getClass().getAnnotation(ModuleMeta.class).name();
        this.plugin = plugin;
    }

    public void reload() {
        var config = plugin.getConfig();
        var logger = plugin.getComponentLogger();

        for (var field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigValue.class)) continue;
            var annotation = field.getAnnotation(ConfigValue.class);
            var path = "modules." + name + "." + annotation.name();
            if (!config.contains(path)) continue;

            try {
                field.setAccessible(true);
                var value = config.get(path);
                field.set(this, value);
            } catch (IllegalAccessException ignored) {
                logger.warn("failed to inject config value for {}", field.getName());
            }
        }

        if (config.getBoolean("modules." + name + ".enabled", false)) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        plugin.getComponentLogger().info(Component.text(name + " enabled", NamedTextColor.GREEN));
        if (!enabled) {
            enabled = true;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public void disable() {
        plugin.getComponentLogger().info(Component.text(name + " disabled", NamedTextColor.RED));
        if (enabled) {
            enabled = false;
            HandlerList.unregisterAll(this);
        }
    }
}
