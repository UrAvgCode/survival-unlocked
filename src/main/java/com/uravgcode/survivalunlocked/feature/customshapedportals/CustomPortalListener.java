package com.uravgcode.survivalunlocked.feature.customshapedportals;

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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomPortalListener implements Listener {
    private static final int minPortalSize = 6;
    private static final int maxWidth = 21;
    private static final int maxHeight = 21;

    private static final Set<Material> portalFrameMaterials = Set.of(
        Material.OBSIDIAN,
        Material.CRYING_OBSIDIAN
    );

    private final JavaPlugin plugin;

    public CustomPortalListener(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        var fireBlock = event.getBlock();
        if (!portalFrameMaterials.contains(fireBlock.getRelative(BlockFace.DOWN).getType())) return;

        var world = fireBlock.getWorld();
        if (world.getEnvironment() == World.Environment.THE_END) return;

        var entity = event.getIgnitingEntity();
        var location = fireBlock.getLocation();

        for (Axis axis : new Axis[]{Axis.X, Axis.Z}) {
            var portalBlocks = getPortalBlocks(fireBlock, axis);
            if (portalBlocks.isPresent()) {
                buildPortal(portalBlocks.get(), axis, world, entity, location);
                event.setCancelled(true);
                break;
            }
        }
    }

    private Optional<Set<Block>> getPortalBlocks(Block fireBlock, Axis axis) {
        Set<Block> visited = new HashSet<>(Collections.singletonList(fireBlock));
        Deque<Block> queue = new ArrayDeque<>(Collections.singletonList(fireBlock));

        var directions = (axis == Axis.Z)
            ? new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH}
            : new BlockFace[]{BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST};

        var bounds = new BoundingBox(
            fireBlock.getX(), fireBlock.getY(), fireBlock.getZ(),
            fireBlock.getX(), fireBlock.getY(), fireBlock.getZ()
        );

        while (!queue.isEmpty()) {
            var current = queue.poll();

            for (BlockFace direction : directions) {
                var neighbor = current.getRelative(direction);
                if (visited.contains(neighbor)) continue;

                var material = neighbor.getType();
                if (!material.equals(Material.AIR) && !material.equals(Material.FIRE)) {
                    if (!portalFrameMaterials.contains(material)) {
                        return Optional.empty();
                    }
                    continue;
                }

                visited.add(neighbor);
                queue.add(neighbor);

                bounds.union(neighbor.getX(), neighbor.getY(), neighbor.getZ());
                var width = (axis == Axis.X) ? bounds.getWidthX() : bounds.getWidthZ();
                var height = bounds.getHeight();

                if (width >= maxWidth || height >= maxHeight) return Optional.empty();
            }
        }

        return (visited.size() < minPortalSize) ? Optional.empty() : Optional.of(visited);
    }

    private void buildPortal(Set<Block> validPortalBlocks, Axis axis, World world, Entity entity, Location location) {
        plugin.getServer().getRegionScheduler().run(plugin, location, task -> {
            var portalData = (Orientable) Material.NETHER_PORTAL.createBlockData();
            portalData.setAxis(axis);

            List<BlockState> blockStates = new ArrayList<>(validPortalBlocks.size());
            for (Block block : validPortalBlocks) {
                var state = block.getState();
                state.setBlockData(portalData);
                blockStates.add(state);
            }

            var portalCreateEvent = new PortalCreateEvent(blockStates, world, entity, PortalCreateEvent.CreateReason.FIRE);
            if (portalCreateEvent.callEvent()) {
                blockStates.forEach(state -> state.update(true));
            }
        });
    }
}
