package com.uravgcode.survivalunlocked.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FireballThrowListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.FIRE_CHARGE) return;

        event.getPlayer().playSound(
            player.getEyeLocation(),
            Sound.ITEM_FIRECHARGE_USE,
            1, 1f
        );

        var fireball = player.getWorld().spawn(player.getEyeLocation(), SmallFireball.class);
        fireball.setDirection(player.getEyeLocation().getDirection());
        fireball.setShooter(player);

        if (player.getGameMode() != GameMode.CREATIVE) item.subtract();
        event.setCancelled(true);
    }
}
