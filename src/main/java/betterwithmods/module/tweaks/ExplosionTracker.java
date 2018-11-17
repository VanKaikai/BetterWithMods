package betterwithmods.module.tweaks;

import betterwithmods.BetterWithMods;
import betterwithmods.library.common.modularity.impl.Feature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;


public class ExplosionTracker extends Feature {

    @SubscribeEvent
    public static void onExplodeTrack(ExplosionTrackingEvent event) {
        Optional<EntityLivingBase> entity = Optional.ofNullable(event.getExploder());
        BetterWithMods.getLog().warn("[EXPLOSION] -  position: {}, cause: {}", event.getSource(), entity.map(EntityLivingBase::getName).orElse("No Placer"));
    }

    @SubscribeEvent
    public void onExplode(ExplosionEvent.Start event) {
        MinecraftForge.EVENT_BUS.post(new ExplosionTrackingEvent(event.getWorld(), event.getExplosion()));
    }

    @Override
    public String getDescription() {
        return "Small server tweak for logging explosion sources";
    }

    public static class ExplosionTrackingEvent extends Event {
        private Vec3d source;
        private EntityLivingBase exploder;
        private World world;

        public ExplosionTrackingEvent(World world, Explosion explosion) {
            this.world = world;
            this.exploder = explosion.getExplosivePlacedBy();
            this.source = explosion.getPosition();
        }

        public ExplosionTrackingEvent(Vec3d source, EntityLivingBase exploder, World world) {
            this.source = source;
            this.exploder = exploder;
            this.world = world;
        }

        public Vec3d getSource() {
            return source;
        }

        public EntityLivingBase getExploder() {
            return exploder;
        }

        public World getWorld() {
            return world;
        }
    }

    @Override
    public boolean hasEvent() {
        return true;
    }
}
