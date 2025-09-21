package com.uravgcode.survivalunlocked.feature.moremobheads;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.BeeVariant;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.CatType;
import com.uravgcode.survivalunlocked.feature.moremobheads.variant.VillagerProfession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MobHeadDropListener implements Listener {
    private final JavaPlugin plugin;
    private final YamlConfiguration config;

    public MobHeadDropListener(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "heads.yml"));
    }

    @EventHandler
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

    private ItemStack createHead(String texture, String display, String sound) {
        var head = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) head.getItemMeta();

        if (texture != null) {
            var profile = plugin.getServer().createProfile(new UUID(0, 0), "");
            profile.getProperties().add(new ProfileProperty("textures", texture));
            meta.setPlayerProfile(profile);
        }

        if (display != null) meta.displayName(Component.text(display).decoration(TextDecoration.ITALIC, false));
        if (sound != null) meta.setNoteBlockSound(NamespacedKey.minecraft(sound));

        head.setItemMeta(meta);
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
