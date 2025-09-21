package com.uravgcode.survivalunlocked.feature.villagersfollowemeralds;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class VillagerFollowListener implements Listener {
    private static final double MIN_FOLLOW_DISTANCE = 2.0;
    private final Set<Player> players = new HashSet<>();

    public VillagerFollowListener(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : players) {
                if (!player.isConnected()) {
                    players.remove(player);
                    continue;
                }

                for (org.bukkit.entity.Entity entity : player.getNearbyEntities(10, 5, 10)) {
                    if (entity.getType() == EntityType.VILLAGER) {
                        var villager = (Villager) entity;
                        var pathfinder = villager.getPathfinder();

                        var distance = villager.getLocation().distance(player.getLocation());
                        if (distance > MIN_FOLLOW_DISTANCE) {
                            pathfinder.moveTo(player, 0.6);
                        } else {
                            pathfinder.stopPathfinding();
                        }
                    }
                }
            }
        }, 0L, 10L);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItem(event.getNewSlot());

        if (item != null && item.getType() == Material.EMERALD_BLOCK) {
            players.add(player);
        } else {
            players.remove(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }
}
