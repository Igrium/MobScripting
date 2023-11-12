package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.server.command.ServerCommandSource;

/**
 * A script routine that can be created from brigadier arguments.
 */
public abstract class ComplexScriptRoutineType<T extends ScriptRoutine> extends ScriptRoutineType<T> {

    public static interface BrigadierRoutineFactory<T> {
        public T create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
    }
    
    /**
     * Create a routine of this type using the given brigadier args.
     * @param context Brigadier context.
     * @return The routine.
     */
    public abstract T createWithArgs(CommandContext<ServerCommandSource> context, EntityScriptComponent component) throws CommandSyntaxException;

    /**
     * Get a brigadier argument builder with the arguments this routine will accept.
     * @param then Node to append with <code>then</code> to all endpoints.
     * @param executes Command to append with <code>executes</code> to all endpoints.
     * @return Brigadier argument builder.
     */
    public abstract ArgumentBuilder<ServerCommandSource, ?> getArgumentBuilder(CommandNode<ServerCommandSource> redirect);
}
