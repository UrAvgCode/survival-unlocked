package com.uravgcode.survivalunlocked.module.callyourpets;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@ModuleMeta(name = "call-your-pets")
public class CallYourPetsModule extends PluginModule {

    @ConfigValue(name = "call-radius")
    private double callRadius = 64.0;

    @EventHandler
    public void onHornBlow(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = event.getPlayer();
        var hand = event.getHand();
        if (hand == null) return;

        var item = player.getInventory().getItem(hand);
        if (item.getType() != Material.GOAT_HORN) return;
        if (player.hasCooldown(item)) return;

        for (var entity : player.getNearbyEntities(callRadius, callRadius, callRadius)) {
            if (!(entity instanceof Tameable pet)) continue;
            if (pet instanceof Sittable sittable && sittable.isSitting()) continue;
            if (!player.getUniqueId().equals(pet.getOwnerUniqueId())) continue;

            final double speed = switch (pet) {
                case AbstractHorse ignored -> 2.0;
                default -> 1.2;
            };

            pet.getPathfinder().moveTo(player, speed);
        }
    }
}
