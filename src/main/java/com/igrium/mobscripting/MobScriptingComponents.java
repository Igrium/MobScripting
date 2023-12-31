package com.igrium.mobscripting;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class MobScriptingComponents implements EntityComponentInitializer {

    public static final ComponentKey<EntityScriptComponent> ENTITY_SCRIPT = ComponentRegistry
            .getOrCreate(new Identifier("mob-scripting:entity_script"), EntityScriptComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, ENTITY_SCRIPT, EntityScriptComponent::new);
    }
    
}
