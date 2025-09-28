package com.uravgcode.survivalunlocked.feature.keepbabyanimals;

import com.uravgcode.survivalunlocked.annotation.Feature;
import org.bukkit.*;
import org.bukkit.entity.Breedable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Feature(name = "keep-baby-animals")
public class KeepBabyAnimalsListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Breedable breedable)) return;
        if (breedable.isAdult()) return;
        if (breedable.getAgeLock()) return;

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.POISONOUS_POTATO) return;

        breedable.playHurtAnimation(0);
        breedable.getWorld().spawnParticle(
            Particle.ENTITY_EFFECT,
            breedable.getEyeLocation(),
            8,
            0.15, 0.15, 0.15,
            0.0,
            Color.fromRGB(0x4E9331)
        );

        player.playSound(
            breedable.getLocation(),
            Sound.ENTITY_DONKEY_EAT,
            1f, 1f
        );

        breedable.setAgeLock(true);
        if (player.getGameMode() != GameMode.CREATIVE) item.subtract();
    }
}
