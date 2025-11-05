package com.uravgcode.survivalunlocked.module.smoothsleeptransition;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ConfigModule(path = "smooth-sleep-transition")
public final class SmoothSleepTransitionModule extends PluginModule {
    private static final long DAY_LENGTH = 24000;

    @ConfigValue(path = "time-rate")
    private long timeRate = 120;

    private final Map<World, ScheduledTask> nightSkipTasks;

    public SmoothSleepTransitionModule(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.nightSkipTasks = new HashMap<>();
    }

    @EventHandler
    public void onPlayerSleep(PlayerDeepSleepEvent event) {
        updateNightSkipState(event.getPlayer().getWorld());
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        updateNightSkipState(event.getPlayer().getWorld());
    }

    @EventHandler
    public static void onTimeSkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            event.setCancelled(true);
        }
    }

    private void updateNightSkipState(@NotNull World world) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
            final boolean isNightSkipping = nightSkipTasks.containsKey(world);
            final boolean sleepingPercentageMet = playersSleepingPercentageMet(world);

            if (!isNightSkipping && sleepingPercentageMet) {
                startNightSkip(world);
            } else if (isNightSkipping && !sleepingPercentageMet) {
                stopNightSkip(world);
            }
        }, 1L);
    }

    private boolean playersSleepingPercentageMet(@NotNull World world) {
        final var gameRuleValue = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        final double sleepingPercentage = Objects.requireNonNullElse(gameRuleValue, 100) / 100.0;

        int total = 0;
        int sleeping = 0;
        for (final var player : world.getPlayers()) {
            if (player.isSleepingIgnored()) continue;
            if (player.isSleeping()) sleeping++;
            total++;
        }

        return sleeping >= total * sleepingPercentage;
    }

    private void startNightSkip(@NotNull World world) {
        final var nightSkipTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            final long time = world.getTime();

            if (time <= DAY_LENGTH && time + timeRate >= DAY_LENGTH) {
                world.setTime(0);
                stopNightSkip(world);
                return;
            }

            world.setTime(time + timeRate);
        }, 1L, 1L);

        nightSkipTasks.put(world, nightSkipTask);
    }

    private void stopNightSkip(@NotNull World world) {
        final var task = nightSkipTasks.remove(world);
        if (task != null) task.cancel();
    }
}
