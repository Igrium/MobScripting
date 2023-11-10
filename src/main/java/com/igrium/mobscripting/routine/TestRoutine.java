package com.igrium.mobscripting.routine;

import org.jline.utils.Log;
import org.slf4j.Logger;

import com.igrium.mobscripting.EntityScriptComponent;
import com.mojang.logging.LogUtils;

public class TestRoutine extends ScriptRoutine {

    private static final Logger LOGGER = LogUtils.getLogger();

    public TestRoutine(ScriptRoutineType<?> type, EntityScriptComponent entityComponent) {
        super(type, entityComponent);
    }

    @Override
    protected void onStart(boolean resume) {
        LOGGER.info("Starting test routine. Resume mode: " + resume);
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
    protected void onShutdown(boolean removed) {
        Log.info("Routine shutting down!");
    }
    
}
