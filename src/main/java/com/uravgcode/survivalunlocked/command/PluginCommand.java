package com.uravgcode.survivalunlocked.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.uravgcode.survivalunlocked.SurvivalUnlocked;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PluginCommand {
    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("survivalunlocked")
            .then(Commands.literal("reload").executes(PluginCommand::reload))
            .build();
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        SurvivalUnlocked.plugin().reload();
        var message = Component.text("successfully reloaded config", NamedTextColor.GREEN);
        context.getSource().getSender().sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }
}
