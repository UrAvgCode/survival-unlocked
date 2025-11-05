package com.uravgcode.survivalunlocked.module.moremobheads;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.uravgcode.survivalunlocked.annotation.ConfigModule;
import com.uravgcode.survivalunlocked.module.PluginModule;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@ConfigModule(path = "more-mob-heads")
public final class MoreMobHeadsModule extends PluginModule {

    private record HeadKey(
        @NotNull EntityType type,
        @Nullable String variant
    ) {
    }

    private record HeadData(
        @Nullable String texture,
        @Nullable String display,
        @Nullable String sound,
        double chance,
        double looting
    ) {
    }

    private final Map<@NotNull HeadKey, @NotNull HeadData> heads = new HashMap<>();

    public MoreMobHeadsModule(@NotNull JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void reload() {
        super.reload();
        heads.clear();

        final var logger = plugin.getComponentLogger();
        final var file = plugin.getDataPath().resolve("heads.yml").toFile();
        final var config = YamlConfiguration.loadConfiguration(file);

        config.getKeys(false).forEach(entityName -> {
            var entitySection = config.getConfigurationSection(entityName);
            if (entitySection == null) return;

            try {
                var entityType = EntityType.valueOf(entityName.toUpperCase());
                var variants = entitySection.getKeys(false);

                if (variants.stream().anyMatch(entitySection::isConfigurationSection)) {
                    variants.forEach(variant -> {
                        var variantSection = entitySection.getConfigurationSection(variant);
                        if (variantSection == null) return;
                        heads.put(new HeadKey(entityType, variant), createHeadData(variantSection));
                    });
                } else {
                    heads.put(new HeadKey(entityType, null), createHeadData(entitySection));
                }
            } catch (IllegalArgumentException e) {
                logger.warn("invalid entity type in heads.yml: {}", entityName);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        final var killed = event.getEntity();
        final var killer = killed.getKiller();
        if (killer == null) return;

        final var headData = heads.get(new HeadKey(killed.getType(), getVariant(killed)));
        if (headData == null) return;

        final var item = killer.getInventory().getItemInMainHand();
        final int looting = item.getEnchantmentLevel(Enchantment.LOOTING);

        final double chance = headData.chance + (headData.looting * looting);
        if (ThreadLocalRandom.current().nextDouble() > chance) return;

        final var head = createHead(headData.texture, headData.display, headData.sound);
        event.getDrops().add(head);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static @NotNull ItemStack createHead(@Nullable String texture, @Nullable String display, @Nullable String sound) {
        final var head = ItemStack.of(Material.PLAYER_HEAD);
        if (display != null) head.setData(DataComponentTypes.ITEM_NAME, Component.text(display));
        if (sound != null) head.setData(DataComponentTypes.NOTE_BLOCK_SOUND, NamespacedKey.minecraft(sound));
        if (texture != null) head.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile()
            .addProperty(new ProfileProperty("textures", texture))
            .build());
        return head;
    }

    private static @NotNull HeadData createHeadData(@NotNull ConfigurationSection section) {
        return new HeadData(
            section.getString("texture"),
            section.getString("display"),
            section.getString("sound"),
            section.getDouble("chance", 0.0),
            section.getDouble("looting", 0.0)
        );
    }

    private static @Nullable String getVariant(@NotNull LivingEntity entity) {
        return switch (entity) {
            case Axolotl axolotl -> axolotl.getVariant().name().toLowerCase();
            case Bee bee -> bee.getAnger() > 0
                ? (bee.hasNectar() ? "nectar_angry" : "angry") : (bee.hasNectar() ? "nectar" : "plain");
            case Cat cat -> cat.getCatType().key().value();
            case Chicken chicken -> chicken.getVariant().key().value();
            case CopperGolem copperGolem -> copperGolem.getWeatheringState().name().toLowerCase();
            case Cow cow -> cow.getVariant().key().value();
            case Creeper creeper -> creeper.isPowered() ? "charged" : null;
            case Fox fox -> fox.getFoxType().name().toLowerCase();
            case Frog frog -> frog.getVariant().key().value();
            case Goat goat -> goat.isScreaming() ? "screaming" : "normal";
            case Horse horse -> horse.getColor().name().toLowerCase();
            case TraderLlama traderLlama -> traderLlama.getColor().name().toLowerCase();
            case Llama llama -> llama.getColor().name().toLowerCase();
            case MushroomCow mushroomCow -> mushroomCow.getVariant().name().toLowerCase();
            case Panda panda -> panda.getMainGene().name().toLowerCase();
            case Parrot parrot -> parrot.getVariant().name().toLowerCase();
            case Pig pig -> pig.getVariant().key().value();
            case Rabbit rabbit -> Component.text("Toast").equals(rabbit.customName())
                ? "toast" : rabbit.getRabbitType().name().toLowerCase();
            case Sheep sheep -> Component.text("jeb_").equals(sheep.customName())
                ? "jeb" : Objects.requireNonNull(sheep.getColor()).name().toLowerCase();
            case Villager villager -> villager.getProfession().key().value();
            case Wolf wolf -> wolf.getVariant().key().value() + (wolf.isAngry() ? "_angry" : "");
            case ZombieVillager zombieVillager -> zombieVillager.getVillagerProfession().key().value();
            default -> null;
        };
    }
}
