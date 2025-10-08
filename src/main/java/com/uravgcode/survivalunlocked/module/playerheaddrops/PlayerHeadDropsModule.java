package com.uravgcode.survivalunlocked.module.playerheaddrops;

import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

@ModuleMeta(name = "player-head-drops")
public class PlayerHeadDropsModule extends PluginModule {

    @SuppressWarnings("UnstableApiUsage")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getEntity();
        if (player.getKiller() == null) return;

        var head = ItemStack.of(Material.PLAYER_HEAD);
        head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        event.getDrops().add(head);
    }
}
