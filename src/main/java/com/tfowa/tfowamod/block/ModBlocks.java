package com.tfowa.tfowamod.block;

import com.tfowa.tfowamod.util.Registration;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    public static final RegistryObject<Block> BOOB_BLOCK = register("boob_block",
            Boob_Block::new);

    public static final RegistryObject<Block> B_Ore = register("b_ore",
            () -> new Block(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f, 10f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> GAS_BLOCK = register("gas_block",
            Gas_Block::new);

    public static void register() { }

    private static <T extends Block>RegistryObject<T> register(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
        return toReturn;
    }
}
