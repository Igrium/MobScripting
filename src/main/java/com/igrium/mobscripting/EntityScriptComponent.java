package com.igrium.mobscripting;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;


public class EntityScriptComponent implements Component {

    private final Entity entity;

    public EntityScriptComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readFromNbt'");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToNbt'");
    }

}
