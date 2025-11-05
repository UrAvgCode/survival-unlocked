package com.uravgcode.survivalunlocked.module.bettertridents;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "better-tridents")
public final class BetterTridentsModule extends PluginModule {

    public BetterTridentsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
