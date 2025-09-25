package com.uravgcode.survivalunlocked.feature.callyourpets;

import com.uravgcode.survivalunlocked.feature.Feature;
import org.bukkit.Material;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Feature(name = "call-your-pets")
public class PetCallListener implements Listener {

    @EventHandler
    public void onHornBlow(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = event.getPlayer();
        var hand = event.getHand();
        if (hand == null) return;

        var item = player.getInventory().getItem(hand);
        if (item.getType() != Material.GOAT_HORN) return;
        if (player.hasCooldown(item)) return;

        for (var entity : player.getNearbyEntities(64, 64, 64)) {
            if (!(entity instanceof Tameable pet)) continue;
            if (pet instanceof Sittable sittable && sittable.isSitting()) continue;
            if (!player.getUniqueId().equals(pet.getOwnerUniqueId())) continue;
            pet.getPathfinder().moveTo(player, 1.2);
        }
    }
}
