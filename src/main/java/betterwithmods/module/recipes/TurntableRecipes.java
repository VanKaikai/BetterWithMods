package betterwithmods.module.recipes;

import betterwithmods.common.blocks.BlockUnfiredPottery;
import betterwithmods.common.registry.TurntableRotationManager;
import betterwithmods.library.common.modularity.impl.Feature;
import betterwithmods.module.internal.RecipeRegistry;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class TurntableRecipes extends Feature {

    @Override
    protected boolean canEnable() {
        return true;
    }

    @Override
    public void onInit(FMLInitializationEvent event) {

        RecipeRegistry.TURNTABLE.addDefaultRecipe(new ItemStack(Blocks.CLAY), BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.CRUCIBLE), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        RecipeRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.CRUCIBLE), BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.PLANTER));
        RecipeRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.PLANTER), BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.VASE), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        RecipeRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.VASE), BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.URN), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        RecipeRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.Type.URN), Blocks.AIR.getDefaultState(), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));


        TurntableRotationManager.addAttachment(b -> b instanceof BlockTorch);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockLever);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockLadder);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockButton);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockWallSign);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockTripWireHook);

        TurntableRotationManager.addRotationHandler(block -> block instanceof BlockTorch, (world, pos) -> {
            IBlockState state = world.getBlockState(pos);
            return state.getValue(BlockTorch.FACING).getAxis().isVertical();
        });
        TurntableRotationManager.addRotationHandler(Blocks.LEVER, (world, pos) -> {
            IBlockState state = world.getBlockState(pos);
            return state.getValue(BlockLever.FACING).getFacing().getAxis().isVertical();
        });
        TurntableRotationManager.addRotationBlacklist(block -> block instanceof BlockPistonExtension);
        TurntableRotationManager.addRotationHandler(block -> block instanceof BlockUnfiredPottery, new TurntableRotationManager.IRotation() {
            @Override
            public boolean isValid(World world, BlockPos pos) {
                return true;
            }

            @Override
            public boolean canTransmitHorizontally(World world, BlockPos pos) {
                return false;
            }

            @Override
            public boolean canTransmitVertically(World world, BlockPos pos) {
                return false;
            }
        });
        TurntableRotationManager.addRotationHandler(Blocks.CLAY, new TurntableRotationManager.IRotation() {
            @Override
            public boolean isValid(World world, BlockPos pos) {
                return true;
            }

            @Override
            public boolean canTransmitHorizontally(World world, BlockPos pos) {
                return false;
            }

            @Override
            public boolean canTransmitVertically(World world, BlockPos pos) {
                return false;
            }
        });
        TurntableRotationManager.addRotationHandler(block -> block instanceof BlockPistonBase, (world, pos) -> !world.getBlockState(pos).getValue(BlockPistonBase.EXTENDED));
    }

    @Override
    public String getDescription() {
        return null;
    }
}