package com.tfowa.tfowamod.events;

import com.tfowa.tfowamod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEvents {

    @SubscribeEvent
    public void onGlowingSheep(AttackEntityEvent event){
        if(event.getPlayer().getHeldItemMainhand().getItem() == ModItems.BOOB_INGOT.get()){
            if (event.getTarget().isAlive()){
                LivingEntity target = (LivingEntity) event.getTarget();
                if(target instanceof SheepEntity){
                    PlayerEntity player = event.getPlayer();

                    //Delete 1 held item
                    player.getHeldItemMainhand().shrink(1);

                    target.addPotionEffect(new EffectInstance(Effects.GLOWING, 600));

                    if(!player.world.isRemote()){
                        String msg = TextFormatting.YELLOW + "Sheep As Succumb To The Boob!";
                        player.sendMessage(new StringTextComponent(msg), player.getUniqueID());
                    }
                }
            }
        }
    }


}
