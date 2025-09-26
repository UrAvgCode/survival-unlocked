package com.uravgcode.survivalunlocked.feature.playerheaddrops;

import com.uravgcode.survivalunlocked.annotation.Feature;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@Feature(name = "player-head-drops")
public class PlayerHeadDropListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        if (player.getKiller() == null) return;

        var head = new ItemStack(Material.PLAYER_HEAD, 1);
        if (!(head.getItemMeta() instanceof SkullMeta meta)) return;

        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        event.getDrops().add(head);
    }
}
