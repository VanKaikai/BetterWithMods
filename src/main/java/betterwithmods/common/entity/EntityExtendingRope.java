package betterwithmods.common.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.tile.TileEntityPulley;
import betterwithmods.module.GlobalConfig;
import betterwithmods.util.AABBArray;
import betterwithmods.util.InvUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static java.lang.Math.max;

public class EntityExtendingRope extends Entity implements IEntityAdditionalSpawnData {

    private BlockPos pulley;
    private int targetY;
    private boolean up;

    private Map<Vec3i, IBlockState> blocks;
    private Map<Vec3i, NBTTagCompound> tiles;
    private AABBArray blockBB;

    private double prevPosYUpd;

    public EntityExtendingRope(World worldIn) {
        this(worldIn, null, null, 0);
    }

    public EntityExtendingRope(World worldIn, BlockPos pulley, BlockPos source, int targetY) {
        super(worldIn);
        this.pulley = pulley;
        this.targetY = targetY;
        if (source != null) {
            this.up = source.getY() < targetY;
            this.setPosition(source.getX() + 0.5, source.getY(), source.getZ() + 0.5);
        }
        this.blocks = Maps.newHashMap();
        this.tiles = Maps.newHashMap();
        this.blockBB = null;
        this.setSize(0.1F, 1F);
        this.ignoreFrustumCheck = true;
    }

    private static AxisAlignedBB createAABB(Vec3d part1, Vec3d part2) {
        return new AxisAlignedBB(part1.x, part1.y, part1.z, part2.x, part2.y, part2.z);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public float getEyeHeight() {
        return -1;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        pulley = new BlockPos(compound.getInteger("PulleyX"), compound.getInteger("PulleyY"),
                compound.getInteger("PulleyZ"));
        targetY = compound.getInteger("TargetY");
        up = compound.getBoolean("Up");
        if (compound.hasKey("BlockData")) {
            byte[] bytes = compound.getByteArray("BlockData");
            ByteBuf buf = Unpooled.buffer(bytes.length);
            buf.writeBytes(bytes);
            blocks = deserializeBlockmap(buf);
        }
        if (compound.hasKey("Tiles")) {
            tiles = deserializeTiles(compound.getCompoundTag("Tiles"));
        }
        rebuildBlockBoundingBox();
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("PulleyX", pulley.getX());
        compound.setInteger("PulleyY", pulley.getY());
        compound.setInteger("PulleyZ", pulley.getZ());
        compound.setInteger("TargetY", targetY);
        compound.setBoolean("Up", up);
        ByteBuf buf = Unpooled.buffer();
        serializeBlockmap(buf, blocks);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        compound.setByteArray("BlockData", bytes);
        NBTTagCompound t = new NBTTagCompound();
        serializeTiles(t, tiles);
        compound.setTag("Tiles", t);
        if (GlobalConfig.debug) {
            for (int i = 0; i < bytes.length; i++) {
                if (i % 16 == 0) {
                    StringBuilder text = new StringBuilder(Integer.toHexString(i));
                    while (text.length() < 8) {
                        text.insert(0, "0");
                    }
                    System.out.print("\n" + text + ": ");
                }
                StringBuilder b = new StringBuilder(Integer.toHexString(Byte.toUnsignedInt(bytes[i])));
                while (b.length() < 2) {
                    b.insert(0, "0");
                }
                System.out.print(b);
                if (i % 2 == 1) {
                    System.out.print(' ');
                }
            }
        }
    }

    private void serializeTiles(NBTTagCompound tag, Map<Vec3i, NBTTagCompound> tiles) {
        NBTTagList list = new NBTTagList();
        tiles.forEach((vec, tile) -> {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("offset", new BlockPos(vec).toLong());
            entry.setTag("tile", tile);
            list.appendTag(entry);
        });
        tag.setTag("entries", list);
    }

    private Map<Vec3i, NBTTagCompound> deserializeTiles(NBTTagCompound tag) {
        Map<Vec3i, NBTTagCompound> map = new HashMap<>();
        NBTTagList list = tag.getTagList("entries", 10);
        list.iterator().forEachRemaining(e -> {
            NBTTagCompound entry = (NBTTagCompound) e;
            Vec3i offset = BlockPos.fromLong(entry.getLong("offset"));
            NBTTagCompound tileData = entry.getCompoundTag("tile");
            map.put(offset, tileData);
        });
        return map;
    }

    private void serializeBlockmap(ByteBuf buf, Map<Vec3i, IBlockState> blocks) {
        buf.writeInt(blocks.size());
        IBlockState state;
        for (Vec3i vec : blocks.keySet()) {
            state = blocks.get(vec);
            if (state != null) {
                buf.writeInt(vec.getX());
                buf.writeInt(vec.getY());
                buf.writeInt(vec.getZ());

                Block block = state.getBlock();
                ResourceLocation resourcelocation = state.getBlock().getRegistryName();
                if (resourcelocation != null) {
                    String blockName = resourcelocation.toString();
                    buf.writeInt(blockName.length());
                    buf.writeBytes(blockName.getBytes(Charset.forName("UTF-8")));
                    buf.writeByte((byte) block.getMetaFromState(state));
                }
            }
        }

    }

    private Map<Vec3i, IBlockState> deserializeBlockmap(ByteBuf buf) {
        Map<Vec3i, IBlockState> map = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Vec3i vec = new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
            int len = buf.readInt();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            String name = new String(bytes, Charset.forName("UTF-8"));
            int meta = buf.readByte();
            //TODO this cannot stay.
            @SuppressWarnings("deprecation")
            IBlockState state = Block.getBlockFromName(name).getStateFromMeta(meta);
            map.put(vec, state);
        }
        return map;
    }

    private void rebuildBlockBoundingBox() {
        if (blocks == null || blocks.isEmpty()) {
            this.blockBB = null;
        } else {
            List<AxisAlignedBB> bbs = new ArrayList<>();
            bbs.add(new AxisAlignedBB(0.45, 0, 0.45, 0.55, 1, 0.55)); // rope bounding box
            for (Vec3i vec : blocks.keySet()) {
                IBlockState state = blocks.get(vec);
                if (state.getBlock().isCollidable()) {
                    AxisAlignedBB bb2 = new AxisAlignedBB(vec.getX(), vec.getY(), vec.getZ(), vec.getX() + 1,
                            vec.getY() + getBlockStateHeight(state), vec.getZ() + 1);
                    bbs.add(bb2);
                }
            }
            this.blockBB = new AABBArray(bbs.toArray(new AxisAlignedBB[0])).offset(-0.5, 0, -0.5);
        }
    }

    @Override
    public void onUpdate() {
        if (up) {
            if (posY > targetY) {
                if (done())
                    return;
            }
        } else {
            if (posY < targetY) {
                if (done())
                    return;
            }
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        setPosition(
                pulley.getX() + 0.5,
                this.posY + 0.1 * (up ? 1 : -1),
                pulley.getZ() + 0.5
        );

        if (blocks != null)
            updatePassengers(prevPosY, posY, false);

        this.world.updateEntityWithOptionalForce(this, false);
    }

    public void updatePassengers(double posY, double newPosY, boolean b) {
        if (blockBB == null) return;
        Set<Entity> passengers = Sets.newHashSet(getEntityWorld().getEntitiesWithinAABB(Entity.class, AABBArray.toAABB(this.getEntityBoundingBox()).expand(0, 0.5, 0).offset(0, 0.5, 0), e -> !(e instanceof EntityExtendingRope)));
        AABBArray oldBB = blockBB.offset(posX, posY, posZ);
        AABBArray newBB = blockBB.offset(posX, newPosY, posZ);
        for (Entity e : passengers) {
            AxisAlignedBB ebb = e.getEntityBoundingBox();
            if (!newBB.intersects(ebb)) continue;

            double yoff = -oldBB.calculateYOffset(ebb, posY - newPosY);

            if (yoff != 0) {
                if (getEntityWorld().isRemote || !(e instanceof EntityPlayer) || b)
                    e.move(null, 0, yoff, 0);

                e.motionY = max(up ? 0 : -0.1, e.motionY);
                e.isAirBorne = false;
                e.onGround = true;
                e.collided = e.collidedVertically = true;
                e.fallDistance = 0;
            }
        }
    }

    private double getBlockStateHeight(IBlockState blockState) {
        return (blockState == null ? 1
                : (blockState.getBlock() == BWMBlocks.ANCHOR ? 0.375F
                : (blockState.getBlock() instanceof BlockRailBase || blockState.getBlock() instanceof BlockRedstoneWire ? 0 : 1)));
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    private void reconstruct() {
        BlockPos pos = this.pulley.down(this.pulley.getY() - targetY);

        int retries = 0;
        while (!blocks.isEmpty() && retries < 10) {
            retries++;
            int skipped = 0;
            for (Entry<Vec3i, IBlockState> entry : blocks.entrySet()) {
                BlockPos blockPos = pos.add(entry.getKey());
                IBlockState state = entry.getValue();
                if (state.getBlock().canPlaceBlockAt(getEntityWorld(), blockPos)) {

                    getEntityWorld().setBlockState(blockPos, state, 3);
                    if (tiles.containsKey(entry.getKey())) {
                        TileEntity tile = getEntityWorld().getTileEntity(blockPos);
                        if (tile != null) {
                            NBTTagCompound tag = tiles.get(entry.getKey());
                            tile.readFromNBT(tag);
                            tile.setPos(blockPos);
                        }
                    }
                    blocks.remove(entry.getKey());
                    tiles.remove(entry.getKey());
                    skipped = 0;
                    break;
                }
                skipped++;
            }
            if (skipped == 0) {
                retries = 0;
            }
        }

        if (retries > 0) {
            blocks.forEach((vec, state) -> state.getBlock().getDrops(getEntityWorld(), pos, state, 0).forEach(stack -> InvUtils.spawnStack(getEntityWorld(), posX, posY, posZ, stack, 10)));
        }

        updatePassengers(posY, targetY + 0.25, true);
    }

    private boolean done() {
        if (!getEntityWorld().isRemote) {
            TileEntity te = getEntityWorld().getTileEntity(pulley);
            if (te instanceof TileEntityPulley) {
                TileEntityPulley pulley = (TileEntityPulley) te;
                if (!pulley.onJobCompleted(up, targetY, this)) {
                    reconstruct();
                    return true;
                }
            } else {
                //The tile has been lost, abort
                reconstruct();
                this.setDead();
                return true;
            }
        }
        return false;
    }

    public void addBlock(Vec3i offset, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos).getActualState(world, pos);
        TileEntity tile = world.getTileEntity(pos);
        blocks.put(offset, state);
        if (tile != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tile.writeToNBT(tag);
            tiles.put(offset, tag);
            world.removeTileEntity(pos);
        }
        rebuildBlockBoundingBox();
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(pulley.getX());
        buffer.writeInt(pulley.getY());
        buffer.writeInt(pulley.getZ());
        buffer.writeInt(targetY);
        buffer.writeBoolean(up);
        serializeBlockmap(buffer, blocks);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        pulley = new BlockPos(additionalData.readInt(), additionalData.readInt(), additionalData.readInt());
        targetY = additionalData.readInt();
        up = additionalData.readBoolean();
        blocks = deserializeBlockmap(additionalData);
    }

    public int getTargetY() {
        return this.targetY;
    }

    public void setTargetY(int i) {
        this.targetY = i;
    }


    /*
    FIXME this is a hack that fixes the odd camera jerking when descending the pulley.
    FIXME From what I can tell, whenever Minecraft.objecctMousedOver is type entity something is effecting the player's position or motion in bizarre fashions. I have yet to find where this happens.
    FIXME For the time being, there will be some odd quirks with warping on top of the pulley if you ever collide with any side but better than jumpiness.
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean getUp() {
        return up;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return false;
    }

    public boolean isPathBlocked() {
        HashSet<BlockPos> blocked = new HashSet<>();
        blocks.forEach((vec, state) -> {
            if (blocked.isEmpty() && !up || state.getBlock() != BWMBlocks.ANCHOR) {
                BlockPos pos = this.pulley.down(this.pulley.getY() - targetY).add(vec);
                if (up)
                    pos = pos.up();
                else
                    pos = pos.down();

                Block b = getEntityWorld().getBlockState(pos).getBlock();

                if (!(b == Blocks.AIR || b.isReplaceable(getEntityWorld(), pos))) {
                    blocked.add(pos);
                }
            }
        });
        return !blocked.isEmpty();
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound compound) {
        return false;
    }

    public BlockPos getPulleyPosition() {
        return this.pulley;
    }

    public Map<Vec3i, IBlockState> getBlocks() {
        return blocks;
    }

    public Map<Vec3i, NBTTagCompound> getTiles() {
        return tiles;
    }

    @Override
    protected void setSize(float width, float height) {
        if (blockBB == null)
            super.setSize(width, height);
    }

    @Override
    public void setEntityBoundingBox(AxisAlignedBB bb) {
        rebuildBlockBoundingBox();
        super.setEntityBoundingBox(blockBB != null ? blockBB.offset(this.posX, this.posY, this.posZ) : bb);
    }

    public AxisAlignedBB getBlockBoundingBox(Vec3i block, IBlockState state) {
        Vec3d pos = new Vec3d(pulley.getX(), posY, pulley.getZ()).addVector(block.getX(), block.getY(), block.getZ());
        return new AxisAlignedBB(pos, pos.addVector(1, getBlockStateHeight(state), 1));
    }


    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getCollisionBoundingBox() {
        return (this.getEntityBoundingBox() instanceof AABBArray
                ? ((AABBArray) this.getEntityBoundingBox()).forEach(i -> i.setMaxY(i.maxY - 0.125))
                : this.getEntityBoundingBox());
    }


}
