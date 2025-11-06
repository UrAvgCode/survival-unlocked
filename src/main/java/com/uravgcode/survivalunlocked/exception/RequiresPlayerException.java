package com.uravgcode.survivalunlocked.exception;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;

public final class RequiresPlayerException extends CommandSyntaxException {
    private static final Message message = MessageComponentSerializer.message().serialize(Component.translatable("permissions.requires.player"));
    private static final CommandExceptionType type = new SimpleCommandExceptionType(message);

    public RequiresPlayerException() {
        super(type, message);
    }
}
