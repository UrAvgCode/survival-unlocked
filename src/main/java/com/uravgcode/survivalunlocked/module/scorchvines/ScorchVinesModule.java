package com.uravgcode.survivalunlocked.module.scorchvines;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.data.BlockDataContainer;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "scorch-vines")
public final class ScorchVinesModule extends PluginModule {
    private final NamespacedKey scorchedKey;

    public ScorchVinesModule(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.scorchedKey = new NamespacedKey(plugin, "scorched");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVineSpread(BlockSpreadEvent event) {
        final var source = event.getSource();
        if (source.getType() != Material.VINE) return;

        final var dataContainer = new BlockDataContainer(source);
        if (dataContainer.has(scorchedKey)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVineRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final var block = event.getClickedBlock();
        if (block == null || block.getType() != Material.VINE) return;

        final var item = event.getItem();
        if (item == null || item.getType() != Material.FLINT_AND_STEEL) return;

        new BlockDataContainer(block).set(scorchedKey, PersistentDataType.BYTE, (byte) 1);

        final var player = event.getPlayer();
        player.swingMainHand();
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1f, 1f);

        if (player.getGameMode() != GameMode.CREATIVE) {
            item.editMeta(Damageable.class, damageable ->
                damageable.setDamage(damageable.getDamage() + 1)
            );
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final var block = event.getBlock();
        if (block.getType() == Material.VINE) {
            new BlockDataContainer(block).remove(scorchedKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakBlock(BlockBreakBlockEvent event) {
        final var block = event.getBlock();
        if (block.getType() == Material.VINE) {
            new BlockDataContainer(block).remove(scorchedKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDestroy(BlockDestroyEvent event) {
        final var block = event.getBlock();
        if (block.getType() == Material.VINE) {
            new BlockDataContainer(block).remove(scorchedKey);
        }
    }
}
