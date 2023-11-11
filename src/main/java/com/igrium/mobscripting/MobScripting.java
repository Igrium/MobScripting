package com.igrium.mobscripting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.mobscripting.commands.RoutineCommand;
import com.igrium.mobscripting.commands.TestCommands;
import com.igrium.mobscripting.routine.ScriptRoutineType;

public class MobScripting implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mob-scripting");

    @Override
    public void onInitialize() {
        ScriptRoutineType.createRegistry();

        CommandRegistrationCallback.EVENT.register(RoutineCommand::register);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(TestCommands::register);
        }
    }
}