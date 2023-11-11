package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.MobScripting;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * A routine that a mob can be made to enact.
 * The general contract with routines is that they're invisibly serializable.
 * That means that there's no guarentee that the same routine object will be
 * maintained throughout the routine's lifetime; it may be saved to NBT and
 * reloaded without notifying code. Therefore, all persistent values should be
 * saved and loaded from NBT accordingly.
 */
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
     */
    protected abstract void onStart();

    /**
     * Called every tick while the routine is running.
     */
    protected abstract void onTick();
    
    /**
     * Called when the routine shuts down.
     */
    protected abstract void onStop();

    /**
     * Called when an exception thrown by the routine goes uncaught.
     * 
     * @param e The exception.
     */
    protected void onException(Exception e) {
        MobScripting.LOGGER.error("Error in script routine " + type.getID(), e);
        if (!isShuttingDown) stop();
    }

    /**
     * Start the routine.
     */
    public final void start() {
        if (isRunning)
            throw new IllegalStateException("Routine is already running.");

        isRunning = true;
        try {
            onStart();
        } catch (Exception e) {
            onException(e);
        }
    }
    
    /**
     * Called every tick.
     */
    public final void tick() {
        // There's a chance this routine is still in the execution queue after shutdown
        if (!isRunning) return;
        try {
            onTick();
        } catch (Exception e) {
            onException(e);
        }
    }
    
    /**
     * Stop the routine.
     */
    public final void stop() {
        isShuttingDown = true;
        try {
            onStop();
        } catch (Exception e) {
            onException(e);
        }
        isRunning = false;
        isShuttingDown = false;
    }
}
