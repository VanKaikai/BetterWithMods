package betterwithmods.module.conversion.beacons;

import betterwithmods.lib.ModLib;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.library.utils.ListUtils;
import betterwithmods.library.utils.ingredient.blockstate.BlockStateIngredient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class BeaconEffect {

    protected boolean enabled;

    protected ResourceLocation resourceLocation;
    protected BlockStateIngredient structureBlock;
    protected Class<? extends EntityLivingBase> validEntityType;
    protected float[] baseBeamColor;
    protected int[] effectRanges;
    protected SoundEvent activationSound, deactivationSound;
    protected int tickRate;

    public BeaconEffect(String name, BlockStateIngredient structureBlock, Class<? extends EntityLivingBase> validEntityType) {
        this.resourceLocation = new ResourceLocation(ModLib.MODID, name + "_beacon");
        this.structureBlock = structureBlock;
        this.validEntityType = validEntityType;
        this.enabled = true;
        this.effectRanges = new int[]{20, 40, 80, 160};
        this.setBaseBeamColor(Color.white);
        this.setActivationSound(SoundEvents.ENTITY_WITHER_SPAWN);
        this.setDeactivationSound(SoundEvents.ENTITY_WITHER_DEATH);
        this.setTickRate(120);
    }

    public void setupConfig(Feature feature) {
        this.setEnabled(feature.loadProperty("Enabled", true).subCategory(this.resourceLocation.getPath()).get());

        if (effectRanges != null) {
            this.setEffectRanges(feature.loadProperty("Effect Ranges", this.effectRanges).setComment("Range, in blocks, that the beacon will have an effect").get());
            this.setTickRate(feature.loadProperty("Tick Rate", tickRate).setComment("The rate, in ticks, that the beacon will update and apply its effect.").get());
        }
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public BeaconEffect setBaseBeamColor(Color baseBeamColor) {
        this.baseBeamColor = new float[]{baseBeamColor.getRed() / 255, baseBeamColor.getGreen() / 255, baseBeamColor.getBlue() / 255};
        return this;
    }

    public BeaconEffect setBaseBeamColor(float[] baseBeamColor) {
        this.baseBeamColor = baseBeamColor;
        return this;
    }

    public SoundEvent getDeactivationSound() {
        return deactivationSound;
    }

    public BeaconEffect setDeactivationSound(SoundEvent deactivationSound) {
        this.deactivationSound = deactivationSound;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public BeaconEffect setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public int[] getEffectRanges() {
        return effectRanges;
    }

    public BeaconEffect setEffectRanges(int[] effectRanges) {
        this.effectRanges = effectRanges;
        return this;
    }

    public int getTickRate() {
        return tickRate;
    }

    public BeaconEffect setTickRate(int tickRate) {
        this.tickRate = tickRate;
        return this;
    }

    public SoundEvent getActivationSound() {
        return activationSound;
    }

    public BeaconEffect setActivationSound(SoundEvent activationSound) {
        this.activationSound = activationSound;
        return this;
    }

    public BlockStateIngredient getStructureBlock() {
        return structureBlock;
    }

    public Class<? extends EntityLivingBase> getValidEntityType() {
        return validEntityType;
    }

    public float[] getBaseBeaconBeamColor(BlockPos beaconPos) {
        return baseBeamColor;
    }

    public boolean isBlockStateValid(World world, BlockPos pos, IBlockState blockState) {
        return structureBlock.test(world, pos, blockState);
    }

    public NonNullList<EntityLivingBase> getEntitiesInRange(World world, BlockPos pos, int beaconLevel) {
        int radius = effectRanges[Math.min(beaconLevel - 1, 3)];
        AxisAlignedBB box = new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(radius);
        return ListUtils.asNonnullList(world.getEntitiesWithinAABB(getValidEntityType(), box));
    }

    public abstract void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel);

    public abstract void apply(NonNullList<EntityLivingBase> entitiesInRange, @Nonnull World world, @Nonnull BlockPos pos, int beaconLevel);

    public abstract boolean onPlayerInteracted(World world, BlockPos pos, int level, EntityPlayer player, EnumHand hand, ItemStack stack);

    public abstract void onBeaconBreak(World world, BlockPos pos, int level);


}
