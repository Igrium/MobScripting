package com.igrium.mobscripting.commands;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.routine.ComplexScriptRoutineType;
import com.igrium.mobscripting.routine.ScriptRoutine;
import com.igrium.mobscripting.routine.ScriptRoutineType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class RoutineCommand {

    private static SimpleCommandExceptionType livingEntityException = new SimpleCommandExceptionType(Text.literal("Entity must be a living entity."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("routine").then(
            argument("target", EntityArgumentType.entity()).then(
                literal("list").executes(RoutineCommand::list)
            ).then(
                literal("add").then(
                    literal("simple").then(
                        appendFinishNodes(argument("type", RegistryEntryArgumentType.registryEntry(registryAccess, ScriptRoutineType.REGISTRY_KEY)), RoutineCommand::add)
                    )
                ).then(
                    buildAdvancedRoutineArguments()
                )
            ).then(
                literal("clear").executes(RoutineCommand::clear)
            )
        ));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildAdvancedRoutineArguments() {
        LiteralArgumentBuilder<ServerCommandSource> root = literal("advanced");

        for (Identifier id : ScriptRoutineType.REGISTRY.getIds()) {
            ScriptRoutineType<?> type = ScriptRoutineType.REGISTRY.get(id);

            if (type instanceof ComplexScriptRoutineType<?> adv) {
                root = root.then(
                    literal(id.toString()).then(
                        adv.getArgumentBuilder(node -> appendFinishNodes(node, context -> addWithArgs(context, id)))
                    )
                );
            }
        }

        return root;
    }

    private static ArgumentBuilder<ServerCommandSource, ?> appendFinishNodes(ArgumentBuilder<ServerCommandSource, ?> builder, Command<ServerCommandSource> command) {
        return builder.then(
            literal("onComplete").then(
                argument("completeFunction", CommandFunctionArgumentType.commandFunction()).then(
                    literal("onInterrupted").then(
                        argument("interruptFunction", CommandFunctionArgumentType.commandFunction()).executes(command)
                    )
                ).executes(command)
            )
        ).then(
            literal("onInterrupted").then(
                argument("interruptFunction", CommandFunctionArgumentType.commandFunction()).then(
                    literal("onComplete").then(
                        argument("completeFunction", CommandFunctionArgumentType.commandFunction()).executes(command)
                    )
                ).executes(command)
            )
        ).executes(command);
    }

    private static int addWithArgs(CommandContext<ServerCommandSource> context, Identifier id) throws CommandSyntaxException {
        LivingEntity entity = getEntity(context);
        EntityScriptComponent component = EntityScriptComponent.get(entity);

        var type = ScriptRoutineType.REGISTRY.get(id);
        ScriptRoutine routine;

        if (type instanceof ComplexScriptRoutineType<?> adv) {
            routine = adv.createWithArgs(context, component);
        } else {
            throw new SimpleCommandExceptionType(Text.literal(
                    "Non-advanced script routine was evaluated in the advanced routine brigadier tree. This should not happen."))
                    .create();
        }

        try {
            Identifier onComplete = CommandFunctionArgumentType.getFunctions(context, "completeFunction")
                    .iterator().next()
                    .getId();
            routine.setCompleteFunction(onComplete);
        } catch (IllegalArgumentException e) {
        }
        
        try {
            Identifier onInterrupt = CommandFunctionArgumentType.getFunctions(context, "interruptFunction")
                    .iterator().next()
                    .getId();
            routine.setInterruptFunction(onInterrupt);
        } catch (IllegalArgumentException e) {
        }

        component.routines().add(routine);
        return 1;
    }

    private static int add(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity entity = getEntity(context);
        EntityScriptComponent component = EntityScriptComponent.get(entity);

        var type = RegistryEntryArgumentType.getRegistryEntry(context, "type", ScriptRoutineType.REGISTRY_KEY)
                .value();
        ScriptRoutine routine = type.create(component);
        component.routines().add(routine);
        return 1;
    }

    private static int clear(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity entity = getEntity(context);
        EntityScriptComponent component = EntityScriptComponent.get(entity);
        
        int count = component.routines().size();
        component.routines().clear();
        return count;
    }

    
    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity ent = getEntity(context);
        int i = 0;
            
        EntityScriptComponent component = EntityScriptComponent.get(ent);
        if (component.routines().isEmpty()) {
            context.getSource().sendFeedback(() -> Text.literal("This entity has no routines."), false);
            return 0;
        }

        for (ScriptRoutine routine : component.routines()) {
            context.getSource().sendFeedback(() -> Text.literal(routine.getType().getID().toString()), false);
        }
        i++;

        return i;
    }

    private static LivingEntity getEntity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity ent = EntityArgumentType.getEntity(context, "target");
        if (ent instanceof LivingEntity entity) {
            return entity;
        } else {
            throw livingEntityException.create();
        }
    }
}
