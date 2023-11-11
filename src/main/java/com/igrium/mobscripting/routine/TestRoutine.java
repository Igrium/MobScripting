package com.igrium.mobscripting.routine;

import org.slf4j.Logger;

import com.igrium.mobscripting.EntityScriptComponent;
import com.mojang.logging.LogUtils;

public class TestRoutine extends ScriptRoutine {

    private static final Logger LOGGER = LogUtils.getLogger();

    public TestRoutine(ScriptRoutineType<?> type, EntityScriptComponent entityComponent) {
        super(type, entityComponent);
    }

    @Override
    protected void onStart() {
        LOGGER.info("Starting test routine.");
    }

    private int tickInterval = 5;

    @Override
    protected void onTick() {
        if (tickInterval <= 0) {
            LOGGER.info("Ticking!");
            tickInterval = 5;
        } else {
            tickInterval--;
        }
    }

    @Override
    protected void onStop() {
        LOGGER.info("Routine shutting down!");
    }
    
}
