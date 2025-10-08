package com.uravgcode.survivalunlocked.module.betterarmorstands;

import com.uravgcode.survivalunlocked.annotation.ModuleMeta;
import com.uravgcode.survivalunlocked.module.PluginModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@ModuleMeta(name = "better-armor-stands")
public class BetterArmorStandsModule extends PluginModule {
    private final NamespacedKey poseKey;

    public BetterArmorStandsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
        this.poseKey = new NamespacedKey(plugin, "pose");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStandSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            armorStand.setArms(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorStandClick(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
        if (!event.getPlayer().isSneaking()) return;

        var pose = getPose(armorStand);
        var poses = ArmorStandPose.values();
        int nextIndex = (pose.ordinal() + 1) % poses.length;
        var nextPose = poses[nextIndex];

        setPose(armorStand, nextPose);
        event.setCancelled(true);
    }

    private ArmorStandPose getPose(ArmorStand armorStand) {
        var poseData = armorStand.getPersistentDataContainer().get(poseKey, PersistentDataType.STRING);
        return poseData != null ? ArmorStandPose.valueOf(poseData) : ArmorStandPose.DEFAULT;
    }

    private void setPose(ArmorStand armorStand, ArmorStandPose pose) {
        armorStand.getPersistentDataContainer().set(poseKey, PersistentDataType.STRING, pose.name());
        armorStand.setBodyPose(pose.body);
        armorStand.setHeadPose(pose.head);
        armorStand.setLeftArmPose(pose.leftArm);
        armorStand.setLeftLegPose(pose.leftLeg);
        armorStand.setRightArmPose(pose.rightArm);
        armorStand.setRightLegPose(pose.rightLeg);
    }
}
