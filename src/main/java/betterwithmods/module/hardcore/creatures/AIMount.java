package betterwithmods.module.hardcore.creatures;

import betterwithmods.library.network.NetworkHandler;
import betterwithmods.network.BWMNetwork;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;

public class AIMount extends EntityAIBase {

    public final EntityLiving rider;
    private final int range;
    public EntityLivingBase target;

    public AIMount(EntityLiving rider, int range) {
        this.rider = rider;
        this.range = range;
    }

    @Override
    public boolean shouldExecute() {
        target = rider.getAttackTarget();
        return !rider.isRiding() && target != null && rider.getDistanceSq(target) < range;
    }

    @Override
    public void startExecuting() {
        if (rider.world.isRemote)
            return;
        if (!target.isBeingRidden()) {
            if (rider.startRiding(target, true)) {
                if (target instanceof EntityPlayerMP) {
                    BWMNetwork.INSTANCE.sendPacket(target, new SPacketSetPassengers(target));
                }
            }
        }
    }
}
