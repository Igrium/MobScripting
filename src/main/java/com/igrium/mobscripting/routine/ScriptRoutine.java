package com.igrium.mobscripting.routine;

import org.jetbrains.annotations.Nullable;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.MobScripting;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

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

    @Nullable
    private Identifier completeFunction = null;

    @Nullable
    private Identifier interruptFunction = null;

    public final Identifier getCompleteFunction() {
        return completeFunction;
    }

    public void setCompleteFunction(Identifier completeFunction) {
        this.completeFunction = completeFunction;
    }

    public final Identifier getInterruptFunction() {
        return interruptFunction;
    }

    public void setInterruptFunction(Identifier interruptFunction) {
        this.interruptFunction = interruptFunction;
    }

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

    public ServerWorld getServerWorld() {
        return (ServerWorld) getEntity().getWorld();
    }

    public final boolean isRunning() {
        return isRunning;
    }

    /**
     * <p><strong><em>INTERNAL SERIALIZATION USE ONLY</em></strong></p>
     */
    @Deprecated
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
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
     * 
     * @param interrupted If this routine was interrupted. The exact definition of
     *                    this is up to the calling class.
     */
    protected abstract void onStop(boolean interrupted);

    /**
     * Called when an exception thrown by the routine goes uncaught.
     * 
     * @param e The exception.
     */
    protected void onException(Exception e) {
        MobScripting.LOGGER.error("Error in script routine " + type.getID(), e);
        if (!isShuttingDown) stop(true);
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
     * @param interrupted If this routine was interrupted. The exact definition of
     *                    this is up to the calling class.
     */
    public final void stop(boolean interrupted) {
        isShuttingDown = true;
        try {
            onStop(interrupted);
        } catch (Exception e) {
            onException(e);
        }
        
        isRunning = false;
        isShuttingDown = false;

        CommandFunctionManager functionManager = getEntity().getServer().getCommandFunctionManager();
        if (!interrupted && completeFunction != null) {
            var onComplete = functionManager.getFunction(completeFunction);

            if (onComplete.isPresent()) {
                functionManager.execute(onComplete.get(), getFinishCommandSource());
            } else {
                MobScripting.LOGGER.warn("Script routine had an unknown onComplete function: " + completeFunction);
            }
        }

        if (interrupted && interruptFunction != null) {
            var onInterrupted = functionManager.getFunction(interruptFunction);

            if (onInterrupted.isPresent()) {
                functionManager.execute(null, getFinishCommandSource());
            } else {
                MobScripting.LOGGER.warn("Script routine had an unknown onInterrupt function: " + interruptFunction);
            }
        }
    }

    public ServerCommandSource getFinishCommandSource() {
        return getEntity().getServer().getCommandSource()
                .withEntity(getEntity())
                .withPosition(getEntity().getPos())
                .withRotation(getEntity().getRotationClient())
                .withWorld(getServerWorld())
                .withLevel(2)
                .withSilent();
    }
}
