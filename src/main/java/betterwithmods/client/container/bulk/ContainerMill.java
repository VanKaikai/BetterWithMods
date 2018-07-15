package betterwithmods.client.container.bulk;

import betterwithmods.client.container.ContainerProgress;
import betterwithmods.common.tile.TileMill;
import betterwithmods.util.CapabilityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerMill extends ContainerProgress {
    private final TileMill tile;
    public boolean blocked;

    public ContainerMill(EntityPlayer player, TileMill tile) {
        super(tile);
        this.tile = tile;

        for (int j = 0; j < 3; j++) {
            addSlotToContainer(new SlotItemHandler(tile.inventory, j, 62 + j * 18, 43));
        }

        IItemHandler playerInv = CapabilityUtils.getEntityInventory(player);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new SlotItemHandler(playerInv, j + i * 9 + 9, 8 + j * 18, 76 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new SlotItemHandler(playerInv, i, 8 + i * 18, 134));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if (index < 3) {
                if (!mergeItemStack(stack1, 3, this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!mergeItemStack(stack1, 0, 3, false))
                return ItemStack.EMPTY;
            if (stack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }
        return stack;
    }


    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 2, this.tile.blocked ? 1 : 0);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean b = tile.blocked;
        if (blocked != b) {
            blocked = b;
            for (IContainerListener craft : this.listeners) {
                craft.sendWindowProperty(this, 2, blocked ? 1 : 0);
            }
        }
    }

    @Override
    public void updateProgressBar(int index, int value) {
        super.updateProgressBar(index, value);
        switch (index) {
            case 2:
                blocked = value == 1;
                break;
        }
    }

}
