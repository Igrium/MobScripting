package com.igrium.mobscripting.commands;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.routine.ScriptRoutine;
import com.igrium.mobscripting.routine.ScriptRoutineType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
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
                    argument("type", RegistryEntryArgumentType.registryEntry(registryAccess, ScriptRoutineType.REGISTRY.getKey()))
                )
            )
        ));
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity ent = EntityArgumentType.getEntity(context, "target");
        int i = 0;
        if (ent instanceof LivingEntity entity) {
            
            EntityScriptComponent component = EntityScriptComponent.get(entity);
            for (ScriptRoutine routine : component.getRoutines()) {
                context.getSource().sendFeedback(() -> Text.literal(routine.getType().getID().toString()), false);
            }
            i++;
        } else {
            throw livingEntityException.create();
        }
        return i;
    }

    private static int add(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity ent = EntityArgumentType.getEntity(context, "target");
        if (!(ent instanceof LivingEntity)) {
            throw livingEntityException.create();
        }

        LivingEntity entity = (LivingEntity) ent;

    }
    
}
