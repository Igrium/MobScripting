package com.igrium.mobscripting.mob_interface;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;

/**
 * A mob that can have its attack target manipulated.
 */
public interface Targetable {
    
    @Nullable
    /**
     * Get the entity that this mob is currently targeting.
     * @return Current target. <code>null</code> if there is no target.
     */
    public LivingEntity getTarget();

    /**
     * Set the target entity.
     * @param target New target entity. <code>null</code> to forget the current target.
     * @param force Indicate to this mob that it is not allowed to "get distracted" and start persuing other entities. The actual effect of this depends on the implementation.
     * @return If the mob successfully changed its target.
     */
    public boolean setTarget(@Nullable LivingEntity target, boolean force);
}
