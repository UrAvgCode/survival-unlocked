package com.uravgcode.survivalunlocked.feature.bettertridents;

import com.uravgcode.survivalunlocked.annotation.Feature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Feature(name = "better-tridents")
public class TridentListener implements Listener {
    private final JavaPlugin plugin;

    public TridentListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTridentThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player)) return;
        if (trident.getLoyaltyLevel() == 0) return;

        trident.getScheduler().runAtFixedRate(plugin, task -> {
            if (trident.getLocation().getY() < trident.getWorld().getMinHeight() - 20) {
                trident.setHasDealtDamage(true);
            }

            if (trident.hasDealtDamage()) {
                task.cancel();
            }
        }, null, 1L, 1L);
    }
}
