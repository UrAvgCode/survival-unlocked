package com.uravgcode.survivalunlocked.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.uravgcode.survivalunlocked.SurvivalUnlocked;
import com.uravgcode.survivalunlocked.dialog.SettingsDialog;
import com.uravgcode.survivalunlocked.update.UpdateChecker;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SurvivalUnlockedCommand {

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("survivalunlocked")
            .requires(sender -> sender.getSender().hasPermission("survivalunlocked.admin"))
            .then(Commands.literal("version")
                .executes(this::version))
            .then(Commands.literal("reload")
                .executes(this::reload))
            .then(Commands.literal("toggle")
                .executes(this::toggle))
            .build();
    }

    private int version(CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();
        new UpdateChecker(SurvivalUnlocked.instance()).sendVersionInfo(sender);
        return Command.SINGLE_SUCCESS;
    }

    private int reload(CommandContext<CommandSourceStack> context) {
        SurvivalUnlocked.instance().reload();
        final var sender = context.getSource().getSender();
        sender.sendMessage(Component.text("successfully reloaded config", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private int toggle(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().showDialog(SettingsDialog.create());
        return Command.SINGLE_SUCCESS;
    }
}
