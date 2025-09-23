package com.uravgcode.survivalunlocked.feature.silencemobs;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SilenceMobListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Mob mob)) return;

        var player = event.getPlayer();
        var item = player.getInventory().getItem(event.getHand());
        if (item.getType() != Material.NAME_TAG) return;

        var name = item.getItemMeta().customName();
        if (Component.text("silence").equals(name)) {
            if (player.getGameMode() != GameMode.CREATIVE) item.subtract();
            player.sendActionBar(Component.text(getMobName(mob) + " silenced"));
            mob.setSilent(true);
            event.setCancelled(true);
        } else if (Component.text("unsilence").equals(name)) {
            if (player.getGameMode() != GameMode.CREATIVE) item.subtract();
            player.sendActionBar(Component.text(getMobName(mob) + " unsilenced"));
            mob.setSilent(false);
            event.setCancelled(true);
        }
    }

    private String getMobName(Mob entity) {
        return Arrays.stream(entity.getType().name().toLowerCase().split("_"))
            .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
            .collect(Collectors.joining(" "));
    }
}
