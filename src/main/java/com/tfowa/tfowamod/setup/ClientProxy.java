package com.tfowa.tfowamod.setup;

import com.tfowa.tfowamod.TFOWAMod;
import com.tfowa.tfowamod.block.Gas_Block;
import com.tfowa.tfowamod.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TFOWAMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy implements IProxy {
    @Override
    public void init() {
        RenderTypeLookup.setRenderLayer(ModBlocks.GAS_BLOCK.get(), RenderType.getTranslucent());
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }
}
