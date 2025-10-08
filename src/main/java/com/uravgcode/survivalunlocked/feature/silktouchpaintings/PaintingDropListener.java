package com.uravgcode.survivalunlocked.feature.silktouchpaintings;

import com.uravgcode.survivalunlocked.annotation.Feature;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.inventory.ItemStack;

@Feature(name = "silk-touch-paintings")
public class PaintingDropListener implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getEntity() instanceof Painting painting)) return;

        var remover = event.getRemover();
        if (!(remover instanceof Player player)) return;

        var itemInHand = player.getInventory().getItemInMainHand();
        if (!itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) return;

        var item = ItemStack.of(Material.PAINTING);
        item.setData(DataComponentTypes.PAINTING_VARIANT, painting.getArt());

        painting.remove();
        painting.getWorld().dropItemNaturally(painting.getLocation(), item);
    }
}
