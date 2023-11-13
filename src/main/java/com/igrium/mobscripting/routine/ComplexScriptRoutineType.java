package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

/**
 * A script routine that can be created from brigadier arguments.
 */
public abstract class ComplexScriptRoutineType<T extends ScriptRoutine> extends ScriptRoutineType<T> {

    public static interface BrigadierExitpointAppender {
        public ArgumentBuilder<ServerCommandSource, ?> exit(ArgumentBuilder<ServerCommandSource, ?> builder);
    }

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
     * Construct a brigadier argument tree that will be suitable for this routine.
     * 
     * @param exitpoint Each "end" node of the tree must be wrapped with
     *                  <code>exitpoint.exit(node)</code> so that command execution
     *                  can continue.
     * @return Brigadier argument builder.
     */
    public abstract ArgumentBuilder<ServerCommandSource, ?> getArgumentBuilder(BrigadierExitpointAppender exitpoint);
}
