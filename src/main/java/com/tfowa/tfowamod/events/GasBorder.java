package com.tfowa.tfowamod.events;

import com.google.common.base.Objects;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tfowa.tfowamod.TFOWAMod;

import com.tfowa.tfowamod.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.*;



//This is a Point Class, just named Pt, cus idk how java handles multiples.
class Pt{
    public double x;
    public double y;

    public Pt(double x, double y){
       this.x = x;
       this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pt pt = (Pt) o;
        return Double.compare(pt.x, x) == 0 &&
                Double.compare(pt.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}

//Formerlly BorderSide
class Line {
    Pt corner1, corner2;
    public Line(Pt corner1, Pt corner2){
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line that = (Line) o;
        return Objects.equal(corner1, that.corner1) &&
                Objects.equal(corner2, that.corner2);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(corner1, corner2);
    }

}

class Border{
    public Line top, bottom, left, right;
    Pt center;
    public int borderNumber;
    public int radius;
    public Pt ltCorner, rtCorner, rbCorner, lbCorner;
    public ArrayList<Pt> corners = new ArrayList<Pt>();
    public Border(Pt center, int radius){
        this.center = center;
        this.radius = radius;

        this.ltCorner = new Pt(center.x - radius, center.y + radius);
        this.rtCorner = new Pt(center.x + radius, center.y + radius);
        this.rbCorner = new Pt(center.x + radius, center.y- radius);
        this.lbCorner = new Pt(center.x - radius, center.y-radius);

        this.corners.add(ltCorner);
        this.corners.add(rtCorner);
        this.corners.add(rbCorner);
        this.corners.add(lbCorner);

        this.top = new Line(this.ltCorner, this.rtCorner);
        this.bottom = new Line(this.lbCorner, this.rbCorner);
        this.left = new Line(this.ltCorner, this.lbCorner);
        this.right = new Line(this.rtCorner, this.rbCorner);
    }


    public void shrinkBorderByOne(){
        this.ltCorner.y -= 1;
        this.rtCorner.x -= 1;
        this.rtCorner.y -= 1;
        this.rbCorner.x -= 1;
        this.rbCorner.y += 1;
        this.lbCorner.x += 1;
        this.ltCorner.y += 1;
    }

    public void shrinkTopByOne(){
        this.ltCorner.y -= 1;
        this.rtCorner.y -= 1;
    }

    public void shrinkBottomByOne(){
        this.lbCorner.y += 1;
        this.rbCorner.y += 1;
    }

    public void shrinkLeftByOne(){
        this.ltCorner.x += 1;
        this.lbCorner.x += 1;
    }

    public void shrinkRightByOne(){
        this.rtCorner.x -= 1;
        this.rbCorner.x -= 1;
    }

    public boolean checkTopEq(Border other){
        if(this.ltCorner.y == other.ltCorner.y){
            return true;
        }
        return false;
    }
    public boolean checkBottomEq(Border other){
        if(this.lbCorner.y == other.lbCorner.y){
            return true;
        }
        return false;
    }

    public boolean checkLeftEq(Border other){
        if(this.lbCorner.x == other.lbCorner.x){
            return true;
        }
        return false;
    }

    public boolean checkRightEq(Border other){
        if(this.rbCorner.x == other.rbCorner.x){
            return true;
        }
        return false;
    }

}

@Mod.EventBusSubscriber(modid = TFOWAMod.MOD_ID)
class BorderHandler{
    public static int ttickCounter = 0;
    public static int btickCounter = 0;
    public static int ltickCounter = 0;
    public static int rtickCounter = 0;

    public static int ttickRate = 0; //TickRate per 20 ticks
    public static int btickRate = 0;
    public static int ltickRate = 0;
    public static int rtickRate = 0;

    public static boolean tOn = false;
    public static boolean bOn = false;
    public static boolean rOn = false;
    public static boolean lOn = false;

    public static Border mainBorder = new Border(new Pt(0, 0), 10);

    /*
    public static Border makeNewBorder(double decreasePercent){
        Random r = new Random();

        Line tempLine;
        int tempRad = (int) (mainBorder.radius * decreasePercent);
        Border tempBorder = new Border(mainBorder.center, tempRad);

        //Pick random side of temp border
        int side = r.nextInt(4 );
        if (side == 0){
            tempLine = mainBorder.top;
        } else if (side == 1){
            tempLine = mainBorder.right;
        } else if (side == 2){
            tempLine = mainBorder.bottom;
        }else if (side == 3){
            tempLine = mainBorder.left;
        }



    }*/


    //Close seconds is an integer amount of time for the Border to close in Seconds
    public static void calcTickRates(Border newBorder, int closeSeconds){
        //Top
        double mainTop = mainBorder.top.corner1.y;
        double newTop = newBorder.top.corner1.y;
        double topDiff = mainTop - newTop;

        double mainBottom = mainBorder.bottom.corner1.y;
        double newBottom = newBorder.bottom.corner1.y;
        double bottomDiff = mainBottom - newBottom;

        double mainLeft = mainBorder.left.corner1.x;
        double newLeft = newBorder.left.corner1.x;
        double leftDiff = mainLeft - newLeft;

        double mainRight = mainBorder.right.corner1.y;
        double newRight = newBorder.right.corner1.y;
        double rightDiff = mainRight - newRight;

        ttickRate = (int)(closeSeconds/topDiff) * 20; // Ticks per Block
        btickRate = (int)(closeSeconds/bottomDiff) * 20;
        ltickRate = (int)(closeSeconds/leftDiff) * 20;
        rtickRate = (int)(closeSeconds/rightDiff) * 20;

    }



    @SubscribeEvent
    public static void shrinkTopEdge(TickEvent.ServerTickEvent event){

        if(tOn && ttickCounter == ttickRate){
            mainBorder.shrinkTopByOne();
            ttickCounter = 0;
        }
        ttickCounter += 1;
    }

    @SubscribeEvent
    public static void shrinkBottomEdge(TickEvent.ServerTickEvent event){

        if(bOn && btickCounter == btickRate){
            mainBorder.shrinkTopByOne();
            btickCounter = 0;
        }
        btickCounter += 1;
    }

    @SubscribeEvent
    public static void shrinkLeftEdge(TickEvent.ServerTickEvent event){

        if(lOn && ltickCounter == ltickRate){
            mainBorder.shrinkTopByOne();
            ltickCounter = 0;
        }
        ltickCounter += 1;
    }

    @SubscribeEvent
    public static void shrinkRightEdge(TickEvent.ServerTickEvent event){

        if(rOn && rtickCounter == rtickRate){
            mainBorder.shrinkTopByOne();
            rtickCounter = 0;
        }
        rtickCounter += 1;
    }



}

@Mod.EventBusSubscriber(modid = TFOWAMod.MOD_ID)
public class GasBorder {

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

        /*
        d.register(Commands.literal("placeBlock").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> spawnGasBlock(command.getSource().getWorld(), new BlockPos(command.getSource().getPos()))));

        d.register(Commands.literal("placeTower").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> spawnGasTower(command.getSource().getWorld(), new BlockPos(command.getSource().getPos()))));*/

        d.register(Commands.literal("startGas").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> enableDisableDamage(0)));


        //New Border centered at 0,0 with Radius of 10
        d.register(Commands.literal("newBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), ModBlocks.GAS_BLOCK.get().getDefaultState(), new Border(new Pt(0,0), 10))));

        d.register(Commands.literal("clearBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.AIR.getDefaultState(), new Border(new Pt(0,0), 10))));
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

    public static void drawTop(World world, BlockState block, Pt ltCorner, Pt rtCorner){
        //System.out.println("First Wall");
        Pt curPt = ltCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(rtCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            //System.out.println("ltCorner: "+ ltCorner.x + ", "+ltCorner.y);
            //System.out.println("finCorner: "+finCorner.x + ", " + finCorner.y);
            spawnGasTower(world, curPos, block);
            //Increment curPos.x and curPt.x
            curPos = curPos.add(1,0,0);
            curPt.x += 1;
        }
    }

    public static void drawRight(World world, BlockState block, Pt rtCorner, Pt rbCorner){
        //System.out.println("Second Wall");
        Pt curPt = rtCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(rbCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnGasTower(world, curPos, block);
            //Decrement curPos.y and curPt.y
            curPos = curPos.add(0,0,-1);
            curPt.y -= 1;
        }
    }

    public static void drawBottom(World world, BlockState block, Pt rbCorner, Pt lbCorner){
        //System.out.println("Third Wall");
        Pt curPt = rbCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(lbCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnGasTower(world, curPos, block);
            //Decrement curPos.x and curPt.x
            curPos = curPos.add(-1,0,0);
            curPt.x -= 1;
        }
    }

    public static void drawLeft(World world, BlockState block, Pt lbCorner, Pt finCorner){
        //System.out.println("Forth Wall");
        Pt curPt = lbCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(finCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnGasTower(world, curPos, block);
            //Increment curPos.y and curPt.y
            curPos = curPos.add(0,0,1);
            curPt.y += 1;
        }
    }


    public static int drawWall(World world, BlockState block, Border  border){
        try {
            ArrayList corners = border.corners;
            Pt ltCorner = (Pt) corners.get(0);
            Pt rtCorner = (Pt) corners.get(1);
            Pt rbCorner = (Pt) corners.get(2);
            Pt lbCorner = (Pt) corners.get(3);
            Pt finCorner = new Pt(ltCorner.x, ltCorner.y);

            drawTop(world, block, ltCorner, rtCorner);
            drawRight(world, block, rtCorner, rbCorner);
            drawBottom(world, block, rbCorner, lbCorner);
            drawLeft(world, block, lbCorner, finCorner);

        } catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    public static int spawnGasBlock(World world, BlockPos pos, BlockState block){
        world.setBlockState(pos, block);
        return 0;
    }

    public static void spawnGasTower(World world, BlockPos pos, BlockState block){
        int y = 0;
        while(y<255){
            //System.out.println(y);
            //System.out.println(pos.getY());

            if(world.getBlockState(pos).getMaterial() == Material.AIR || world.getBlockState(pos).getMaterial() == Material.GLASS){
                //System.out.println("Placing block...");
                world.setBlockState(pos, block);
            }
            pos = pos.add(0, 1, 0);
            y+=1;
        }
    }

}
