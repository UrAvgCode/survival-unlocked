package com.uravgcode.survivalunlocked.feature.lockchests;

import com.uravgcode.survivalunlocked.feature.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Feature(name = "container-locking")
public class ContainerLockListener implements Listener {
    private final NamespacedKey lockKey;

    public ContainerLockListener(@NotNull JavaPlugin plugin) {
        this.lockKey = new NamespacedKey(plugin, "lock");
    }

    @EventHandler
    public void onContainerLock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var block = event.getClickedBlock();
        if (block == null) return;

        var state = block.getState();
        if (!(state instanceof Container container)) return;
        if (container.isLocked()) return;

        var player = event.getPlayer();
        if (!player.isSneaking()) return;

        var key = player.getInventory().getItemInMainHand();
        if (key.getType() != Material.TRIAL_KEY) return;
        setKeyMeta(key, container);

        long lockValue = ThreadLocalRandom.current().nextLong();
        setLockValue(key, lockValue);
        lockContainer(container, lockValue);

        event.setCancelled(true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void setKeyMeta(ItemStack item, Container container) {
        var meta = item.getItemMeta();
        if (meta == null) return;

        if (!meta.hasCustomName() || meta.customName() instanceof TranslatableComponent) {
            var containerTranslationKey = container.getType().translationKey();
            var name = Component.translatable(containerTranslationKey)
                .append(Component.text(" "))
                .append(Component.translatable("item.survivalunlocked.key").fallback("Key"))
                .decoration(TextDecoration.ITALIC, false);

            meta.customName(name);
        }

        var customModelData = meta.getCustomModelDataComponent();
        customModelData.setStrings(List.of("key"));
        meta.setCustomModelDataComponent(customModelData);

        item.setItemMeta(meta);
    }

    private void setLockValue(ItemStack item, long value) {
        var meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(lockKey, PersistentDataType.LONG, value);
        item.setItemMeta(meta);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void lockContainer(Container container, long value) {
        var item = new ItemStack(Material.TRIAL_KEY);
        setLockValue(item, value);

        container.setLockItem(item);
        container.update();
    }
}
