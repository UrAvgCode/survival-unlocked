package com.uravgcode.survivalunlocked.feature.moremobheads;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.uravgcode.survivalunlocked.annotation.Feature;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.BeeVariant;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.CatType;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.CopperGolemState;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.VillagerProfession;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Feature(name = "more-mob-heads")
public class MobHeadDropListener implements Listener {
    private final YamlConfiguration config;

    public MobHeadDropListener(@NotNull JavaPlugin plugin) {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "heads.yml"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        var killed = event.getEntity();
        var killer = killed.getKiller();
        if (killer == null) return;

        getEntityConfig(killed).ifPresent(configSection -> {
            var chance = configSection.getDouble("chance", 0.0);
            var looting = configSection.getDouble("looting", 0.00);
            int lootingLevel = killer.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOTING);

            double finalChance = chance + (looting * lootingLevel);
            if (ThreadLocalRandom.current().nextDouble() > finalChance) return;

            var texture = configSection.getString("texture");
            var display = configSection.getString("display");
            var sound = configSection.getString("sound");
            var head = createHead(texture, display, sound);
            event.getDrops().add(head);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private ItemStack createHead(String texture, String display, String sound) {
        var head = ItemStack.of(Material.PLAYER_HEAD);

        if (texture != null) {
            head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
                .addProperty(new ProfileProperty("textures", texture))
                .build());
        }

        if (display != null) head.setData(DataComponentTypes.ITEM_NAME, Component.text(display));
        if (sound != null) head.setData(DataComponentTypes.NOTE_BLOCK_SOUND, NamespacedKey.minecraft(sound));

        return head;
    }

    private Optional<ConfigurationSection> getEntityConfig(LivingEntity entity) {
        var configSection = config.getConfigurationSection(entity.getType().name().toLowerCase());
        if (configSection == null) return Optional.empty();

        String key = switch (entity) {
            case Axolotl axolotl -> axolotl.getVariant().name().toLowerCase();
            case Bee bee -> BeeVariant.name(bee.hasNectar(), bee.getAnger() > 0);
            case Cat cat -> CatType.name(cat.getCatType());
            case Chicken chicken -> chicken.getVariant().key().value();
            case CopperGolem copperGolem -> CopperGolemState.name(copperGolem);
            case Cow cow -> cow.getVariant().key().value();
            case Creeper creeper -> creeper.isPowered() ? "charged" : null;
            case Fox fox -> fox.getFoxType().name().toLowerCase();
            case Frog frog -> frog.getVariant().key().value();
            case Goat goat -> goat.isScreaming() ? "screaming" : "normal";
            case Horse horse -> horse.getColor().name().toLowerCase();
            case TraderLlama traderLlama -> traderLlama.getColor().name().toLowerCase();
            case Llama llama -> llama.getColor().name().toLowerCase();
            case MushroomCow mooshroom -> mooshroom.getVariant().name().toLowerCase();
            case Panda panda -> panda.getMainGene().name().toLowerCase();
            case Parrot parrot -> parrot.getVariant().name().toLowerCase();
            case Pig pig -> pig.getVariant().key().value();
            case Rabbit rabbit -> Component.text("Toast").equals(rabbit.customName())
                ? "toast" : rabbit.getRabbitType().name().toLowerCase();
            case Sheep sheep -> Component.text("jeb_").equals(sheep.customName())
                ? "jeb" : Objects.requireNonNull(sheep.getColor()).name().toLowerCase();
            case Villager villager -> VillagerProfession.name(villager.getProfession());
            case Wolf wolf -> wolf.getVariant().key().value() + (wolf.isAngry() ? "_angry" : "");
            case ZombieVillager zombieVillager -> VillagerProfession.name(zombieVillager.getVillagerProfession());
            default -> null;
        };

        return Optional.of(configSection).map(section -> key == null ? section : section.getConfigurationSection(key));
    }
}
