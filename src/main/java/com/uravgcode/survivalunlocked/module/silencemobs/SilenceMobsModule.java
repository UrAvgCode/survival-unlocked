package com.uravgcode.survivalunlocked.module.silencemobs;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

@ConfigModule(path = "silence-mobs")
public final class SilenceMobsModule extends PluginModule {

    public SilenceMobsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof final Mob mob)) return;

        final var player = event.getPlayer();
        final var item = player.getInventory().getItem(event.getHand());
        if (item.getType() != Material.NAME_TAG) return;

        final var name = item.getData(DataComponentTypes.CUSTOM_NAME);
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

    private @NotNull String getMobName(@NotNull Mob entity) {
        return Arrays.stream(entity.getType().name().toLowerCase().split("_"))
            .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
            .collect(Collectors.joining(" "));
    }
}
