package com.uravgcode.survivalunlocked;

import com.uravgcode.survivalunlocked.listener.*;
import com.uravgcode.survivalunlocked.mobheads.MobHeadDropListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalUnlockedPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("heads.yml", false);

        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new VillagerFollowListener(this), this);
        pluginManager.registerEvents(new ItemFrameInvisibleListener(), this);
        pluginManager.registerEvents(new CustomPortalListener(this), this);
        pluginManager.registerEvents(new FireballThrowListener(), this);
        pluginManager.registerEvents(new PlayerHeadDropListener(), this);
        pluginManager.registerEvents(new MobHeadDropListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
