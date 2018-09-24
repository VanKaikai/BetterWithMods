package betterwithmods.module.hardcore.creatures;

import betterwithmods.lib.ModLib;
import betterwithmods.library.modularity.impl.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HCSheep extends Feature {
    public static final ResourceLocation NATURAL_COLOR = new ResourceLocation(ModLib.MODID, "natural_color");
    private static final HashMap<NaturalColorMix, EnumDyeColor> COLOR_MIX_TABLE = new HashMap<>();
    private static final ArrayList<EnumDyeColor> MUTATION_COLORS = new ArrayList<>();
    @CapabilityInject(NaturalColor.class)
    public static Capability<NaturalColor> NATURAL_COLOR_CAP;
    private static int mutationChance = 500;

    public static void addMutation(EnumDyeColor color) {
        MUTATION_COLORS.add(color);
    }

    public static void addShapedColorMixing(EnumDyeColor colorA, EnumDyeColor colorB, EnumDyeColor result) {
        COLOR_MIX_TABLE.put(new NaturalColorMix(colorA, colorB), result);
    }

    public static void addShapelessColorMixing(EnumDyeColor colorA, EnumDyeColor colorB, EnumDyeColor result) {
        COLOR_MIX_TABLE.put(new NaturalColorMix(colorA, colorB), result);
        COLOR_MIX_TABLE.put(new NaturalColorMix(colorB, colorA), result);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        mutationChance = loadProperty("Mutation Chance", 500).setComment("How likely a sheep is to mutate into a weird natural color. Chance is 1 in n. Default mirrors vanilla chance to obtain pink sheep.").get();

        CapabilityManager.INSTANCE.register(NaturalColor.class, new Capability.IStorage<NaturalColor>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<NaturalColor> capability, NaturalColor instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<NaturalColor> capability, NaturalColor instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, NaturalColor::new);

        addMutation(EnumDyeColor.BLACK);
        addMutation(EnumDyeColor.LIME);
        addMutation(EnumDyeColor.LIGHT_BLUE);
        addMutation(EnumDyeColor.PINK);

        //Looks like it's this way in BTW
        addShapedColorMixing(EnumDyeColor.BLACK, EnumDyeColor.WHITE, EnumDyeColor.SILVER);
        addShapedColorMixing(EnumDyeColor.WHITE, EnumDyeColor.BLACK, EnumDyeColor.GRAY);
        //Darkening colors
        addShapelessColorMixing(EnumDyeColor.BLACK, EnumDyeColor.SILVER, EnumDyeColor.GRAY);
        addShapelessColorMixing(EnumDyeColor.BLACK, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.BLUE);
        addShapelessColorMixing(EnumDyeColor.BLACK, EnumDyeColor.LIME, EnumDyeColor.GREEN);
        addShapelessColorMixing(EnumDyeColor.BLACK, EnumDyeColor.PINK, EnumDyeColor.RED);
        //Lighting colors
        addShapelessColorMixing(EnumDyeColor.WHITE, EnumDyeColor.GRAY, EnumDyeColor.SILVER);
        addShapelessColorMixing(EnumDyeColor.WHITE, EnumDyeColor.BLUE, EnumDyeColor.LIGHT_BLUE);
        addShapelessColorMixing(EnumDyeColor.WHITE, EnumDyeColor.GREEN, EnumDyeColor.LIME);
        addShapelessColorMixing(EnumDyeColor.WHITE, EnumDyeColor.RED, EnumDyeColor.PINK);
        //Primary color combinations
        addShapelessColorMixing(EnumDyeColor.RED, EnumDyeColor.BLUE, EnumDyeColor.PURPLE);
        addShapelessColorMixing(EnumDyeColor.BLUE, EnumDyeColor.GREEN, EnumDyeColor.CYAN);
        addShapelessColorMixing(EnumDyeColor.GREEN, EnumDyeColor.RED, EnumDyeColor.YELLOW);
        //Secondary color combinations
        addShapelessColorMixing(EnumDyeColor.RED, EnumDyeColor.YELLOW, EnumDyeColor.ORANGE);
        addShapelessColorMixing(EnumDyeColor.PURPLE, EnumDyeColor.WHITE, EnumDyeColor.MAGENTA);
        addShapelessColorMixing(EnumDyeColor.RED, EnumDyeColor.LIGHT_BLUE, EnumDyeColor.MAGENTA);
        addShapelessColorMixing(EnumDyeColor.CYAN, EnumDyeColor.RED, EnumDyeColor.BROWN);
    }

    @Override
    public String getDescription() {
        return "Sheep can mutate, shearing them resets their wool color to their natural color, breeding them can produce all colors";
    }

    @SubscribeEvent
    public static void sheepCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntitySheep) {
            event.addCapability(NATURAL_COLOR, new NaturalColor());
        }
    }

    @SubscribeEvent
    public static void sheepSpawnEvent(LivingSpawnEvent event) {
        if (event instanceof LivingSpawnEvent.AllowDespawn)
            return;

        World world = event.getEntity().world;

        if (world.isRemote)
            return;

        if (event.getEntity() instanceof EntitySheep) {
            EntitySheep sheep = (EntitySheep) event.getEntity();
            mutateSheep(sheep, event.getWorld().rand);
            setNaturalColor(sheep, sheep.getFleeceColor());
        }
    }

    private static void mutateSheep(EntitySheep sheep, Random random) {
        if (random.nextInt(mutationChance) < 1 && !MUTATION_COLORS.isEmpty()) {
            sheep.setFleeceColor(MUTATION_COLORS.get(random.nextInt(MUTATION_COLORS.size())));
        }
    }

    private static EnumDyeColor getNaturalColor(Entity sheep) {
        if (sheep.hasCapability(NATURAL_COLOR_CAP, null)) {
            NaturalColor color = sheep.getCapability(NATURAL_COLOR_CAP, null);
            return color.color;
        }

        return EnumDyeColor.WHITE;
    }

    private static void setNaturalColor(Entity sheep, EnumDyeColor newcolor) {
        if (sheep.hasCapability(NATURAL_COLOR_CAP, null)) {
            NaturalColor color = sheep.getCapability(NATURAL_COLOR_CAP, null);
            color.color = newcolor;
        }
    }

    private static EnumDyeColor mixNaturalColors(EnumDyeColor colorA, EnumDyeColor colorB, Random random) {
        NaturalColorMix mix = new NaturalColorMix(colorA, colorB);

        if (COLOR_MIX_TABLE.containsKey(mix))
            return COLOR_MIX_TABLE.get(mix);

        return random.nextInt(2) < 1 ? colorA : colorB;
    }

    @SubscribeEvent
    public static void sheepBreedEvent(BabyEntitySpawnEvent event) {
        World world = event.getParentA().world;

        if (world.isRemote)
            return;

        if (event.getParentA() instanceof EntitySheep && event.getParentB() instanceof EntitySheep && event.getChild() instanceof EntitySheep) {
            EntitySheep father = (EntitySheep) event.getParentA();
            EntitySheep mother = (EntitySheep) event.getParentB();

            EntitySheep child = (EntitySheep) event.getChild();

            child.setFleeceColor(mixNaturalColors(getNaturalColor(father), getNaturalColor(mother), world.rand));
            mutateSheep(child, world.rand);
            setNaturalColor(child, child.getFleeceColor());
        }
    }

    @SubscribeEvent
    public static void sheepUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        //Hacky reset for when a sheep is sheared.
        Entity entity = event.getEntity();

        if (!entity.world.isRemote && entity instanceof EntitySheep) {
            EntitySheep sheep = (EntitySheep) entity;
            if (!sheep.getSheared())
                return;
            NaturalColor cap = sheep.getCapability(NATURAL_COLOR_CAP, null);
            if (cap != null) {
                EnumDyeColor naturalColor = cap.color;

                if (sheep.getFleeceColor() != naturalColor) {
                    sheep.setFleeceColor(naturalColor);
                }
            }
        }
    }

    private static class NaturalColorMix {
        final EnumDyeColor colorA;
        final EnumDyeColor colorB;

        public NaturalColorMix(EnumDyeColor colorA, EnumDyeColor colorB) {
            this.colorA = colorA;
            this.colorB = colorB;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NaturalColorMix)
                return ((NaturalColorMix) obj).colorA.equals(colorA) && ((NaturalColorMix) obj).colorB.equals(colorB);

            return false;
        }

        @Override
        public int hashCode() {
            return colorA.getMetadata() | colorB.getMetadata() << 4; //Unlikely that EnumDyeColor will ever change
        }
    }

    public static class NaturalColor implements ICapabilitySerializable<NBTTagCompound> {
        public EnumDyeColor color = EnumDyeColor.WHITE;

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == NATURAL_COLOR_CAP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == NATURAL_COLOR_CAP)
                return NATURAL_COLOR_CAP.cast(this);
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("NaturalColor", color.getMetadata());
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            color = EnumDyeColor.byMetadata(nbt.getInteger("NaturalColor"));
        }
    }
}
