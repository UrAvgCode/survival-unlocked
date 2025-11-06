package com.uravgcode.survivalunlocked.module.shearnametags;

import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ConfigModule(path = "shear-name-tags")
public final class ShearNameTagsModule extends PluginModule {

    public ShearNameTagsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final var player = event.getPlayer();
        final var hand = event.getHand();
        final var item = player.getInventory().getItem(hand);
        if (item.getType() != Material.SHEARS || !player.isSneaking()) return;

        if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
        final var customName = entity.customName();
        if (customName == null) return;

        player.swingHand(hand);
        entity.customName(null);

        final var nameTag = ItemStack.of(Material.NAME_TAG);
        nameTag.setData(DataComponentTypes.CUSTOM_NAME, customName);

        final var world = entity.getWorld();
        final var location = entity.getLocation();
        world.dropItemNaturally(location, nameTag);
        world.playSound(location, Sound.ENTITY_SHEEP_SHEAR, 1f, 1f);

        event.setCancelled(true);
    }
}
