package com.uravgcode.survivalunlocked.feature.zombiehorse;

import com.uravgcode.survivalunlocked.annotation.Feature;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Feature(name = "zombie-horse")
public class ZombieHorseListener implements Listener {

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        var cause = event.getCause();
        if (cause != LightningStrikeEvent.Cause.WEATHER) return;

        var lightning = event.getLightning();
        var world = lightning.getWorld();
        var location = lightning.getLocation();

        if (ThreadLocalRandom.current().nextDouble() > spawnChance(world, location)) return;

        var zombie = world.spawn(location, Zombie.class);
        var horse = world.spawn(location, ZombieHorse.class);

        horse.setTamed(true);
        horse.addPassenger(zombie);

        var equipment = zombie.getEquipment();
        equipment.setHelmet(new ItemStack(Material.IRON_HELMET));
        equipment.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.IRON_BOOTS));
        equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
    }

    private double spawnChance(World world, Location location) {
        var difficulty = world.getDifficulty();
        if (difficulty == Difficulty.PEACEFUL) return 0.0;

        var fullTime = world.getFullTime();

        double daytimeFactor;
        if (fullTime > 63L * 24000L) {
            daytimeFactor = 0.25;
        } else if (fullTime < 3L * 24000L) {
            daytimeFactor = 0.0;
        } else {
            daytimeFactor = (fullTime - 72000L) / 5760000.0;
        }

        var chunk = world.getChunkAt(location);
        var inhabitedTime = chunk.getInhabitedTime();

        double chunkFactor;
        if (inhabitedTime > TimeUnit.HOURS.toSeconds(50) * 20) {
            chunkFactor = 1.0;
        } else {
            chunkFactor = inhabitedTime / 3600000.0;
        }

        if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.EASY) {
            chunkFactor *= 0.75;
        }

        var moonPhase = world.getMoonPhase();
        chunkFactor += Math.min(moonPhase.ordinal() / 4.0, daytimeFactor);

        if (difficulty == Difficulty.EASY) {
            chunkFactor *= 0.5;
        }

        var regionalDifficulty = 0.75 + daytimeFactor + chunkFactor;
        if (difficulty == Difficulty.NORMAL) {
            regionalDifficulty *= 2;
        } else if (difficulty == Difficulty.HARD) {
            regionalDifficulty *= 3;
        }

        return regionalDifficulty / 100.0;
    }
}
