package com.uravgcode.survivalunlocked.feature.keepbabyanimals;

import com.uravgcode.survivalunlocked.feature.Feature;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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

        breedable.setAgeLock(true);
        player.playSound(breedable.getLocation(), Sound.ENTITY_DONKEY_EAT, 1f, 1f);
        if (player.getGameMode() != GameMode.CREATIVE) item.subtract();
    }
}
