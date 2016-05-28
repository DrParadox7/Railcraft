/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.blocks.tracks.instances;

import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.blocks.tracks.speedcontroller.SpeedControllerHighSpeed;
import net.minecraft.entity.item.EntityMinecart;

import javax.annotation.Nonnull;

public class TrackSpeedSwitch extends TrackSwitch {

    public TrackSpeedSwitch() {
        speedController = SpeedControllerHighSpeed.instance();
    }

    @Override
    public void onMinecartPass(@Nonnull EntityMinecart cart) {
        TrackSpeed.testCartSpeedForBasic(this, cart);
    }

    @Override
    public EnumTrack getTrackType() {
        return EnumTrack.SPEED_SWITCH;
    }
}
