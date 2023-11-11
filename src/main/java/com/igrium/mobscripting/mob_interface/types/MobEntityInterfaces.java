package com.igrium.mobscripting.mob_interface.types;

import org.jetbrains.annotations.Nullable;

import com.igrium.mobscripting.mob_interface.Targetable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class MobEntityInterfaces implements Targetable {
    protected final MobEntity entity;

    public MobEntityInterfaces(MobEntity entity) {
        this.entity = entity;
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return entity.getTarget();
    }

    @Override
    public boolean setTarget(@Nullable LivingEntity target, boolean force) {
        if (target == null) {
            entity.setTarget(null);
            return true;
        }
        if (!entity.canTarget(target) && !force) return false;
        if (!target.isAlive()) return false;
        entity.setTarget(target);
        return entity.getTarget() == target;
    }
    
}
