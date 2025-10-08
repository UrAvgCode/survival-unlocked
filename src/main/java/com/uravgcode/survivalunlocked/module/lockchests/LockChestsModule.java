package com.uravgcode.survivalunlocked.module.lockchests;

import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@ModuleMeta(name = "lock-chests")
public class LockChestsModule extends PluginModule {
    private final NamespacedKey lockKey;
    private final NamespacedKey recipeKey;

    public LockChestsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.lockKey = new NamespacedKey(plugin, "lock");
        this.recipeKey = new NamespacedKey(plugin, "trial_key_recipe");

        var result = ItemStack.of(Material.TRIAL_KEY);
        var recipe = new ShapelessRecipe(recipeKey, result);
        recipe.addIngredient(Material.TRIAL_KEY);
        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestLock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var block = event.getClickedBlock();
        if (block == null) return;

        var state = block.getState();
        if (!(state instanceof Chest chest)) return;
        if (chest.isLocked()) return;

        var player = event.getPlayer();
        if (!player.isSneaking()) return;

        var key = player.getInventory().getItemInMainHand();
        if (key.getType() != Material.TRIAL_KEY) return;

        var lockValue = key.getPersistentDataContainer().get(lockKey, PersistentDataType.LONG);
        if (lockValue == null) lockValue = ThreadLocalRandom.current().nextLong();

        setKeyMeta(key);
        setLockValue(key, lockValue);
        lockChest(chest, lockValue);

        player.swingMainHand();
        player.getWorld().playSound(
            player.getLocation(),
            Sound.BLOCK_VAULT_INSERT_ITEM_FAIL,
            1.0f, 1.0f
        );
    }

    @EventHandler
    public void onTrialKeyCraft(PrepareItemCraftEvent event) {
        if (!(event.getRecipe() instanceof ShapelessRecipe recipe)) return;
        if (!recipe.key().equals(recipeKey)) return;

        boolean isContainerKey = Arrays.stream(event.getInventory().getMatrix())
            .filter(Objects::nonNull)
            .allMatch(item -> item.getPersistentDataContainer().has(lockKey, PersistentDataType.LONG));

        if (!isContainerKey) {
            event.getInventory().setResult(null);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private void setKeyMeta(ItemStack item) {
        item.setData(DataComponentTypes.ITEM_NAME, Component.translatable("item.survivalunlocked.key").fallback("Key"));
        item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("key"));
    }

    private void setLockValue(ItemStack item, long value) {
        item.editPersistentDataContainer(container -> container.set(lockKey, PersistentDataType.LONG, value));
    }

    @SuppressWarnings("UnstableApiUsage")
    private void lockChest(Chest chest, long value) {
        var item = ItemStack.of(Material.TRIAL_KEY);
        setLockValue(item, value);

        if (chest.getInventory().getHolder() instanceof DoubleChest doubleChest) {
            if (doubleChest.getLeftSide() instanceof Chest leftSide) {
                leftSide.setLockItem(item);
                leftSide.update(false, false);
            }
            if (doubleChest.getRightSide() instanceof Chest rightSide) {
                rightSide.setLockItem(item);
                rightSide.update(false, false);
            }
        } else {
            chest.setLockItem(item);
            chest.update(false, false);
        }
    }
}
