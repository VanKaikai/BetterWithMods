package betterwithmods.module.hardcore.world.buckets;

import betterwithmods.BetterWithMods;
import betterwithmods.common.blocks.BlockIce;
import betterwithmods.library.common.block.creation.BlockEntryBuilderFactory;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.module.general.General;
import betterwithmods.module.internal.BlockRegistry;
import betterwithmods.util.PlayerUtils;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Created by primetoxinz on 4/20/17.
 */
@Mod.EventBusSubscriber
public class HCBuckets extends Feature {
    public static boolean modifyDispenserBehavior, stopDispenserFillBehavior;
    private static List<String> fluidWhitelist;
    private static List<ResourceLocation> fluidcontainerBacklist;
    private static List<Integer> dimensionBlacklist;

    @Override
    public String getDescription() {
        return "Makes it so water buckets cannot move an entire source block, making water a more valuable resource";
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {


        dimensionBlacklist = Ints.asList(loadProperty("Dimension Black List", new int[]{DimensionType.THE_END.getId()}).setComment("A List of dimension ids in which water buckets will work normally. This is done in the End by default to make Enderman Farms actually reasonable to createBlock.").get());

        fluidWhitelist = Lists.newArrayList(loadProperty("Fluid Whitelist", new String[]{
                FluidRegistry.WATER.getName(),
                FluidRegistry.LAVA.getName(),
                "swamp_water",
                "milk",
                "stagnant_water",
                "acid",
                "sludge",
                "ale",
                "alewort",
                "applejuice",
                "cider",
                "grapejuice",
                "honey",
                "ironberryjuice",
                "ironwine",
                "mead",
                "oliveoil",
                "wildberryjuice",
                "wildberrywine",
                "wine",
                "blood",
                "purpleslime",
        }).setComment("List of fluids that will be handled by HCBuckets.").get());
        boolean fixIce = loadProperty("Fix ice", true).setComment("Replace ice block so that it does not place water sources when it melts or is broken.").get();
        modifyDispenserBehavior = loadProperty("Modify dispenser behavior", true).setComment("Change how the Dispenser handles buckets when activated.").get();
        stopDispenserFillBehavior = loadProperty("Stop Dispenser Fill Behavior", false).setComment("Disallow the dispenser from using an empty bucket for anything.").get();

        if (fixIce) {
            BlockRegistry.registerBlocks(BlockEntryBuilderFactory.<Void>create().builder().block(new BlockIce().setTranslationKey("ice")).id("minecraft:ice").build().complete()
            );
        }
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        //TODO dispenser behavior; for water and lava bucket

        fluidcontainerBacklist = config().loadResouceLocations("Fluid container blacklist", getCategory(), "Blacklist itemstacks from being effected by HCBuckets", new String[]{
                "thermalcultivation:watering_can"
        });

        if (modifyDispenserBehavior) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.BUCKET, BehaviorFluidContainer.getInstance());
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.LAVA_BUCKET, BehaviorFluidContainer.getInstance());
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.WATER_BUCKET, BehaviorFluidContainer.getInstance());
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.MILK_BUCKET, BehaviorFluidContainer.getInstance());

            if (FluidRegistry.isUniversalBucketEnabled()) {
                Item item = ForgeModContainer.getInstance().universalBucket;
                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, BehaviorFluidContainer.getInstance());
            }
        }
    }


    @SubscribeEvent
    public static void onInteractFluidHandlerItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        IFluidHandlerItem handlerItem = FluidUtil.getFluidHandler(stack);
        if (handlerItem != null) {
            //Don't need to do buckets
            if (stack.getItem() instanceof ItemBucket)
                return;

            if (fluidcontainerBacklist.contains(stack.getItem().getRegistryName()))
                return;

            FluidStack contained = FluidUtil.getFluidContained(stack);

            RayTraceResult raytraceresult = stack.getItem().rayTrace(event.getWorld(), event.getEntityPlayer(), contained == null);

            ActionResult<ItemStack> actionResult = ForgeEventFactory.onBucketUse(event.getEntityPlayer(), event.getWorld(), stack, raytraceresult);
            if (actionResult != null && actionResult.getType() == EnumActionResult.SUCCESS) {
                if (stack != actionResult.getResult()) {
                    event.getEntityPlayer().setHeldItem(event.getHand(), actionResult.getResult());
                    event.setCanceled(true);
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseFluidContainer(FillBucketEvent event) {
        if (event.isCanceled()) return;
        if (event.getTarget() != null) {
            ItemStack container = event.getEmptyBucket();
            RayTraceResult raytraceresult = event.getTarget();
            BlockPos pos = raytraceresult.getBlockPos();
            World world = event.getWorld();
            EntityPlayer player = event.getEntityPlayer();
            if (!PlayerUtils.isSurvival(player))
                return;


            //Skip blacklisted fluidcontainers
            if (fluidcontainerBacklist.contains(container.getItem().getRegistryName()))
                return;

            FluidStack fluidStack = FluidUtil.getFluidContained(container);
            //Ignore blacklisted dimensions
            if (dimensionBlacklist.contains(world.provider.getDimension()))
                return;

            //Only use whitelisted fluids.
            if (fluidStack != null && !fluidWhitelist.contains(fluidStack.getFluid().getName())) {
                return;
            }

            //Attempt to pick up a BlockFluidBase or BlockLiquidBase using our custom wrappers.
            FluidActionResult result = BucketsUtils.tryPickUpFluid(container, player, world, pos, raytraceresult.sideHit);


            if (result.isSuccess()) {
                if (player.getCooldownTracker().hasCooldown(container.getItem())) {
                    event.setCanceled(true);
                    return;
                }

                ItemStack filledContainer = result.getResult();
                FluidStack filledFluidStack = FluidUtil.getFluidContained(filledContainer);

                if (filledFluidStack != null && !fluidWhitelist.contains(filledFluidStack.getFluid().getName())) {
                    event.setResult(Event.Result.DEFAULT);
                    return;
                }

                event.setResult(Event.Result.ALLOW);
                event.setFilledBucket(result.getResult());
                //Add a cool down so you cannot pickup fluid from small puddle made when dumping it.
                //(Stops you from using a single bucket to traverse a lava pool)
                player.getCooldownTracker().setCooldown(container.getItem(), 20);
            } else {
                //No fluid was found, try to place one instead
                BlockPos offset = pos.offset(raytraceresult.sideHit);
                IBlockState state = world.getBlockState(offset);
                if (state.getMaterial().isReplaceable()) {

                    if (fluidStack != null) {
                        if (fluidStack.amount == Fluid.BUCKET_VOLUME) {
                            //Try to place the fluid using our custom wrappers again, does not createBlock a source block.
                            FluidActionResult placeResult = BucketsUtils.tryPlaceFluid(player, world, offset, container, fluidStack);
                            if (placeResult.isSuccess()) {
                                event.setResult(Event.Result.ALLOW);
                                event.setFilledBucket(placeResult.getResult());
                            }
                        }
                    }
                } else if (!state.getMaterial().isOpaque()) {
                    //Can't place it here.
                    event.setCanceled(true);
                }
            }

            if (General.isDebug()) {
                event.getEntityPlayer().sendMessage(new TextComponentTranslation("FillBucketEvent: %s,%s,%s,%s", event.getTarget().getBlockPos(), event.getTarget().typeOfHit, event.getEmptyBucket() != null ? event.getEmptyBucket().getDisplayName() : null, event.getFilledBucket() != null ? event.getFilledBucket().getDisplayName() : null));
                BetterWithMods.getLog().info("FillBucketEvent: {}, {}, {}", event.getTarget(), event.getEmptyBucket(), event.getFilledBucket());
            }

        }
    }

}
