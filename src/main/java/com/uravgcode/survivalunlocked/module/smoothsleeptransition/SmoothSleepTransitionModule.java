package com.uravgcode.survivalunlocked.module.smoothsleeptransition;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "smooth-sleep-transition")
public final class SmoothSleepTransitionModule extends PluginModule {

    @ConfigValue(path = "time-rate")
    private long timeRate = 120;

    public SmoothSleepTransitionModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() != TimeSkipEvent.SkipReason.NIGHT_SKIP) return;

        final long skipAmount = event.getSkipAmount();
        for (final var player : plugin.getServer().getOnlinePlayers()) {

            final long[] remaining = {skipAmount};
            player.getScheduler().runAtFixedRate(plugin, task -> {
                remaining[0] = Math.max(0, remaining[0] - timeRate);
                player.setPlayerTime(-remaining[0], true);

                if (remaining[0] <= 0) {
                    task.cancel();
                }
            }, null, 1L, 1L);
        }
    }
}
