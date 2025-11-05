package com.uravgcode.survivalunlocked.module.silktouchpaintings;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "silk-touch-paintings")
public final class SilkTouchPaintingsModule extends PluginModule {

    public SilkTouchPaintingsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getEntity() instanceof final Painting painting)) return;

        final var remover = event.getRemover();
        if (!(remover instanceof final Player player)) return;

        final var itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        final var item = ItemStack.of(Material.PAINTING);
        item.setData(DataComponentTypes.PAINTING_VARIANT, painting.getArt());

        painting.remove();
        painting.getWorld().dropItemNaturally(painting.getLocation(), item);
    }
}
