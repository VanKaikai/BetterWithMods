package betterwithmods.common.world.gen.feature;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockBloodLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import java.util.Random;

public class WorldGenBloodTree extends WorldGenAbstractTree {
    private final IBlockState log = BWMBlocks.BLOOD_LOG.getDefaultState();
    private final IBlockState leaves = BWMBlocks.BLOOD_LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, true);

    public WorldGenBloodTree() {
        super(true);
    }

    @Override
    public boolean generate(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos) {
        int height = 5 + rand.nextInt(4);

        boolean flag = true;
        if (pos.getY() >= 1 && pos.getY() + height + 1 <= 256) {
            for (int i = pos.getY(); i <= pos.getY() + height + 1; i++) {
                int range = i == pos.getY() ? 0 : i <= pos.getY() + 1 + height - 2 ? 2 : 1;

                BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

                for (int x = pos.getX() - range; x <= pos.getX() + range && flag; x++) {
                    for (int z = pos.getZ() - range; z <= pos.getZ() + range && flag; z++) {
                        if (i >= 0 && i < world.getHeight()) {
                            if (!this.isReplaceable(world, mut.setPos(x, i, z)))
                                flag = false;
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
                return false;
            else {
                boolean isSustained = world.getBlockState(pos.down()).getBlock().canSustainPlant(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP, (IPlantable)BWMBlocks.BLOOD_SAPLING);

                if (isSustained && pos.getY() < world.getHeight() - height - 1) {
                    world.getBlockState(pos.down()).getBlock().onPlantGrow(world.getBlockState(pos.down()), world, pos.down(), pos);

                    for (int y = pos.getY() - 3 + height; y <= pos.getY() + height; y++) {
                        int w = y - (pos.getY() + height);
                        int width = 1 - w / 2;

                        for(int x = pos.getX() - width; x <= pos.getX() + width; ++x) {
                            int xWide = x - pos.getX();
                            for(int z = pos.getZ() - width; z <= pos.getZ() + width; ++z) {
                                int zWide = z - pos.getZ();

                                if(Math.abs(xWide) != width || Math.abs(zWide) != width || rand.nextInt(2) != 0 && w != 0) {
                                    BlockPos leafPos = new BlockPos(x, y, z);
                                    IBlockState leafState = world.getBlockState(leafPos);
                                    if(leafState.getBlock().isAir(leafState, world, leafPos)) {
                                        this.setBlockAndNotifyAdequately(world, leafPos, leaves);
                                    }
                                }
                            }
                        }
                    }

                    for(int y = 0; y < height; ++y)
                    {
                        BlockPos up = pos.up(y);
                        IBlockState logState = world.getBlockState(up);
                        if(logState.getBlock().isAir(logState, world, up) || logState.getBlock().isLeaves(logState, world, up))
                        {
                            if (y > height - 4) {
                                this.setBlockAndNotifyAdequately(world, up, log.withProperty(BlockBloodLog.EXPANDABLE, world.provider.isNether()));
                                int face = rand.nextInt(6);
                                if (face > 1) {
                                    EnumFacing facing = EnumFacing.getFront(face);
                                    for (int i = 0; i < 1 + rand.nextInt(2); i++) {
                                        generateBranch(world, up.offset(facing, i), facing);
                                    }
                                }
                            }
                            else
                                this.setBlockAndNotifyAdequately(world, up, log);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void generateBranch(World world, BlockPos pos, EnumFacing facing) {
        if (this.isReplaceable(world, pos)) {
            BlockPos check = pos.offset(facing);
            IBlockState checkState = world.getBlockState(check);
            if (canOverwrite(checkState, world, check)) {
                world.setBlockState(check, log.withProperty(BlockBloodLog.EXPANDABLE, world.provider.isNether()).withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis())));
            }
            generateLeafExpansion(world, check, facing.getAxis());
            generateLeafExpansion(world, check.offset(facing), facing.getAxis());
        }
    }

    private void generateLeafExpansion(World world, BlockPos pos, EnumFacing.Axis axis) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockPos offset;
                if (axis == EnumFacing.Axis.X)
                    offset = pos.add(0, i, j);
                else if (axis == EnumFacing.Axis.Y)
                    offset = pos.add(i, 0, j);
                else
                    offset = pos.add(i, j, 0);
                if (canOverwrite(world.getBlockState(offset), world, offset)) {
                    world.setBlockState(offset, leaves);
                }
            }
        }
    }

    @Override
    public boolean canGrowInto(Block block) {
        return super.canGrowInto(block) || block == BWMBlocks.BLOOD_LOG;
    }

    private boolean canOverwrite(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        return block.isReplaceable(world, pos) || block.isLeaves(state, world, pos);
    }
}
