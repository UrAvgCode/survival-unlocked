package com.uravgcode.survivalunlocked.feature.lockchests;

import com.uravgcode.survivalunlocked.feature.Feature;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@Feature(name = "container-locking")
public class ContainerLockListener implements Listener {
    private final NamespacedKey lockKey;

    public ContainerLockListener(@NotNull JavaPlugin plugin) {
        this.lockKey = new NamespacedKey(plugin, "lock");
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onContainerLock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var block = event.getClickedBlock();
        if (block == null) return;

        var state = block.getState();
        if (!(state instanceof Container container)) return;

        var player = event.getPlayer();
        if (!player.isSneaking()) return;

        var key = player.getInventory().getItemInMainHand();
        if (key.getType() != Material.TRIAL_KEY) return;

        var meta = key.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(lockKey, PersistentDataType.LONG, ThreadLocalRandom.current().nextLong());
        key.setItemMeta(meta);

        container.setLockItem(key);
        container.update();
        event.setCancelled(true);
    }
}
