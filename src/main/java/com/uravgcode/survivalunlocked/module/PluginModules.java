package com.uravgcode.survivalunlocked.module;

import com.uravgcode.survivalunlocked.module.betterarmorstands.BetterArmorStandsModule;
import com.uravgcode.survivalunlocked.module.bettertridents.BetterTridentsModule;
import com.uravgcode.survivalunlocked.module.callyourpets.CallYourPetsModule;
import com.uravgcode.survivalunlocked.module.customshapedportals.CustomShapedPortalsModule;
import com.uravgcode.survivalunlocked.module.invisibleitemframes.InvisibleItemFramesModule;
import com.uravgcode.survivalunlocked.module.keepbabyanimals.KeepBabyAnimalsModule;
import com.uravgcode.survivalunlocked.module.lockchests.LockChestsModule;
import com.uravgcode.survivalunlocked.module.moremobheads.MoreMobHeadsModule;
import com.uravgcode.survivalunlocked.module.playerheaddrops.PlayerHeadDropsModule;
import com.uravgcode.survivalunlocked.module.silencemobs.SilenceMobsModule;
import com.uravgcode.survivalunlocked.module.silktouchpaintings.SilkTouchPaintingsModule;
import com.uravgcode.survivalunlocked.module.silktouchspawners.SilkTouchSpawnersModule;
import com.uravgcode.survivalunlocked.module.smoothsleeptransition.SmoothSleepTransitionModule;
import com.uravgcode.survivalunlocked.module.throwablefireballs.ThrowableFireballsModule;
import com.uravgcode.survivalunlocked.module.transferyourpets.TransferYourPetsModule;
import com.uravgcode.survivalunlocked.module.villagersfollowemeralds.VillagerFollowEmeraldsModule;
import com.uravgcode.survivalunlocked.module.zombiehorse.ZombieHorseModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginModules {
    public static List<@NotNull PluginModule> modules(@NotNull JavaPlugin plugin) {
        return List.of(
            new BetterArmorStandsModule(plugin),
            new BetterTridentsModule(plugin),
            new CallYourPetsModule(),
            new CustomShapedPortalsModule(plugin),
            new InvisibleItemFramesModule(),
            new KeepBabyAnimalsModule(),
            new LockChestsModule(plugin),
            new MoreMobHeadsModule(plugin),
            new PlayerHeadDropsModule(),
            new SilenceMobsModule(),
            new SilkTouchPaintingsModule(),
            new SilkTouchSpawnersModule(),
            new SmoothSleepTransitionModule(plugin),
            new ThrowableFireballsModule(),
            new TransferYourPetsModule(),
            new VillagerFollowEmeraldsModule(),
            new ZombieHorseModule()
        );
    }
}
