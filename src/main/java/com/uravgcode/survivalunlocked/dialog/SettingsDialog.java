package com.uravgcode.survivalunlocked.dialog;

import com.uravgcode.survivalunlocked.SurvivalUnlocked;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
@SuppressWarnings("UnstableApiUsage")
public final class SettingsDialog {
    private SettingsDialog() {
    }

    public static Dialog create() {
        return Dialog.create(builder -> builder.empty()
            .base(DialogBase.builder(Component.text("Survival Unlocked Settings"))
                .inputs(generateDialogInputs())
                .build()
            )
            .type(DialogType.confirmation(
                ActionButton.builder(Component.text("Confirm", NamedTextColor.GREEN))
                    .action(DialogAction.customClick(SettingsDialog::updateSettings, ClickCallback.Options.builder().build()))
                    .width(100)
                    .build(),
                ActionButton.builder(Component.text("Discard", NamedTextColor.RED))
                    .width(100)
                    .build()
            ))
        );
    }

    private static void updateSettings(DialogResponseView response, Audience audience) {
        var plugin = SurvivalUnlocked.instance();
        var config = plugin.getConfig();

        for (String key : getConfigKeys()) {
            var sanitizedKey = key.replace("-", "");
            var responseText = response.getText(sanitizedKey);
            var enabled = "enabled".equals(responseText);
            config.set("modules." + key + ".enabled", enabled);
        }

        plugin.saveConfig();
        plugin.reload();
    }

    private static List<DialogInput> generateDialogInputs() {
        return getConfigKeys().stream()
            .map(key -> {
                var config = SurvivalUnlocked.instance().getConfig();
                var enabled = config.getBoolean("modules." + key + ".enabled", false);

                var enabledOption = SingleOptionDialogInput.OptionEntry.create(
                    "enabled",
                    Component.text("enabled", NamedTextColor.GREEN),
                    enabled
                );

                var disabledOption = SingleOptionDialogInput.OptionEntry.create(
                    "disabled",
                    Component.text("disabled", NamedTextColor.RED),
                    !enabled
                );

                var label = Arrays.stream(key.split("-"))
                    .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                    .collect(Collectors.joining(" "));

                return DialogInput.singleOption(
                    key.replace("-", ""),
                    200,
                    List.of(enabledOption, disabledOption),
                    Component.text(label),
                    true
                );
            })
            .collect(Collectors.toList());
    }

    private static Set<String> getConfigKeys() {
        var defaults = SurvivalUnlocked.instance().getConfig().getDefaults();
        if (defaults == null) return Collections.emptySet();

        var modulesSection = defaults.getConfigurationSection("modules");
        if (modulesSection == null) return Collections.emptySet();

        return modulesSection.getKeys(false);
    }
}
