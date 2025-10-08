package com.uravgcode.survivalunlocked.module.villagersfollowemeralds;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ModuleMeta(name = "villagers-follow-emeralds")
public final class VillagerFollowEmeraldsModule extends PluginModule {

    @ConfigValue(name = "follow-speed")
    private double followSpeed = 0.6;

    public VillagerFollowEmeraldsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onVillagerEntityAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof CraftVillager craftVillager)) return;
        var villager = craftVillager.getHandle();

        var attributes = villager.getAttributes();
        if (!attributes.hasAttribute(Attributes.TEMPT_RANGE)) {
            attributes.registerAttribute(Attributes.TEMPT_RANGE);
        }

        villager.goalSelector.addGoal(3, new TemptGoal(
            villager,
            followSpeed,
            Ingredient.of(Items.EMERALD_BLOCK),
            false
        ));
    }
}
