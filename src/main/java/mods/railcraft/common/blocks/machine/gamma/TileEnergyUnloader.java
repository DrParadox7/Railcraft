/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IEnergyTransfer;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.ic2.IEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2EmitterDelegate;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileEnergyUnloader extends TileLoaderEnergyBase implements IEmitterDelegate, IGuiReturnHandler {

    private static final int[] OUTPUT_LEVELS = {512, 2048};
    private boolean waitTillEmpty = true;
    private TileEntity emitterDelegate;

    @Override
    public EnumMachineGamma getMachineType() {
        return EnumMachineGamma.ENERGY_UNLOADER;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.UNLOADER_ENERGY, player, worldObj, getPos());
        return true;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(getWorld()))
            return;

        transferredEnergy = false;
        transferRate = 0;

        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, getPos(), 0.1f, direction);

        if (cart != currentCart) {
            setPowered(false);
            currentCart = cart;
            cartWasSent();
        }

        if (cart == null)
            return;

        if (!canHandleCart(cart)) {
            sendCart(cart);
            return;
        }

        if (isPaused())
            return;

        IEnergyTransfer energyCart = (IEnergyTransfer) cart;

        if (energy < getCapacity() && energyCart.getEnergy() > 0) {
            double usage = (energyCart.getTransferLimit() * Math.pow(1.5, overclockerUpgrades));
            double injection = (energyCart.getTransferLimit() * Math.pow(1.3, overclockerUpgrades));

            double room = getCapacity() - getEnergy();
            if (room < injection) {
                double ratio = room / injection;
                injection = room;
                usage = usage * ratio;
            }

            double extract = energyCart.extractEnergy(this, usage, getTier(), true, false, false);

            if (extract < usage) {
                double ratio = extract / usage;
                usage = extract;
                injection = injection * ratio;
            }

            transferRate = (int) injection;
            energy += injection;
            transferredEnergy = extract > 0;
        }

        if (!transferredEnergy && !isPowered() && shouldSendCart(cart))
            sendCart(cart);
    }

    @Override
    public boolean canHandleCart(EntityMinecart cart) {
        if (!super.canHandleCart(cart))
            return false;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        return energyCart.canExtractEnergy();
    }

    @Override
    protected boolean shouldSendCart(EntityMinecart cart) {
        if (!(cart instanceof IEnergyTransfer))
            return true;
        IEnergyTransfer energyCart = (IEnergyTransfer) cart;
        if (!waitTillEmpty)
            return true;
        else if (energyCart.getEnergy() == 0)
            return true;
        return false;
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("WaitTillEmpty", waitTillEmpty());
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        setWaitTillEmpty(nbttagcompound.getBoolean("WaitTillEmpty"));
    }

    @Override
    public void writePacketData(@Nonnull DataOutputStream data) throws IOException {
        super.writePacketData(data);

        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readPacketData(@Nonnull DataInputStream data) throws IOException {
        super.readPacketData(data);

        waitTillEmpty = data.readBoolean();
    }

    @Override
    public void writeGuiData(@Nonnull DataOutputStream data) throws IOException {
        data.writeBoolean(waitTillEmpty);
    }

    @Override
    public void readGuiData(@Nonnull DataInputStream data, EntityPlayer sender) throws IOException {
        waitTillEmpty = data.readBoolean();
    }

    public boolean waitTillEmpty() {
        return waitTillEmpty;
    }

    public void setWaitTillEmpty(boolean wait) {
        waitTillEmpty = wait;
    }

    @Override
    public double getOfferedEnergy() {
        int emit = transformerUpgrades > 0 ? OUTPUT_LEVELS[1] : OUTPUT_LEVELS[0];
        return Math.min(energy, emit);
    }

    @Override
    public int getSourceTier() {
        return transformerUpgrades > 0 ? 4 : 3;
    }

    @Override
    public void drawEnergy(double amount) {
        energy -= amount;
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, EnumFacing direction) {
        return this.direction != direction;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public TileEntity getIC2Delegate() {
        if (emitterDelegate == null)
            try {
                emitterDelegate = new TileIC2EmitterDelegate(this);
            } catch (Throwable error) {
                Game.logErrorAPI("IndustrialCraft", error);
            }
        return emitterDelegate;
    }
}
