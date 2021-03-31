package com.tfowa.tfowamod.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class Gas_Block extends Block {
    public Gas_Block(){
        super(AbstractBlock.Properties.create(Material.AIR).hardnessAndResistance(3600000f, 10f).sound(SoundType.GLASS).doesNotBlockMovement());
    }
}
