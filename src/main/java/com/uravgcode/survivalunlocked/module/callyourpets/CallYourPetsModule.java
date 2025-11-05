package com.uravgcode.survivalunlocked.module.callyourpets;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "call-your-pets")
public final class CallYourPetsModule extends PluginModule {

    @ConfigValue(path = "call-radius")
    private double callRadius = 64.0;

    @ConfigValue(path = "stop-distance")
    private double stopDistance = 4.0;

    @ConfigValue(path = "follow-time")
    private long followTime = 100L;

    public CallYourPetsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onHornBlow(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final var player = event.getPlayer();
        final var hand = event.getHand();
        if (hand == null) return;

        final var item = player.getInventory().getItem(hand);
        if (item.getType() != Material.GOAT_HORN) return;
        if (player.hasCooldown(item)) return;

        final var location = player.getLocation();
        for (final var entity : player.getNearbyEntities(callRadius, callRadius, callRadius)) {
            if (!(entity instanceof final Tameable pet)) continue;
            if (pet instanceof final Sittable sittable && sittable.isSitting()) continue;
            if (!player.getUniqueId().equals(pet.getOwnerUniqueId())) continue;

            final double speed = pet instanceof AbstractHorse ? 1.8 : 1.2;
            final var followTask = pet.getScheduler().runAtFixedRate(plugin, task -> {
                if (pet.getLocation().distanceSquared(location) <= stopDistance * stopDistance) {
                    task.cancel();
                } else {
                    if (pet instanceof AbstractHorse horse) horse.setEatingGrass(false);
                    pet.getPathfinder().moveTo(location, speed);
                }
            }, null, 1L, 10L);

            pet.getScheduler().runDelayed(plugin, task -> {
                if (followTask != null) followTask.cancel();
            }, null, followTime);
        }
    }
}
