package com.uravgcode.survivalunlocked.module.customshapedportals;

import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Orientable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ModuleMeta(name = "custom-shaped-portals")
public final class CustomShapedPortalsModule extends PluginModule {

    @ConfigValue(name = "minimum-portal-size")
    private int minPortalSize = 6;

    @ConfigValue(name = "maximum-portal-width")
    private int maxWidth = 21;

    @ConfigValue(name = "maximum-portal-height")
    private int maxHeight = 21;

    private final Set<Material> portalBlocks = Set.of(
        Material.OBSIDIAN,
        Material.CRYING_OBSIDIAN
    );

    public CustomShapedPortalsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        final var fireBlock = event.getBlock();
        if (!portalBlocks.contains(fireBlock.getRelative(BlockFace.DOWN).getType())) return;

        final var world = fireBlock.getWorld();
        if (world.getEnvironment() == World.Environment.THE_END) return;

        final var entity = event.getIgnitingEntity();
        final var location = fireBlock.getLocation();

        for (final var axis : new Axis[]{Axis.X, Axis.Z}) {
            final var portalBlocks = getPortalBlocks(fireBlock, axis);
            if (portalBlocks.isPresent()) {
                buildPortal(portalBlocks.get(), axis, world, entity, location);
                break;
            }
        }
    }

    private @NotNull Optional<Set<Block>> getPortalBlocks(@NotNull Block fireBlock, @NotNull Axis axis) {
        final var visited = new HashSet<>(Collections.singletonList(fireBlock));
        final var queue = new ArrayDeque<>(Collections.singletonList(fireBlock));

        final var directions = (axis == Axis.Z)
            ? new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH}
            : new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST};

        final var bounds = new BoundingBox(
            fireBlock.getX(), fireBlock.getY(), fireBlock.getZ(),
            fireBlock.getX(), fireBlock.getY(), fireBlock.getZ()
        );

        while (!queue.isEmpty()) {
            final var current = queue.poll();
            for (final var direction : directions) {
                final var neighbor = current.getRelative(direction);
                if (visited.contains(neighbor)) continue;

                final var material = neighbor.getType();
                if (!material.equals(Material.AIR) && !material.equals(Material.FIRE)) {
                    if (!portalBlocks.contains(material)) return Optional.empty();
                    continue;
                }

                visited.add(neighbor);
                queue.add(neighbor);

                bounds.union(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                final var width = (axis == Axis.X) ? bounds.getWidthX() : bounds.getWidthZ();
                final var height = bounds.getHeight();

                if (width >= maxWidth || height >= maxHeight) return Optional.empty();
            }
        }

        return (visited.size() < minPortalSize) ? Optional.empty() : Optional.of(visited);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void buildPortal(@NotNull Set<Block> blocks, @NotNull Axis axis, @NotNull World world, @Nullable Entity entity, @NotNull Location location) {
        final var server = plugin.getServer();
        server.getRegionScheduler().execute(plugin, location, () -> {
            final var portalData = (Orientable) Material.NETHER_PORTAL.createBlockData();
            portalData.setAxis(axis);

            final var blockStates = new ArrayList<BlockState>(blocks.size());
            for (final var block : blocks) {
                final var state = block.getState();
                state.setBlockData(portalData);
                blockStates.add(state);
            }

            final var event = new PortalCreateEvent(blockStates, world, entity, PortalCreateEvent.CreateReason.FIRE);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                blockStates.forEach(state -> state.update(true));
            }
        });
    }
}
