/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.epsilon;

import mods.railcraft.api.electricity.IElectricGrid;
import mods.railcraft.common.blocks.machine.TileMachineBase;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.plugins.ic2.ISinkDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2MultiEmitterDelegate;
import mods.railcraft.common.plugins.ic2.TileIC2SinkDelegate;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileElectricFeeder extends TileMachineBase implements IElectricGrid, ISinkDelegate {

    private final ChargeHandler chargeHandler = new ChargeHandler(this, ChargeHandler.ConnectType.BLOCK, 1);
    private TileEntity sinkDelegate;
    private boolean addedToIC2EnergyNet;

    @Override
    public EnumMachineEpsilon getMachineType() {
        return EnumMachineEpsilon.ELECTRIC_FEEDER;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isNotHost(getWorld()))
            return;

        if (!addedToIC2EnergyNet) {
            IC2Plugin.addTileToNet(getIC2Delegate());
            addedToIC2EnergyNet = true;
        }

        chargeHandler.tick();
    }

    private void dropFromNet() {
        if (addedToIC2EnergyNet)
            IC2Plugin.removeTileFromNet(getIC2Delegate());
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        dropFromNet();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        dropFromNet();
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        chargeHandler.readFromNBT(data);
    }

    @Nonnull
    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        chargeHandler.writeToNBT(data);
    }

    @Override
    public ChargeHandler getChargeHandler() {
        return chargeHandler;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public double getDemandedEnergy() {
        double chargeDifference = chargeHandler.getCapacity() - chargeHandler.getCharge();
        return chargeDifference > 0.0 ? chargeDifference : 0.0;
    }

    @Override
    public int getSinkTier() {
        return 3;
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount) {
        getChargeHandler().addCharge(amount);
        return 0.0;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, EnumFacing direction) {
        return !(emitter instanceof TileIC2MultiEmitterDelegate);
    }

    public TileEntity getIC2Delegate() {
        if (sinkDelegate == null)
            try {
                sinkDelegate = new TileIC2SinkDelegate(this);
            } catch (Throwable error) {
                Game.logErrorAPI("IndustrialCraft", error);
            }
        return sinkDelegate;
    }
}
