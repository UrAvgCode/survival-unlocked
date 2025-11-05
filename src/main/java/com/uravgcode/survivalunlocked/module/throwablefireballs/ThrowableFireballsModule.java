package com.uravgcode.survivalunlocked.module.throwablefireballs;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "throwable-fireballs")
public final class ThrowableFireballsModule extends PluginModule {

    public ThrowableFireballsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        var player = event.getPlayer();
        var hand = event.getHand();
        if (hand == null) return;

        var item = player.getInventory().getItem(hand);
        if (item.getType() != Material.FIRE_CHARGE) return;

        player.swingHand(hand);
        player.getWorld().playSound(
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
