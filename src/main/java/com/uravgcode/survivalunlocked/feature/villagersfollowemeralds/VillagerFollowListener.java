package com.uravgcode.survivalunlocked.feature.villagersfollowemeralds;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.Feature;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Feature(name = "villagers-follow-emeralds")
public class VillagerFollowListener implements Listener {

    @ConfigValue(name = "follow-radius")
    private double followRadius = 9.0;

    @ConfigValue(name = "follow-speed")
    private double followSpeed = 0.6;

    @ConfigValue(name = "min-follow-distance")
    private double minFollowDistance = 2.0;

    private final JavaPlugin plugin;
    private final Map<Player, ScheduledTask> followTasks;

    public VillagerFollowListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.followTasks = new HashMap<>();
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        var player = event.getPlayer();
        var slot = event.getNewSlot();
        var mainHand = player.getInventory().getItem(slot);
        var offHand = player.getInventory().getItemInOffHand();

        if ((mainHand != null && mainHand.getType() == Material.EMERALD_BLOCK) ||
            (offHand.getType() == Material.EMERALD_BLOCK)) {
            startFollowTask(player);
        } else {
            stopFollowTask(player);
        }
    }

    @EventHandler
    public void onPlayerInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        var player = event.getPlayer();
        var mainHand = player.getInventory().getItemInMainHand();
        var offHand = player.getInventory().getItemInOffHand();

        if ((mainHand.getType() == Material.EMERALD_BLOCK) ||
            (offHand.getType() == Material.EMERALD_BLOCK)) {
            startFollowTask(player);
        } else {
            stopFollowTask(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stopFollowTask(event.getPlayer());
    }

    private void startFollowTask(Player player) {
        if (followTasks.containsKey(player)) return;

        var followTask = player.getScheduler().runAtFixedRate(plugin, task -> {
            for (Entity entity : player.getNearbyEntities(followRadius, followRadius, followRadius)) {
                if (entity instanceof Villager villager && !villager.isSleeping()) {
                    var pathfinder = villager.getPathfinder();

                    var distance = villager.getLocation().distance(player.getLocation());
                    if (distance > minFollowDistance) {
                        pathfinder.moveTo(player, followSpeed);
                    } else {
                        pathfinder.stopPathfinding();
                    }
                }
            }
        }, null, 1L, 10L);

        followTasks.put(player, followTask);
    }

    private void stopFollowTask(Player player) {
        var followTask = followTasks.remove(player);
        if (followTask != null) followTask.cancel();
    }
}
