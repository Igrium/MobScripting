package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;

public class SimpleScriptRoutineType<T extends ScriptRoutine> extends ScriptRoutineType<T> {

    public static interface ScriptRoutineFactory<T extends ScriptRoutine> {
        public T create(ScriptRoutineType<T> type, EntityScriptComponent component);
    }

    private final ScriptRoutineFactory<T> factory;

    public SimpleScriptRoutineType(ScriptRoutineFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T create(EntityScriptComponent component) {
        return factory.create(this, component);
    }
    
}
