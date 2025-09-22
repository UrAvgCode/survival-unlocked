package com.uravgcode.survivalunlocked.feature.betterarmorstands;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ArmorStandListener implements Listener {
    private final NamespacedKey poseKey;

    public ArmorStandListener(@NotNull JavaPlugin plugin) {
        this.poseKey = new NamespacedKey(plugin, "pose");
    }

    @EventHandler
    public void onArmorStandSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            armorStand.setArms(true);
        }
    }

    @EventHandler
    public void onArmorStandClick(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
        if (!event.getPlayer().isSneaking()) return;

        var dataContainer = armorStand.getPersistentDataContainer();
        var poseData = dataContainer.get(poseKey, PersistentDataType.STRING);
        var pose = poseData != null ? ArmorStandPose.valueOf(poseData) : ArmorStandPose.DEFAULT;

        var poses = ArmorStandPose.values();
        int nextIndex = (pose.ordinal() + 1) % poses.length;
        var nextPose = poses[nextIndex];

        armorStand.setBodyPose(pose.body);
        armorStand.setHeadPose(pose.head);
        armorStand.setLeftArmPose(pose.leftArm);
        armorStand.setLeftLegPose(pose.leftLeg);
        armorStand.setRightArmPose(pose.rightArm);
        armorStand.setRightLegPose(pose.rightLeg);
        armorStand.getPersistentDataContainer().set(poseKey, PersistentDataType.STRING, nextPose.name());

        event.setCancelled(true);
    }
}
