package com.igrium.mobscripting.routine;

import java.util.UUID;
import java.util.Objects;

import com.igrium.mobscripting.EntityScriptComponent;
import com.igrium.mobscripting.mob_interface.MobInterfaceType;
import com.igrium.mobscripting.mob_interface.Targetable;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TargetRoutine extends ScriptRoutine {

    public static class Type extends ComplexScriptRoutineType<TargetRoutine> {

        @Override
        public TargetRoutine createWithArgs(CommandContext<ServerCommandSource> context, EntityScriptComponent component) throws CommandSyntaxException {
            TargetRoutine routine = new TargetRoutine(this, component);
            routine.setTarget(EntityArgumentType.getEntity(context, "targetEnt"));
            return routine;
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> getArgumentBuilder(BrigadierExitpointAppender exit) {
            return exit.exit(CommandManager.argument("targetEnt", EntityArgumentType.entity()));
        }

        @Override
        public TargetRoutine create(EntityScriptComponent component) {
            return new TargetRoutine(this, component);
        }

    }

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
        this.target = nbt.getUuid("target");
    }

    @Override
    protected void onTick() {
        if (this.target == null) {
            stop(false);
            return;
        }

        LivingEntity target = (LivingEntity) getServerWorld().getEntity(this.target);
        if (target == null) {
            stop(false);
            return;
        }

        if (target != lastTarget) {
            if (targetable == null) {
                noTarget();
                return;
            }
            targetable.setTarget(target, false);
            lastTarget = target;
        }

        // If the mob AI changed the target on its own
        if (!Objects.equals(targetable.getTarget(), target)) {
            stop(false);
        }
    }

    @Override
    protected void onStop(boolean interrupted) {
        LogUtils.getLogger().info("Target routine complete!");
        if (Objects.equals(lastTarget, targetable.getTarget())) {
            targetable.setTarget(null, false);
        }
    }
    
    private void noTarget() {
        LogUtils.getLogger().error("Entity does not have Targetable interface. Shutting down routine.");
        stop(true);
    }
}
