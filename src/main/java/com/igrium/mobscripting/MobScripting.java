package com.igrium.mobscripting;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igrium.mobscripting.commands.RoutineCommand;
import com.igrium.mobscripting.routine.ScriptRoutineType;

public class MobScripting implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mob-scripting");

    @Override
    public void onInitialize() {
        ScriptRoutineType.createRegistry();

        CommandRegistrationCallback.EVENT.register(RoutineCommand::register);
    }
}