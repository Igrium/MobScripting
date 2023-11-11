package com.igrium.mobscripting.routine;

import java.util.UUID;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.mob_interface.MobInterfaceType;
import com.igrium.mobscripting.mob_interface.Targetable;
import com.mojang.logging.LogUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public class TargetRoutine extends ScriptRoutine {

    private UUID target;
    private Entity lastTarget;

    private Targetable targetable;

    public TargetRoutine(ScriptRoutineType<?> type, EntityScriptComponent entityComponent) {
        super(type, entityComponent);
        this.targetable = MobInterfaceType.TARGETABLE.getForEntity(getEntity());
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public void setTarget(Entity target) {
        this.target = target.getUuid();
    }

    @Override
    protected void onStart() {
    
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        super.writeToNbt(nbt);
        nbt.putUuid("target", target);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        super.readFromNbt(nbt);
        nbt.putUuid("target", target);
    }

    @Override
    protected void onTick() {
        if (this.target == null) {
            stop(false);
            return;
        }

        LivingEntity target = (LivingEntity) getServerWorld().getEntity(this.target);
        if (target != lastTarget) {
            if (targetable == null) {
                noTarget();
                return;
            }
            targetable.setTarget(target, false);
            lastTarget = target;
        }

        // If the mob AI changed the target on its own
        if (!targetable.getTarget().equals(target)) {
            stop(false);
        }
    }

    @Override
    protected void onStop(boolean interrupted) {
        LogUtils.getLogger().info("Target routine complete!");
    }
    
    private void noTarget() {
        LogUtils.getLogger().error("Entity does not have Targetable interface. Shutting down routine.");
        stop(true);
    }
}
