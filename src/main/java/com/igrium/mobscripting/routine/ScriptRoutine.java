package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.MobScripting;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public abstract class ScriptRoutine {

    public static class ScriptRoutineException extends RuntimeException {
        public ScriptRoutineException() {}
        
    }

    private final ScriptRoutineType<?> type;
    private final EntityScriptComponent entityComponent;

    private boolean isRunning;
    private boolean isShuttingDown;

    public ScriptRoutine(ScriptRoutineType<?> type, EntityScriptComponent entityComponent) {
        this.type = type;
        this.entityComponent = entityComponent;
    }

    public final ScriptRoutineType<?> getType() {
        return type;
    }

    public final EntityScriptComponent getEntityComponent() {
        return entityComponent;
    }

    public final LivingEntity getEntity() {
        return getEntityComponent().getEntity();
    }

    public final boolean isRunning() {
        return isRunning;
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("id", type.getID().toString());
    }

    public void readFromNbt(NbtCompound nbt) {

    }

    /**
     * Called when the routine starts up.
     * @param resume If we're resuming a previous serialized routine.
     */
    protected abstract void onStart(boolean resume);

    /**
     * Called every tick while the routine is running.
     */
    protected abstract void onTick();
    
    /**
     * Called when the routine shuts down.
     * 
     * @param removed True if this routine has been removed from the entity. False
     *                if the entity itself is being unloaded or removed.
     */
    protected abstract void onShutdown(boolean removed);

    /**
     * Called when an exception thrown by the routine goes uncaught.
     * 
     * @param e The exception.
     */
    protected void onException(Exception e) {
        MobScripting.LOGGER.error("Error in script routine " + type.getID(), e);
        if (!isShuttingDown) shutdown(true);
    }

    /**
     * Start the routine.
     * @param resume If we're resuming a previous serialized routine.
     */
    public final void start(boolean resume) {
        if (isRunning)
            throw new IllegalStateException("Routine is already running.");

        isRunning = true;
        try {
            onStart(resume);
        } catch (Exception e) {
            onException(e);
        }
    }
    
    public final void tick() {
        try {
            onTick();
        } catch (Exception e) {
            onException(e);
        }
    }
    
    /**
     * Stop the routine.
     */
    public final void shutdown(boolean removed) {
        isShuttingDown = true;
        try {
            onShutdown(false);
        } catch (Exception e) {
            onException(e);
        }
        isRunning = false;
        isShuttingDown = false;
    }
}
