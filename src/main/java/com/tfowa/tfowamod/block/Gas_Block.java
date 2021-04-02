package com.tfowa.tfowamod.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class Gas_Block extends Block {
    public Gas_Block(){
        super(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(3600000f, 10f).sound(SoundType.GLASS));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
        return VoxelShapes.empty();
    }
}
