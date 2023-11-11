package com.igrium.mobscripting.routine;

import com.igrium.mobscripting.EntityScriptComponent;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class ScriptRoutineType<T extends ScriptRoutine> {
    public static final RegistryKey<Registry<ScriptRoutineType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier("mob-scription:routines"));
    public static final SimpleRegistry<ScriptRoutineType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static interface RoutineFactory<T extends ScriptRoutine> {
        public T create(ScriptRoutineType<T> type, EntityScriptComponent component);
    }

    private final RoutineFactory<T> factory;

    public ScriptRoutineType(RoutineFactory<T> factory) {
        this.factory = factory;
    }

    public T create(EntityScriptComponent component) {
        return factory.create(this, component);
    }

    public Identifier getID() {
        Identifier id = REGISTRY.getId(this);
        if (id == null)
            throw new IllegalStateException("This routine type hasn't been registered!");
        return id;
    }

    public static <T extends ScriptRoutine> ScriptRoutineType<T> register(Identifier id, ScriptRoutineType<T> type) {
        return Registry.register(REGISTRY, id, type);
    }

    public static <T extends ScriptRoutine> ScriptRoutineType<T> register(Identifier id, RoutineFactory<T> factory) {
        return register(id, new ScriptRoutineType<>(factory));
    }

    // Empty method for class loader
    public static void createRegistry() {

    }

    public static final ScriptRoutineType<TestRoutine> TEST_ROUTINE = register(new Identifier("mob-scripting"), TestRoutine::new);
}
