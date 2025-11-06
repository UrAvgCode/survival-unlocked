package com.uravgcode.survivalunlocked.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.uravgcode.survivalunlocked.event.CoordinateHudEvent;
import com.uravgcode.survivalunlocked.exception.RequiresPlayerException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public final class CoordinateHudCommand {

    public LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("coordinatehud")
            .requires(sender -> sender.getSender().hasPermission("survivalunlocked.coordinatehud"))
            .executes(this::execute)
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context) throws RequiresPlayerException {
        if (!(context.getSource().getSender() instanceof Player player)) throw new RequiresPlayerException();
        player.getServer().getPluginManager().callEvent(new CoordinateHudEvent(player));
        return Command.SINGLE_SUCCESS;
    }
}
