package com.igrium.mobscripting.commands;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.routine.ScriptRoutineType;
import com.igrium.mobscripting.routine.TargetRoutine;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public class TestCommands {
    private static SimpleCommandExceptionType livingEntityException = new SimpleCommandExceptionType(
            Text.literal("Entity must be a living entity."));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        
        dispatcher.register(literal("routine_test").then(
            argument("subject", EntityArgumentType.entity()).then(
                literal("target").then(
                    argument("target", EntityArgumentType.entity()).executes(TestCommands::target)
                )
            )
        ));
    }

    private static int target(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LivingEntity subject = castToLivingEntity(EntityArgumentType.getEntity(context, "subject"));
        LivingEntity target = castToLivingEntity(EntityArgumentType.getEntity(context, "target"));

        EntityScriptComponent component = EntityScriptComponent.get(subject);
        TargetRoutine routine = ScriptRoutineType.TARGET.create(component);
        routine.setTarget(target);

        component.routines().add(routine);
        return 1;
    }

    private static LivingEntity castToLivingEntity(Entity entity) throws CommandSyntaxException {
        if (entity instanceof LivingEntity living) {
            return living;
        } else {
            throw livingEntityException.create();
        }
    }
}
