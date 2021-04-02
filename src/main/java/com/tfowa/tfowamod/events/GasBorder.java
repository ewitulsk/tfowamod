package com.tfowa.tfowamod.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tfowa.tfowamod.TFOWAMod;
import com.tfowa.tfowamod.block.Gas_Block;
import com.tfowa.tfowamod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = TFOWAMod.MOD_ID)
public class GasBorder {

    public static void spawnGasBlock(){
        Gas_Block gasBlock = new Gas_Block();
    }

    public static boolean doPlayerDamage = false;

    //if 1 passed in, then enable player damage.
    public static int enableDisableDamage(int onOff){
        if(onOff == 1){
            doPlayerDamage = true;
            return 1;
        }
        doPlayerDamage = false;
        return 1;
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event){
        CommandDispatcher<CommandSource> d = event.getServer().getCommandManager().getDispatcher();

        //enableDamage Command
        d.register(Commands.literal("enableDamage").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> enableDisableDamage(1)));

        //disableDamage Command
        d.register(Commands.literal("disableDamage").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> enableDisableDamage(0)));

    }

    public static float GAS_DAMAGE_TO_PLAYER = 1f;
    static DamageSource gasDamageSource = new DamageSource("tfowamod.gas_damage");

    @SubscribeEvent
    public static void playerGasDamage(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
        BlockPos pos = player.getPosition();
        BlockState feetBlock = player.world.getBlockState(pos);
        BlockState headBlock = player.world.getBlockState(pos.up());

        if(doPlayerDamage){
            player.attackEntityFrom(gasDamageSource, GAS_DAMAGE_TO_PLAYER);
        }


        /*
        if(feetBlock.getMaterial() == Material.GLASS || headBlock.getMaterial() == Material.GLASS){
            //This is a heck of a work around, since I don't know how to make my own material im just checking to see if
            //the player is inside of glass, which is the Gas Blocks material type. It works cus u can't be in normal glass...
               player.attackEntityFrom(gasDamageSource, GAS_DAMAGE_TO_PLAYER);

        }*/
    }

}
