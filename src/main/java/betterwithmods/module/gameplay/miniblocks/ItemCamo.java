package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.common.blocks.camo.BlockCamo;
import betterwithmods.common.blocks.camo.TileCamo;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemCamo extends ItemBlock {

    public ItemCamo(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public static IBlockState getState(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemCamo) {
            NBTTagCompound tag = stack.getSubCompound("texture");
            if (tag != null) {
                return NBTUtil.readBlockState(tag);
            }
        }
        return null;
    }

    public static boolean matches(ItemStack a, ItemStack b) {
        if ((a == null || b == null))
            return false;
        if (!(a.getItem() instanceof ItemCamo && b.getItem() instanceof ItemCamo))
            return false;

        ItemCamo miniA = (ItemCamo) a.getItem(), miniB = (ItemCamo) b.getItem();
        if (miniA.getBlock() != miniB.getBlock())
            return false;
        IBlockState stateA = getState(a), stateB = getState(b);
        return stateA != null & stateB != null && stateA.equals(stateB);
    }

    private void setNBT(World worldIn, BlockPos pos, ItemStack stackIn) {
        MinecraftServer minecraftserver = worldIn.getMinecraftServer();
        if (minecraftserver == null)
            return;

        NBTTagCompound data = stackIn.getSubCompound("miniblock");

        if (data != null) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileCamo) {
                if (!worldIn.isRemote && tileentity.onlyOpsCanSetNbt()) {
                    return;
                }

                NBTTagCompound tileNBT = tileentity.writeToNBT(new NBTTagCompound());
                NBTTagCompound newNBT = tileNBT.copy();
                tileNBT.merge(data);

                if (!tileNBT.equals(newNBT)) {
                    tileentity.readFromNBT(tileNBT);
                    tileentity.markDirty();
                }
            }
        }
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block) {


            setTileEntityNBT(world, player, pos, stack);
            setNBT(world, pos, stack);
            ((BlockCamo) this.block).onBlockPlacedBy(world, pos, state, player, stack, side, hitX, hitY, hitZ);
            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
        }

        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("texture");
        String type = I18n.translateToLocal("bwm.unknown_mini.name").trim();
        if (tag != null) {
            IBlockState state = NBTUtil.readBlockState(tag);
            ItemStack block = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            if (block.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) block.getItem();
                type = itemBlock.getItemStackDisplayName(block);
            }
        }
        return String.format("%s %s", type, I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim());
    }
}