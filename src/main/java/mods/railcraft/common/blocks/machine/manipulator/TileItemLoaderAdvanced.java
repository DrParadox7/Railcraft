/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.common.blocks.machine.interfaces.ITileRotate;

public class TileItemLoaderAdvanced extends TileItemLoader implements ITileRotate {

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.ITEM_LOADER_ADVANCED;
    }

}
