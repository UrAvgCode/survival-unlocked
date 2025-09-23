package com.uravgcode.survivalunlocked.feature.smoothsleeptransition;

import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class SleepListener implements Listener {
    private static final long DAY_LENGTH = 24000;
    private static final long TIME_RATE = 120;

    private final JavaPlugin plugin;
    private final Map<World, ScheduledTask> nightSkipTasks;

    public SleepListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.nightSkipTasks = new WeakHashMap<>();
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

    private void updateNightSkipState(World world) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
            boolean isNightSkipping = nightSkipTasks.containsKey(world);
            boolean sleepingPercentageMet = playersSleepingPercentageMet(world);

            if (!isNightSkipping && sleepingPercentageMet) {
                startNightSkip(world);
            } else if (isNightSkipping && !sleepingPercentageMet) {
                stopNightSkip(world);
            }
        }, 1L);
    }

    private boolean playersSleepingPercentageMet(World world) {
        var gameRuleValue = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        double sleepingPercentage = Objects.requireNonNullElse(gameRuleValue, 100) / 100.0;

        int total = 0;
        int sleeping = 0;
        for (var player : world.getPlayers()) {
            if (player.isSleepingIgnored()) continue;
            if (player.isSleeping()) sleeping++;
            total++;
        }

        return sleeping >= total * sleepingPercentage;
    }

    private void startNightSkip(World world) {
        var nightSkipTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            long time = world.getTime();

            if (time <= DAY_LENGTH && time + TIME_RATE >= DAY_LENGTH) {
                world.setTime(0);
                stopNightSkip(world);
                return;
            }

            world.setTime(time + TIME_RATE);
        }, 1L, 1L);

        nightSkipTasks.put(world, nightSkipTask);
    }

    private void stopNightSkip(World world) {
        var task = nightSkipTasks.remove(world);
        if (task != null) task.cancel();
    }
}
