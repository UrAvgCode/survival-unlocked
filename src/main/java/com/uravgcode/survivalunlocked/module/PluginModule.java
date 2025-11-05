package com.uravgcode.survivalunlocked.module;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class PluginModule implements Listener {
    protected final String name;
    protected final JavaPlugin plugin;
    protected boolean enabled;

    protected PluginModule(@NotNull JavaPlugin plugin) {
        this.name = getClass().getAnnotation(ConfigModule.class).path();
        this.plugin = plugin;
        this.enabled = false;
    }

    public void reload() {
        final var config = plugin.getConfig();
        final var logger = plugin.getComponentLogger();

        for (final var field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigValue.class)) continue;
            final var annotation = field.getAnnotation(ConfigValue.class);
            final var path = "modules." + name + "." + annotation.path();

            try {
                field.setAccessible(true);
                final var value = config.get(path, field.get(this));
                field.set(this, value);
            } catch (Exception exception) {
                logger.warn("failed to inject config value for {}: {}", field.getName(), exception.getMessage());
            } finally {
                field.setAccessible(false);
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
