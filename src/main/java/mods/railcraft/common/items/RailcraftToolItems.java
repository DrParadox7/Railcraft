/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import mods.railcraft.common.plugins.forge.ItemRegistry;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class RailcraftToolItems {

    private static Item itemSteelShears;
    private static Item itemSteelSword;
    private static Item itemSteelShovel;
    private static Item itemSteelPickaxe;
    private static Item itemSteelAxe;
    private static Item itemSteelHoe;
    private static Item itemSteelHelmet;
    private static Item itemSteelArmor;
    private static Item itemSteelLegs;
    private static Item itemSteelBoots;
    private static Item itemCoalCoke;
    private static Item itemOveralls;

    public static void initializeToolsArmor() {
        registerSteelShears();
        registerSteelSword();
        registerSteelShovel();
        registerSteelPickaxe();
        registerSteelAxe();
        registerSteelHoe();

        registerSteelHelmet();
        registerSteelArmor();
        registerSteelLegs();
        registerSteelBoots();

        registerOveralls();
    }

    private static void registerOveralls() {
        Item item = itemOveralls;
        if (item == null) {
            String tag = "railcraft.armor.overalls";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemOveralls = new ItemOveralls();
                item.setUnlocalizedName(tag);
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item),
                        "III",
                        "I I",
                        "I I",
                        'I', new ItemStack(Blocks.wool, 1, 3));

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWorkshop(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getOveralls() {
        if (itemOveralls == null)
            return null;
        return new ItemStack(itemOveralls);
    }

    private static void registerSteelShears() {
        Item item = itemSteelShears;
        if (item == null) {
            String tag = "railcraft.tool.steel.shears";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelShears = new ItemSteelShears();
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), false,
                        " I",
                        "I ",
                        'I', "ingotSteel");

                LootPlugin.addLootTool(new ItemStack(item), 1, 1, tag);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelShears() {
        if (itemSteelShears == null)
            return new ItemStack(Items.shears);
        return new ItemStack(itemSteelShears);
    }

    private static void registerSteelSword() {
        Item item = itemSteelSword;
        if (item == null) {
            String tag = "railcraft.tool.steel.sword";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelSword = new ItemSteelSword();
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), false,
                        " I ",
                        " I ",
                        " S ",
                        'I', "ingotSteel",
                        'S', new ItemStack(Items.stick));

                LootPlugin.addLootWarrior(new ItemStack(item), 1, 1, tag);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelSword() {
        if (itemSteelSword == null)
            return new ItemStack(Items.iron_sword);
        return new ItemStack(itemSteelSword);
    }

    private static void registerSteelShovel() {
        Item item = itemSteelShovel;
        if (item == null) {
            String tag = "railcraft.tool.steel.shovel";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelShovel = new ItemSteelShovel();
                ItemRegistry.registerItem(item);
                HarvestPlugin.setToolClass(item, "shovel", 2);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), false, new Object[]{
                    " I ",
                    " S ",
                    " S ",
                    'I', "ingotSteel",
                    'S', new ItemStack(Items.stick)
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);

                LootPlugin.addLootTool(new ItemStack(item), 1, 1, tag);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelShovel() {
        if (itemSteelShovel == null)
            return new ItemStack(Items.iron_shovel);
        return new ItemStack(itemSteelShovel);
    }

    private static void registerSteelPickaxe() {
        Item item = itemSteelPickaxe;
        if (item == null) {
            String tag = "railcraft.tool.steel.pickaxe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelPickaxe = new ItemSteelPickaxe();
                ItemRegistry.registerItem(item);
                HarvestPlugin.setToolClass(item, "pickaxe", 2);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), false, new Object[]{
                    "III",
                    " S ",
                    " S ",
                    'I', "ingotSteel",
                    'S', new ItemStack(Items.stick)
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);

                LootPlugin.addLootTool(new ItemStack(item), 1, 1, tag);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelPickaxe() {
        if (itemSteelPickaxe == null)
            return new ItemStack(Items.iron_pickaxe);
        return new ItemStack(itemSteelPickaxe);
    }

    private static void registerSteelAxe() {
        Item item = itemSteelAxe;
        if (item == null) {
            String tag = "railcraft.tool.steel.axe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelAxe = new ItemSteelAxe();
                ItemRegistry.registerItem(item);
                HarvestPlugin.setToolClass(item, "axe", 2);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true,
                        "II ",
                        "IS ",
                        " S ",
                        'I', "ingotSteel",
                        'S', new ItemStack(Items.stick));

                LootPlugin.addLootTool(new ItemStack(item), 1, 1, tag);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelAxe() {
        if (itemSteelAxe == null)
            return new ItemStack(Items.iron_axe);
        return new ItemStack(itemSteelAxe);
    }

    private static void registerSteelHoe() {
        Item item = itemSteelHoe;
        if (item == null) {
            String tag = "railcraft.tool.steel.hoe";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelHoe = new ItemSteelHoe();
                ItemRegistry.registerItem(item);

                IRecipe recipe = new ShapedOreRecipe(new ItemStack(item), true, new Object[]{
                    "II ",
                    " S ",
                    " S ",
                    'I', "ingotSteel",
                    'S', new ItemStack(Items.stick)
                });
                CraftingManager.getInstance().getRecipeList().add(recipe);

                ItemRegistry.registerItemStack(tag, new ItemStack(item));
            }
        }
    }

    public static ItemStack getSteelHoe() {
        if (itemSteelHoe == null)
            return new ItemStack(Items.iron_hoe);
        return new ItemStack(itemSteelHoe);
    }

    private static void registerSteelHelmet() {
        Item item = itemSteelHelmet;
        if (item == null) {
            String tag = "railcraft.armor.steel.helmet";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelHelmet = new ItemSteelArmor(0);
                item.setUnlocalizedName(tag);
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true, new Object[]{
                    "III",
                    "I I",
                    'I', "ingotSteel",});

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWarrior(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getSteelHelm() {
        if (itemSteelHelmet == null)
            return null;
        return new ItemStack(itemSteelHelmet);
    }

    private static void registerSteelArmor() {
        Item item = itemSteelArmor;
        if (item == null) {
            String tag = "railcraft.armor.steel.plate";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelArmor = new ItemSteelArmor(1);
                item.setUnlocalizedName(tag);
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true, new Object[]{
                    "I I",
                    "III",
                    "III",
                    'I', "ingotSteel",});

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWarrior(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getSteelArmor() {
        if (itemSteelArmor == null)
            return null;
        return new ItemStack(itemSteelArmor);
    }

    private static void registerSteelLegs() {
        Item item = itemSteelLegs;
        if (item == null) {
            String tag = "railcraft.armor.steel.legs";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelLegs = new ItemSteelArmor(2);
                item.setUnlocalizedName(tag);
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true, new Object[]{
                    "III",
                    "I I",
                    "I I",
                    'I', "ingotSteel",});

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWarrior(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getSteelLegs() {
        if (itemSteelLegs == null)
            return null;
        return new ItemStack(itemSteelLegs);
    }

    private static void registerSteelBoots() {
        Item item = itemSteelBoots;
        if (item == null) {
            String tag = "railcraft.armor.steel.boots";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = itemSteelBoots = new ItemSteelArmor(3);
                item.setUnlocalizedName(tag);
                ItemRegistry.registerItem(item);

                CraftingPlugin.addShapedRecipe(new ItemStack(item), true, new Object[]{
                    "I I",
                    "I I",
                    'I', "ingotSteel",});

                ItemRegistry.registerItemStack(tag, new ItemStack(item));

                LootPlugin.addLootWarrior(new ItemStack(item), 1, 1, tag);
            }
        }
    }

    public static ItemStack getSteelBoots() {
        if (itemSteelBoots == null)
            return null;
        return new ItemStack(itemSteelBoots);
    }

    public static void registerCoalCoke() {
        if (itemCoalCoke == null) {
            String tag = "railcraft.fuel.coke";
            if (RailcraftConfig.isItemEnabled(tag)) {
                Item item = itemCoalCoke = new ItemRailcraft().setUnlocalizedName(tag);
                ItemRegistry.registerItem(itemCoalCoke);

                LootPlugin.addLootTool(new ItemStack(item), 4, 16, tag);
                LootPlugin.addLootWorkshop(new ItemStack(item), 4, 16, tag);

                ItemRegistry.registerItemStack("fuel.coke", new ItemStack(itemCoalCoke));
                OreDictionary.registerOre("fuelCoke", new ItemStack(itemCoalCoke));
            }
        }
    }

    public static ItemStack getCoalCoke() {
        return getCoalCoke(1);
    }

    public static ItemStack getCoalCoke(int qty) {
        if (itemCoalCoke == null)
            return null;
        return new ItemStack(itemCoalCoke, qty);
    }

}