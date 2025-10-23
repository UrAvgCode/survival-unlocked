package com.uravgcode.survivalunlocked.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.uravgcode.survivalunlocked.SurvivalUnlocked;
import com.uravgcode.survivalunlocked.dialog.SettingsDialog;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginCommand {
    private PluginCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("survivalunlocked")
            .requires(sender -> sender.getSender().hasPermission("survivalunlocked.admin"))
            .then(Commands.literal("reload").executes(PluginCommand::reload))
            .then(Commands.literal("toggle").executes(PluginCommand::toggle))
            .build();
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        final var plugin = SurvivalUnlocked.instance();
        final var sender = context.getSource().getSender();

        plugin.reload();
        sender.sendMessage(Component.text("successfully reloaded config", NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int toggle(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().showDialog(SettingsDialog.create());
        return Command.SINGLE_SUCCESS;
    }
}
