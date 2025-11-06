package com.uravgcode.survivalunlocked.module.coordinatehud;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.event.CoordinateHudEvent;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ConfigModule(path = "coordinate-hud")
public final class CoordinateHudModule extends PluginModule {
    private final List<Player> players;
    private final NamespacedKey hudKey;
    private final MiniMessage miniMessage;
    private ScheduledTask task = null;

    @ConfigValue(path = "format")
    private String format = "<x>,<y>,<z>  <yellow><direction>";

    @ConfigValue(path = "refresh-interval")
    private long refreshInterval = 2L;

    public CoordinateHudModule(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.players = new ArrayList<>();
        this.hudKey = new NamespacedKey(plugin, "coordinate_hud");
        this.miniMessage = MiniMessage.builder().tags(TagResolver.resolver(
            TagResolver.standard(),
            positionPlaceholders()
        )).build();
    }

    @Override
    public void enable() {
        super.enable();
        if (task == null) {
            final var scheduler = plugin.getServer().getGlobalRegionScheduler();
            task = scheduler.runAtFixedRate(plugin, this::updateCoordinateHud, 1L, refreshInterval);
        }
    }

    @Override
    public void disable() {
        super.disable();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @EventHandler
    public void onCoordinateHud(CoordinateHudEvent event) {
        final var player = event.getPlayer();
        final var dataContainer = player.getPersistentDataContainer();

        if (dataContainer.has(hudKey)) {
            dataContainer.remove(hudKey);
            players.remove(player);
        } else {
            dataContainer.set(hudKey, PersistentDataType.BYTE, (byte) 1);
            players.add(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getPersistentDataContainer().has(hudKey)) {
            players.add(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    private void updateCoordinateHud(@Nullable ScheduledTask ignored) {
        for (final var player : players) {
            player.sendActionBar(miniMessage.deserialize(format, player));
        }
    }

    private @NotNull TagResolver positionPlaceholders() {
        return TagResolver.builder()
            .tag("x", (arguments, context) -> {
                final var player = context.targetAsType(Player.class);
                final var x = player.getLocation().getBlockX();
                return Tag.selfClosingInserting(Component.text(x));
            })
            .tag("y", (arguments, context) -> {
                final var player = context.targetAsType(Player.class);
                final var y = player.getLocation().getBlockY();
                return Tag.selfClosingInserting(Component.text(y));
            })
            .tag("z", (arguments, context) -> {
                final var player = context.targetAsType(Player.class);
                final var z = player.getLocation().getBlockZ();
                return Tag.selfClosingInserting(Component.text(z));
            })
            .tag("direction", (arguments, context) -> {
                final var player = context.targetAsType(Player.class);
                final var yaw = player.getLocation().getYaw();

                var direction = "?";
                if ((yaw >= -22.5 && yaw < 22.5) || (yaw >= 337.5 || yaw < -337.5)) direction = "S";
                else if (yaw >= 22.5 && yaw < 67.5) direction = "SW";
                else if (yaw >= 67.5 && yaw < 112.5) direction = "W";
                else if (yaw >= 112.5 && yaw < 157.5) direction = "NW";
                else if ((yaw >= 157.5 && yaw <= 180) || (yaw >= -180 && yaw < -157.5)) direction = "N";
                else if (yaw >= -157.5 && yaw < -112.5) direction = "NE";
                else if (yaw >= -112.5 && yaw < -67.5) direction = "E";
                else if (yaw >= -67.5 && yaw < -22.5) direction = "SE";

                return Tag.selfClosingInserting(Component.text(direction));
            })
            .build();
    }
}
