package com.igrium.mobscripting;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.igrium.collections.PerceptibleList;
import com.igrium.mobscripting.routine.ScriptRoutine;
import com.igrium.mobscripting.routine.ScriptRoutineType;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;


public class EntityScriptComponent implements ServerTickingComponent {

    public static EntityScriptComponent get(LivingEntity entity) {
        return MobScriptingComponents.ENTITY_SCRIPT.get(entity);
    }

    private final LivingEntity entity;

    private List<ScriptRoutine> routines = new LinkedList<>();

    private PerceptibleList<ScriptRoutine> perceptibleRoutines = new PerceptibleList<ScriptRoutine>(routines,
            this::onAddedRoutine, this::onRemovedRoutine);

    /**
     * We keep a queue of routines needing their startup script called instead of
     * calling them immedietly so we can be assured that all routine shit happens
     * together.
     */
    private Queue<ScriptRoutine> startupQueue = new LinkedList<>();


    public EntityScriptComponent(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * Get the entity these scripts control.
     * @return Owning entity.
     */
    public LivingEntity getEntity() {
        return entity;
    }

    public List<ScriptRoutine> routines() {
        return perceptibleRoutines;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void readFromNbt(NbtCompound tag) {
        NbtList list = tag.getList("routines", NbtElement.COMPOUND_TYPE);
        routines.clear();

        NbtCompound item;
        for (NbtElement element : list) {
            item = (NbtCompound) element;

            Identifier id = new Identifier(item.getString("id"));
            ScriptRoutineType<?> type = ScriptRoutineType.REGISTRY.get(id);
            if (type == null) {
                MobScripting.LOGGER.warn("Unknown routine type: " + id);
                continue;
            }
            ScriptRoutine routine = type.create(this);
            routine.readFromNbt(item);
            routine.setRunning(true);
            routines.add(routine);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();

        for (ScriptRoutine routine : routines) {
            NbtCompound nbt = new NbtCompound();
            routine.writeToNbt(nbt);
            
            nbt.putString("id", routine.getType().getID().toString());
            list.add(nbt);
        }

        tag.put("routines", list);
    }

    public void startup() {
        for (ScriptRoutine routine : routines) {
            routine.start();
        }
    }

    public void shutdown() {
            for (ScriptRoutine routine : routines) {
            routine.stop(true);
        }
    }

    @Override
    public void serverTick() {
        while (!startupQueue.isEmpty()) {
            startupQueue.poll().start();
        }

        Iterator<ScriptRoutine> iterator = routines.iterator();
        ScriptRoutine routine;
        
        while (iterator.hasNext()) {
            routine = iterator.next();
            
            if (routine.isRunning()) {
                routine.tick();
            } else {
                iterator.remove();
            }
        }
    }

    protected void onAddedRoutine(ScriptRoutine routine) {
        startupQueue.add(routine);
    }

    protected void onRemovedRoutine(Object item) {
        if (item instanceof ScriptRoutine routine) {
            routine.stop(false);
        }
    }

    // @Override
    // public void loadServerside() {
    //     startup();
    // }

    // @Override
    // public void unloadServerside() {
    //     shutdown();
    // }
    
    // public void addRoutine(ScriptRoutine routine) throws IllegalStateException {
    //     if (routine.isRunning()) {
    //         throw new IllegalStateException("Routine is already running.");
    //     }
    //     if (routine.getEntityComponent() != this) {
    //         throw new IllegalStateException("Routine belongs to the wrong component.");
    //     }

    //     routines.add(routine);
    //     startupQueue.add(routine);
    // }

    // public void clearRoutines() {
    //     for (ScriptRoutine routine : routines) {
    //         routine.stop();
    //     }
    //     routines.clear();
    // }
}
