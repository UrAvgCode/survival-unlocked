package com.uravgcode.survivalunlocked.manager;

import com.google.common.reflect.ClassPath;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private final List<@NotNull PluginModule> modules;

    public ModuleManager(@NotNull JavaPlugin plugin) {
        final var logger = plugin.getComponentLogger();

        modules = new ArrayList<>();
        for (final var clazz : discoverModules()) {
            try {
                final var constructor = clazz.getConstructor(JavaPlugin.class);
                final var module = constructor.newInstance(plugin);
                modules.add(module);
            } catch (Exception exception) {
                logger.warn("Failed to initialize {}: {}", clazz.getSimpleName(), exception.getMessage());
            }
        }

        logger.info("Initialized {} modules", modules.size());
    }

    public void reloadModules() {
        modules.forEach(PluginModule::reload);
    }

    public static List<@NotNull Class<? extends PluginModule>> discoverModules() {
        try {
            final var moduleClasses = new ArrayList<Class<? extends PluginModule>>();
            final var classLoader = ModuleManager.class.getClassLoader();
            final var classPath = ClassPath.from(classLoader);

            final var packageName = "com.uravgcode.survivalunlocked.module";
            for (final var classInfo : classPath.getTopLevelClassesRecursive(packageName)) {
                final var clazz = classInfo.load();

                if (!PluginModule.class.isAssignableFrom(clazz)) continue;
                if (Modifier.isAbstract(clazz.getModifiers())) continue;
                if (clazz.isInterface()) continue;

                final var moduleClass = clazz.asSubclass(PluginModule.class);
                moduleClasses.add(moduleClass);
            }

            return moduleClasses;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
