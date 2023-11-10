package com.igrium.mobscripting;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.igrium.mobscripting.routine.ScriptRoutine;
import com.igrium.mobscripting.routine.ScriptRoutineType;

import dev.onyxstudios.cca.api.v3.component.load.ServerLoadAwareComponent;
import dev.onyxstudios.cca.api.v3.component.load.ServerUnloadAwareComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;


public class EntityScriptComponent implements ServerTickingComponent, ServerLoadAwareComponent, ServerUnloadAwareComponent {

    public static EntityScriptComponent get(LivingEntity entity) {
        return MobScriptingComponents.ENTITY_SCRIPT.get(entity);
    }

    private final LivingEntity entity;

    private List<ScriptRoutine> routines = new LinkedList<>();

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

    public List<ScriptRoutine> getRoutines() {
        return Collections.unmodifiableList(routines);
    }

    @Override
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

            routines.add(type.create(this));
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

    public void startup(boolean resume) {
        for (ScriptRoutine routine : routines) {
            routine.start(resume);
        }
    }

    public void shutdown() {
        for (ScriptRoutine routine : routines) {
            routine.shutdown(false);
        }
    }

    @Override
    public void serverTick() {
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

    @Override
    public void loadServerside() {
        startup(true);
    }

    @Override
    public void unloadServerside() {
        shutdown();
    }
    
}
