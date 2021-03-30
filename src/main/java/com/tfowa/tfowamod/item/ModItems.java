package com.tfowa.tfowamod.item;

import com.tfowa.tfowamod.util.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;

public class ModItems {
    public static final RegistryObject<Item> BOOB_INGOT =
            Registration.ITEMS.register("boob_ingot",
                    ()->new Item(new Item.Properties().group(ItemGroup.MATERIALS).maxStackSize(69)));

    public static void register() { }
}
