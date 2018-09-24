package betterwithmods.manual.custom;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.BlockChime;
import betterwithmods.library.utils.ingredient.BlockIngredient;
import betterwithmods.library.utils.ingredient.StateIngredient;
import betterwithmods.manual.api.manual.PathProvider;
import betterwithmods.library.utils.ingredient.SetBlockIngredient;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class StatePathProvider implements PathProvider {

    private final Set<PathOverride> PATH_OVERRIDES = Sets.newHashSet();

    {
        addBlock(new SetBlockIngredient(BlockChime.BLOCKS), "wind_chime");
        addBlock(new StateIngredient(BWMBlocks.WOODEN_BROKEN_GEARBOX), "wooden_gearbox");

        addBlock(new BlockIngredient(BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCK), BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCKBLOOD)), "chopping_block");

        addBlock(new BlockIngredient(BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITESTONE), BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE)), "white_stone");
        addItem(new BlockIngredient(BlockAesthetic.getStack(BlockAesthetic.EnumType.NETHERCOAL)), "nether_coal");

        addBlock(new StateIngredient(BWMBlocks.TURNTABLE), "turntable");
        addBlock(new StateIngredient(BWMBlocks.MILLSTONE), "millstone");
        addBlock(new StateIngredient(BWMBlocks.FILTERED_HOPPER), "hopper");
        addBlock(new StateIngredient(BWMBlocks.PULLEY), "pulley");
        addItem(new StateIngredient(BWMBlocks.ROPE), "rope");

        addBlock(new SetBlockIngredient(
//                        new StateIngredient(BWMBlocks.GRATE),
                        new StateIngredient(BWMBlocks.WICKER)
//                        new StateIngredient(BWMBlocks.SLATS)
                ),
                "decoration");
    }

    private void addBlock(BlockIngredient ingredient, String name) {
        PATH_OVERRIDES.add(new PathOverride(ingredient, name));
    }

    private void addItem(BlockIngredient ingredient, String name) {
        PATH_OVERRIDES.add(new PathOverride(ingredient, "items", name));
    }

    @Nullable
    @Override
    public String pathFor(@Nonnull ItemStack stack) {
        return null;
    }

    @Nullable
    @Override
    public String pathFor(@Nonnull World world, @Nonnull BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        for (PathOverride override : PATH_OVERRIDES)
            if (override.apply(world, pos, state))
                return String.format("%%LANGUAGE%%/%s/%s.md", override.path, override.name);
        return defaultPathFor(state);
    }

    private String defaultPathFor(IBlockState state) {
        Block block = state.getBlock();
        ResourceLocation loc = block.getRegistryName();
        if (loc != null) {
            String name = loc.getPath();
            return String.format("%%LANGUAGE%%/blocks/%s.md", name);
        }
        return null;
    }

    private class PathOverride {
        private BlockIngredient ingredient;

        private String name, path;

        public PathOverride(BlockIngredient ingredient, String name) {
            this(ingredient, "blocks", name);
        }

        public PathOverride(BlockIngredient ingredient, String path, String name) {
            this.ingredient = ingredient;
            this.name = name;
            this.path = path;
        }

        public boolean apply(World world, BlockPos pos, IBlockState state) {
            return ingredient.apply(world, pos, state);
        }
    }
}
