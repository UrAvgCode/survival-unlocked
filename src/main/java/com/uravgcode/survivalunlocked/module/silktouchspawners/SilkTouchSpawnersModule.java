package com.uravgcode.survivalunlocked.module.silktouchspawners;

import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.keys.DataComponentTypeKeys;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ModuleMeta(name = "silk-touch-spawners")
public class SilkTouchSpawnersModule extends PluginModule {

    public SilkTouchSpawnersModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        var player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) return;

        var tool = player.getInventory().getItemInMainHand();
        if (!tool.containsEnchantment(Enchantment.SILK_TOUCH)) return;
        if (!block.isPreferredTool(tool)) return;

        var item = ItemStack.of(Material.SPAWNER);
        var dataComponentType = Registry.DATA_COMPONENT_TYPE.get(DataComponentTypeKeys.BLOCK_ENTITY_DATA);
        if (dataComponentType != null) {
            var tooltipDisplay = TooltipDisplay.tooltipDisplay().addHiddenComponents(dataComponentType).build();
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);
        }

        block.getWorld().dropItemNaturally(block.getLocation(), item);
        event.setExpToDrop(0);
    }
}
