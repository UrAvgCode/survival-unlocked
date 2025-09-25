package com.uravgcode.survivalunlocked.feature.lockchests;

import com.uravgcode.survivalunlocked.feature.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Feature(name = "container-locking")
public class ContainerLockListener implements Listener {
    private final NamespacedKey lockKey;
    private final NamespacedKey recipeKey;

    public ContainerLockListener(@NotNull JavaPlugin plugin) {
        this.lockKey = new NamespacedKey(plugin, "lock");
        this.recipeKey = new NamespacedKey(plugin, "trial_key_recipe");

        var result = new ItemStack(Material.TRIAL_KEY);
        var recipe = new ShapelessRecipe(recipeKey, result);
        recipe.addIngredient(Material.TRIAL_KEY);
        plugin.getServer().addRecipe(recipe);
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
        if (key.getItemMeta().getPersistentDataContainer().has(lockKey, PersistentDataType.LONG)) return;

        long lockValue = ThreadLocalRandom.current().nextLong();
        setLockValue(key, lockValue);
        lockContainer(container, lockValue);

        setKeyMeta(key, container);
        player.playSound(player.getLocation(), Sound.BLOCK_VAULT_INSERT_ITEM_FAIL, 1.0f, 1.0f);

        event.setCancelled(true);
    }

    @EventHandler
    public void onTrialKeyCraft(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapelessRecipe recipe)) return;
        if (!recipe.key().equals(recipeKey)) return;

        boolean isContainerKey = Arrays.stream(event.getInventory().getMatrix())
            .filter(Objects::nonNull)
            .map(ItemStack::getItemMeta)
            .filter(Objects::nonNull)
            .allMatch(meta -> meta.getPersistentDataContainer().has(lockKey, PersistentDataType.LONG));

        if (!isContainerKey) {
            event.getInventory().setResult(null);
        }
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
