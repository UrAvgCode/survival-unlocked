package com.uravgcode.survivalunlocked.feature.transferyourpets;

import com.uravgcode.survivalunlocked.feature.Feature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

@Feature(name = "transfer-your-pets")
public class PetTransferListener implements Listener {

    @EventHandler
    public void onPetTransfer(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player targetPlayer)) return;

        var player = event.getPlayer();
        if (!player.isSneaking()) return;

        for (var entity : player.getNearbyEntities(16, 16, 16)) {
            if (!(entity instanceof Tameable pet) || !pet.isLeashed()) continue;
            if (!player.getUniqueId().equals(pet.getLeashHolder().getUniqueId())) continue;
            if (!player.getUniqueId().equals(pet.getOwnerUniqueId())) continue;

            pet.setOwner(targetPlayer);
            pet.setLeashHolder(targetPlayer);
        }
    }
}
