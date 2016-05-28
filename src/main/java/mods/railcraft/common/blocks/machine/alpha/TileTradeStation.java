/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.blocks.machine.alpha.ai.EntityAIMoveToBlock;
import mods.railcraft.common.blocks.machine.alpha.ai.EntityAIWatchBlock;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileTradeStation extends TileMachineItem implements IGuiReturnHandler, ISidedInventory {
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);

    public enum GuiPacketType {

        NEXT_TRADE, SET_PROFESSION
    }

    private static final int AREA = 6;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);
    private int profession;
    private final PhantomInventory recipeSlots = new PhantomInventory(9);
    private final InventoryMapper invInput;
    private final InventoryMapper invOutput;
    protected EnumFacing direction = EnumFacing.NORTH;

    public TileTradeStation() {
        setInventorySize(16);
        invInput = new InventoryMapper(this, 0, 10);
        invOutput = new InventoryMapper(this, 10, 6, false);
    }

    @Override
    public EnumMachineAlpha getMachineType() {
        return EnumMachineAlpha.TRADE_STATION;
    }

    @Nonnull
    @Override
    public IBlockState getActualState(@Nonnull IBlockState state) {
        state = super.getActualState(state);
        state = state.withProperty(FACING, direction);
        return state;
    }

    public IInventory getRecipeSlots() {
        return recipeSlots;
    }

    public int getProfession() {
        return profession;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.TRADE_STATION, player, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (clock % 256 == 0)
            modifyNearbyAI();

        List<EntityVillager> villagers = findNearbyVillagers(AREA);
        attemptTrade(villagers, 0);
        attemptTrade(villagers, 1);
        attemptTrade(villagers, 2);
    }

    private void modifyNearbyAI() {
        for (EntityVillager villager : findNearbyVillagers(20)) {
            AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, getMachineType().getState(), 4, 0.08F));
            AIPlugin.addAITask(villager, 9, new EntityAIMoveToBlock(villager, getMachineType().getState(), 16, 0.002F));
        }
    }

    private List<EntityVillager> findNearbyVillagers(int range) {
        float x = getPos().getX();
        float y = getPos().getY();
        float z = getPos().getZ();
        List<EntityVillager> villagers = MiscTools.getNearbyEntities(worldObj, EntityVillager.class, x, y - 1, y + 3, z, range);
        return villagers;
    }

    private boolean attemptTrade(List<EntityVillager> villagers, int tradeSet) {
        ItemStack buy1 = recipeSlots.getStackInSlot(tradeSet * 3 + 0);
        ItemStack buy2 = recipeSlots.getStackInSlot(tradeSet * 3 + 1);
        ItemStack sell = recipeSlots.getStackInSlot(tradeSet * 3 + 2);
        for (EntityVillager villager : villagers) {
            MerchantRecipeList recipes = villager.getRecipes(null);
            for (MerchantRecipe recipe : recipes) {
                if (recipe.isRecipeDisabled())
                    continue;
                if (recipe.getItemToBuy() != null && !InvTools.isItemLessThanOrEqualTo(recipe.getItemToBuy(), buy1))
                    continue;
                if (recipe.getSecondItemToBuy() != null && !InvTools.isItemLessThanOrEqualTo(recipe.getSecondItemToBuy(), buy2))
                    continue;
                if (!InvTools.isItemGreaterOrEqualThan(recipe.getItemToSell(), sell))
                    continue;
//                System.out.printf("Buying: %d %s Found: %d%n", recipe.getItemToBuy().stackSize, recipe.getItemToBuy().getDisplayName(), InvTools.countItems(invInput, recipe.getItemToBuy()));
                if (canDoTrade(recipe)) {
//                    System.out.println("Can do trade");
                    doTrade(villager, recipe);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canDoTrade(MerchantRecipe recipe) {
        if (recipe.getItemToBuy() != null && InvTools.countItems(invInput, recipe.getItemToBuy()) < recipe.getItemToBuy().stackSize)
            return false;
        if (recipe.getSecondItemToBuy() != null && InvTools.countItems(invInput, recipe.getSecondItemToBuy()) < recipe.getSecondItemToBuy().stackSize)
            return false;
        return InvTools.isRoomForStack(recipe.getItemToSell(), invOutput);
    }

    private void doTrade(IMerchant merchant, MerchantRecipe recipe) {
        merchant.useRecipe(recipe);
        if (recipe.getItemToBuy() != null)
            InvTools.removeItemsAbsolute(invInput, recipe.getItemToBuy().stackSize, recipe.getItemToBuy());
        if (recipe.getSecondItemToBuy() != null)
            InvTools.removeItemsAbsolute(invInput, recipe.getSecondItemToBuy().stackSize, recipe.getSecondItemToBuy());
        InvTools.moveItemStack(recipe.getItemToSell().copy(), invOutput);
    }

    @Override
    public void onBlockPlacedBy(@Nonnull IBlockState state, @Nonnull EntityLivingBase entityliving, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(state, entityliving, stack);
        direction = MiscTools.getSideFacingPlayer(getPos(), entityliving);
    }

    @Override
    public boolean rotateBlock(EnumFacing axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        recipeSlots.writeToNBT("recipe", data);

        data.setInteger("profession", profession);
        data.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        recipeSlots.readFromNBT("recipe", data);

        profession = data.getInteger("profession");
        direction = EnumFacing.getFront(data.getByte("direction"));
    }

    @Override
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(profession);
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
        super.readPacketData(data);
        profession = data.readInt();
        EnumFacing f = EnumFacing.getFront(data.readByte());
        if (direction != f) {
            direction = f;
            markBlockForUpdate();
        }
    }

    @Override
    public void writeGuiData(@Nonnull DataOutputStream data) throws IOException {
    }

    @Override
    public void readGuiData(@Nonnull DataInputStream data, EntityPlayer sender) throws IOException {
        GuiPacketType type = GuiPacketType.values()[data.readByte()];
        switch (type) {
            case NEXT_TRADE:
                nextTrade(data.readByte());
                break;
            case SET_PROFESSION:
                profession = data.readInt();
                sendUpdateToClient();
                break;
        }
    }

    public void nextTrade(int tradeSet) {
        EntityVillager villager = new EntityVillager(worldObj);
        villager.setProfession(profession);
        MerchantRecipeList recipes = villager.getRecipes(null);
        MerchantRecipe recipe = recipes.get(MiscTools.RANDOM.nextInt(recipes.size()));
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 0, recipe.getItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 1, recipe.getSecondItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 2, recipe.getItemToSell());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing face) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
        return slot >= 10;
    }
}
