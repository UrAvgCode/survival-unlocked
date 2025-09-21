package com.uravgcode.survivalunlocked.feature.invisibleitemframes;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ItemFrameListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame)) return;

        var player = event.getPlayer();
        var handItem = player.getInventory().getItem(event.getHand());
        if (handItem.getType() != Material.SHEARS) return;

        var frameItem = frame.getItem();
        if (frameItem.getType() == Material.AIR || !frame.isVisible()) return;

        frame.getWorld().spawnParticle(
            Particle.CRIT,
            frame.getLocation().add(0, 0.5, 0),
            5, 0.2, 0.2, 0.2,
            0.05
        );

        event.getPlayer().playSound(
            frame.getLocation(),
            Sound.BLOCK_BEEHIVE_SHEAR,
            1, 1f
        );

        frame.setVisible(false);
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame frame) {
            frame.setVisible(true);
        }
    }
}
