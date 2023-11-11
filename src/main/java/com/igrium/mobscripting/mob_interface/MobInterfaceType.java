package com.igrium.mobscripting.mob_interface;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.igrium.mobscripting.mob_interface.types.MobEntityInterfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

/**
 * Provides a standardized interface to communicate with native mob functions from script routines.
 */
public final class MobInterfaceType<T> {
    private Map<Class<?>, InterfaceFactory<?, T>> registry = new HashMap<>();

    private static record InterfaceFactory<E extends LivingEntity, T>(Class<E> entClass,
            Function<? super E, ? extends T> factory) {
        
        public T create(LivingEntity entity) {
            E castEntity = entClass().cast(entity);
            return factory.apply(castEntity);
        }
    }

    private WeakHashMap<LivingEntity, T> cache = new WeakHashMap<>();


    /**
     * Register an interface implementation for a given mob type.
     * 
     * @param <E>     Mob type.
     * @param type    Mob entity class.
     * @param factory Interface constructor.
     */
    public <E extends LivingEntity> void register(Class<E> type, Function<? super E, ? extends T> factory) {
        registry.put(type, new InterfaceFactory<>(type, factory));
    }

    /**
     * Get this interface implementation for a given entity. Begins with the entity
     * class and searches up the class hierarchy until a compatible implementation
     * is found, therefore finding the most specific implementation for any given
     * entity.
     * 
     * @param entity Entity to use.
     * @return The interface implementation. <code>null</code> if no compatible
     *         implementation was found.
     */
    @Nullable
    public T getForEntity(LivingEntity entity) {
        var cached = cache.get(entity);
        if (cached != null) {
            return cached;
        }

        var factory = getFactory(entity.getClass());
        if (factory == null)
            return null;

        T impl = factory.create(entity);
        cache.put(entity, impl);
        return impl;
    }

    @Nullable
    private InterfaceFactory<?, T> getFactory(Class<?> clazz) {
        var factory = registry.get(clazz);

        if (factory == null) {
            Class<?> parent = clazz.getSuperclass();
            return parent != null ? getFactory(parent) : null;
        }

        return factory;
    }

    /**
     * An mob that can target & attack other entities.
     */
    public static final MobInterfaceType<Targetable> TARGETABLE = new MobInterfaceType<>();
    static {
        TARGETABLE.register(MobEntity.class, MobEntityInterfaces::new);
    }
}
