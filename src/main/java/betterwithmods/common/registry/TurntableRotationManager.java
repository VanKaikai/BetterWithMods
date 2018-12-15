package betterwithmods.common.registry;

import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import betterwithmods.library.utils.DirUtils;
import betterwithmods.network.BWMNetwork;
import betterwithmods.network.messages.MessageRotate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TurntableRotationManager {


    public static final HashSet<Predicate<Block>> BLOCK_PREDICATE_ATTACHMENTS = Sets.newHashSet();
    public static final HashSet<Block> BLOCK_ATTACHMENTS = Sets.newHashSet();
    public static final HashMap<Predicate<Block>, IRotation> PREDICATE_ROTATIONS = Maps.newHashMap();
    public static final HashMap<Block, IRotation> BLOCK_ROTATIONS = Maps.newHashMap();

    private static final IRotation NO_ROTATION = (world, pos) -> false;
    private static final IRotation BASE_ROTATION = (world, pos) -> true;

    public static boolean isAttachment(Block block) {
        return BLOCK_ATTACHMENTS.contains(block) || BLOCK_PREDICATE_ATTACHMENTS.stream().anyMatch(p -> p.test(block));
    }

    public static void addAttachment(Block block) {
        BLOCK_ATTACHMENTS.add(block);
    }

    public static void addAttachment(Predicate<Block> block) {
        BLOCK_PREDICATE_ATTACHMENTS.add(block);
    }

    public static void addRotationBlacklist(Predicate<Block> predicate) {
        addRotationHandler(predicate, NO_ROTATION);
    }

    public static void addRotationHandler(Predicate<Block> predicate, IRotation rotation) {
        PREDICATE_ROTATIONS.put(predicate, rotation);
    }

    public static void addRotationHandler(Block block, IRotation rotation) {
        BLOCK_ROTATIONS.put(block, rotation);
    }

    public static IRotation rotate(World world, BlockPos pos, Rotation rotation) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block.isAir(state, world,pos))
            return null;

        IRotation handler = BLOCK_ROTATIONS.getOrDefault(block, null);
        if (handler == null) {
            for (Map.Entry<Predicate<Block>, IRotation> entry : PREDICATE_ROTATIONS.entrySet()) {
                if (entry.getKey().test(block)) {
                    handler = entry.getValue();
                    break;
                }
            }
        }
        if (handler == null)
            handler = BASE_ROTATION;

        if (handler.isValid(world, pos)) {
            if (handler.rotate(world, pos, rotation)) {
                world.scheduleBlockUpdate(pos, block, block.tickRate(world), 1);
                world.notifyNeighborsOfStateChange(pos, block, true);
            }
            return handler;
        }
        return null;
    }

    private static BlockPos rotateAround(BlockPos centerPos, EnumFacing facing, Rotation rotation) {
        return centerPos.add(facing.getXOffset(), 0, facing.getZOffset());
    }

    public static void rotateAttachments(World world, BlockPos pos, Rotation rotation) {
        HashMap<EnumFacing, IBlockState> blocks = Maps.newHashMap();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos newPos = pos.offset(facing);
            IBlockState state = world.getBlockState(newPos);
            if (isAttachment(state.getBlock())) {
                blocks.put(facing, state);
                world.setBlockToAir(newPos);
            }
        }
        if (blocks.isEmpty())
            return;
        for (EnumFacing facing : blocks.keySet()) {
            IBlockState state = blocks.get(facing);
            EnumFacing newFacing = rotation == Rotation.CLOCKWISE_90 ? facing.rotateY() : facing.rotateYCCW();
            BlockPos newPos = rotateAround(pos, newFacing, rotation);
            if (!world.getBlockState(newPos).getMaterial().isReplaceable()) {
                state.getBlock().dropBlockAsItem(world, pos.offset(facing), state, 0);
                world.setBlockToAir(pos.offset(facing));
            } else {
                world.setBlockState(newPos, state.withRotation(rotation));
            }
        }
    }

    public static void rotateEntities(World world, BlockPos pos, Rotation rotation) {
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, Block.FULL_BLOCK_AABB.offset(pos).shrink(0.5), entity -> true);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                rotateEntity(entity, rotation);
            }
        }
    }

    private static void rotateEntity(Entity entity, Rotation rotation) {
        float angle;
        if (rotation == Rotation.CLOCKWISE_90)
            angle = 90;
        else if (rotation == Rotation.COUNTERCLOCKWISE_90)
            angle = -90;
        else
            angle = 180;
        float newYaw = entity.rotationYaw + angle;
        entity.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, newYaw, entity.rotationPitch);
        BWMNetwork.INSTANCE.sendToAllAround(new MessageRotate(entity.getEntityId(), newYaw, entity.rotationPitch), entity.world, entity.getPosition());
    }

    public interface IRotation {
        boolean isValid(World world, BlockPos pos);

        default boolean rotate(World world, BlockPos pos, Rotation rotation) {
            IBlockState state = world.getBlockState(pos);
            return world.setBlockState(pos, state.withRotation(rotation));
        }

        default boolean canTransmitVertically(World world, BlockPos pos, TurntableRecipe recipe) {
            if(recipe != null)
                return false;
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.GLASS || block == Blocks.STAINED_GLASS)
                return true;
            return world.isBlockNormalCube(pos, false);
        }

        default boolean canTransmitHorizontally(World world, BlockPos pos) {
            return true;
        }
    }

}
