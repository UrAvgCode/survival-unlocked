package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.command.PluginCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings({"unused", "UnstableApiUsage"})
public final class SurvivalUnlockedBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(PluginCommand.build());
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new SurvivalUnlocked();
    }
}
