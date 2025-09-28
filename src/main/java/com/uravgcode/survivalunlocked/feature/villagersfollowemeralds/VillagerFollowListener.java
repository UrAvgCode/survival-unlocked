package com.uravgcode.survivalunlocked.feature.villagersfollowemeralds;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.uravgcode.survivalunlocked.annotation.ConfigValue;
import com.uravgcode.survivalunlocked.annotation.Feature;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Feature(name = "villagers-follow-emeralds")
public class VillagerFollowListener implements Listener {

    @ConfigValue(name = "follow-speed")
    private double followSpeed = 0.6;

    @EventHandler
    public void onVillagerEntityAdd(EntityAddToWorldEvent event) {
        if (!(event.getEntity() instanceof CraftVillager craftVillager)) return;
        var villager = craftVillager.getHandle();

        var attributes = villager.getAttributes();
        if (attributes.hasAttribute(Attributes.TEMPT_RANGE)) return;
        attributes.registerAttribute(Attributes.TEMPT_RANGE);

        var temptRange = villager.getAttribute(Attributes.TEMPT_RANGE);
        if (temptRange == null) return;

        villager.goalSelector.addGoal(3, new TemptGoal(
            villager,
            followSpeed,
            Ingredient.of(Items.EMERALD_BLOCK),
            false
        ));
    }
}
