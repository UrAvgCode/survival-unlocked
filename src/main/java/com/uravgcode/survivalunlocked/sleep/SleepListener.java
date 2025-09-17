package com.uravgcode.survivalunlocked.sleep;

import org.bukkit.GameRule;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SleepListener implements Listener {
    private static final int TIME_RATE = 70;
    private static final int DAY_START = 1200;

    private final JavaPlugin plugin;
    private boolean skippingNight;

    public SleepListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.skippingNight = false;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;
        if (skippingNight) return;

        var sleepingPlayer = event.getPlayer();
        var world = sleepingPlayer.getWorld();
        var gameRuleValue = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        double sleepingPercentage = Objects.requireNonNullElse(gameRuleValue, 100) / 100.0;

        long totalPlayers = world.getPlayers().stream().filter(player -> !player.isSleepingIgnored()).count();
        long sleepingPlayers = world.getPlayers().stream().filter(player -> player.isSleeping() || player.equals(sleepingPlayer)).count();
        if (sleepingPlayers < totalPlayers * sleepingPercentage) return;

        skippingNight = true;
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            long time = world.getTime();

            if (time >= (DAY_START - TIME_RATE * 1.5) && time <= DAY_START) {
                world.setTime(DAY_START);
                if (world.hasStorm()) world.setStorm(false);
                if (world.isThundering()) world.setThundering(false);

                world.getPlayers().forEach(player -> {
                    player.setStatistic(Statistic.TIME_SINCE_REST, 0);
                    if (player.isSleeping()) player.wakeup(false);
                });

                skippingNight = false;
                task.cancel();
                return;
            }

            world.setTime(time + TIME_RATE);
        }, 1L, 1L);
    }
}
