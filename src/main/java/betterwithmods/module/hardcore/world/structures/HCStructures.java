package betterwithmods.module.hardcore.world.structures;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.registry.block.recipe.StateIngredient;
import betterwithmods.library.event.StructureSetBlockEvent;
import betterwithmods.module.Feature;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

/**
 * Created by primetoxinz on 4/20/17.
 */
@Mod.EventBusSubscriber
public class HCStructures extends Feature {
    public static int HARDCORE_STRUCTURE_RADIUS;
    private boolean disableRecipes;

    public static Set<StructureChanger> DESERT_TEMPLE = Sets.newHashSet();
    public static StructureChanger ABANDONED_DESERT_TEMPLE, NORMAL_DESERT_TEMPLE;

    public static Set<StructureChanger> JUNGLE_TEMPLE = Sets.newHashSet();
    public static StructureChanger ABANDONED_JUNGLE_TEMPLE, NORMAL_JUNGLE_TEMPLE;

    public static Set<StructureChanger> WITCH_HUT = Sets.newHashSet();
    public static StructureChanger WITCH_HUT_CHANGER = StructureChanger.create(WITCH_HUT, (w, p) -> true);

    @Override
    public String getDescription() {
        return "Makes it so structures are looted within a radius of spawn and unlooted outside of that radius. Encourages exploration and also makes unlooted structures the only source of Enchanting Tables and Brewing Stands.";
    }


    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        HARDCORE_STRUCTURE_RADIUS = loadProperty("Hardcore Structure Radius", 2000).setComment("Radius from original spawn which structures will be abandoned in").get();
        disableRecipes = loadProperty("Disable Recipes", true).setComment("Disable Recipes for blocks that generate only in structures, including Enchanting Tables and Brewing Stands").get();
        ABANDONED_DESERT_TEMPLE = StructureChanger.create(DESERT_TEMPLE, (w, p) -> p.distanceSq(w.getSpawnPoint()) < HARDCORE_STRUCTURE_RADIUS * HARDCORE_STRUCTURE_RADIUS);
        NORMAL_DESERT_TEMPLE = StructureChanger.create(DESERT_TEMPLE, (w, p) -> p.distanceSq(w.getSpawnPoint()) >= HARDCORE_STRUCTURE_RADIUS * HARDCORE_STRUCTURE_RADIUS);

        ABANDONED_JUNGLE_TEMPLE = StructureChanger.create(JUNGLE_TEMPLE, (w, p) -> p.distanceSq(w.getSpawnPoint()) < HARDCORE_STRUCTURE_RADIUS * HARDCORE_STRUCTURE_RADIUS);
        NORMAL_JUNGLE_TEMPLE = StructureChanger.create(JUNGLE_TEMPLE, (w, p) -> p.distanceSq(w.getSpawnPoint()) >= HARDCORE_STRUCTURE_RADIUS * HARDCORE_STRUCTURE_RADIUS);

        if (disableRecipes) {
            BWMRecipes.removeRecipe(new ItemStack(Blocks.ENCHANTING_TABLE));
            BWMRecipes.removeRecipe(new ItemStack(Items.BREWING_STAND));
        }
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        ABANDONED_DESERT_TEMPLE
                //Dig hole
                .addChanger(new RelativePosChanger(Blocks.AIR.getDefaultState(),
                        new BlockPos(10, 0, 10),
                        new BlockPos(11, 0, 10),
                        new BlockPos(10, 0, 9)))
                //Remove TNT
                .addChanger(new RelativePosChanger(Blocks.SANDSTONE.getDefaultState(), new AxisAlignedBB(9, -13, 9, 11, -13, 11)))
                //Remove pressure plate
                .addChanger(new RelativePosChanger(Blocks.AIR.getDefaultState(), new BlockPos(10, -11, 10)))
                //Place Ladder
                .addChanger(new RelativePosChanger(Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.WEST), new AxisAlignedBB(11, 0, 9, 11, -13, 9)))
                //Deletes loot table
                //TODO This doesn't work as the lootable gets set again later
                .addChanger(new ChestLootChanger())
                //Replace clay with obsidian
                .addChanger(new IngredientChanger(new StateIngredient(Blocks.STAINED_HARDENED_CLAY), Blocks.OBSIDIAN.getDefaultState()));

        NORMAL_DESERT_TEMPLE
                .addChanger(new RelativePosChanger(Blocks.ENCHANTING_TABLE.getDefaultState(), new BlockPos(10, 1, 10)))
                //Replace clay with obsidian
                .addChanger(new IngredientChanger(new StateIngredient(Blocks.STAINED_HARDENED_CLAY), Blocks.OBSIDIAN.getDefaultState()));


        //TODO
        ABANDONED_JUNGLE_TEMPLE
                .addChanger(new IngredientChanger(new StateIngredient(Blocks.REDSTONE_WIRE), Blocks.AIR.getDefaultState()));

        NORMAL_JUNGLE_TEMPLE
                .addChanger(new RelativePosChanger(BWMBlocks.HAND_CRANK.getDefaultState(), new BlockPos(5, 3, 10)))
                .addChanger(new RelativePosChanger(BWMBlocks.DRAGON_VESSEL.getDefaultState(), new BlockPos(6, 3, 10)));


        WITCH_HUT_CHANGER
                .addChanger(new RelativePosChanger(Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), new BlockPos(2, 2, 6)))
                .addChanger(new RelativePosChanger(Blocks.BREWING_STAND.getDefaultState(), new BlockPos(2, 3, 6)));
    }


    @SubscribeEvent
    public void onStructureSetBlock(StructureSetBlockEvent event) {
        if (event.getComponent() instanceof ComponentScatteredFeaturePieces.DesertPyramid) {
            StructureChanger.convert(DESERT_TEMPLE, event);
        } else if (event.getComponent() instanceof ComponentScatteredFeaturePieces.JunglePyramid) {
//            System.out.printf("/tp %s ~ %s\n", event.getPos().getX(), event.getPos().getZ());
            StructureChanger.convert(JUNGLE_TEMPLE, event);
        } else if (event.getComponent() instanceof ComponentScatteredFeaturePieces.SwampHut) {
            StructureChanger.convert(WITCH_HUT, event);
        }
    }

}
