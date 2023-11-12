package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public abstract class ScriptRoutineType<T extends ScriptRoutine> {
    public static final RegistryKey<Registry<ScriptRoutineType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier("mob-scription:routines"));
    public static final SimpleRegistry<ScriptRoutineType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public abstract T create(EntityScriptComponent component);


    public Identifier getID() {
        Identifier id = REGISTRY.getId(this);
        if (id == null)
            throw new IllegalStateException("This routine type hasn't been registered!");
        return id;
    }

    public static <T extends ScriptRoutine> ScriptRoutineType<T> register(Identifier id, ScriptRoutineType<T> type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static <T extends ScriptRoutine> ScriptRoutineType<T> register(Identifier id, SimpleScriptRoutineType.ScriptRoutineFactory<T> factory) {
        return register(id, new SimpleScriptRoutineType<>(factory));
    }

    // Empty method for class loader
    public static void createRegistry() {

    }

    public static final ScriptRoutineType<TestRoutine> TEST_ROUTINE = register(new Identifier("mob-scripting:test"), TestRoutine::new);
    public static final ScriptRoutineType<TargetRoutine> TARGET = register(new Identifier("mob-scripting:target"), new TargetRoutine.Type());
}
