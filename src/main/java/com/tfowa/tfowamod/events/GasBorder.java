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
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;



//This is a Point Class, just named Pt, cus idk how java handles multiples.
class Pt{
    public int x;
    public int y;

    public Pt(int x, int y){
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

    public static double ttickRate = 0; //TickRate per 20 ticks
    public static double btickRate = 0;
    public static double ltickRate = 0;
    public static double rtickRate = 0;

    public static boolean tOn = false;
    public static boolean bOn = false;
    public static boolean rOn = false;
    public static boolean lOn = false;

    public static Border mainBorder = new Border(new Pt(0, 0), 100);
    public static Border newBorder;
    public static Border tempBorder;

    /*
    *
    * Random r = new Random();
            ArrayList<Integer> pointsArr = new ArrayList<Integer>();
            pointsArr.add(p1.x);
            pointsArr.add(p2.x);
            pointsArr.add(p1.y);
            pointsArr.add(p2.y);
            int min = Collections.min(pointsArr);
            min = Math.abs(min) * 2;
            p1.x += min;
            p2.x += min;
            p1.y += min;
            p2.y += min;

            int xMin;
            int xMax;
            int yMin;
            int yMax;
            ArrayList<Integer> xArr = new ArrayList<Integer>();
            xArr.add(p1.x);
            xArr.add(p2.x);
            System.out.println("xArr: " + xArr);
            Collections.sort(xArr);
            System.out.println("PostSort xArr: " + xArr);
            xMin = xArr.get(0);
            xMax = xArr.get(1);
            int xRand = r.nextInt((xMax - xMin) + 1) + min;

            ArrayList<Integer> yArr = new ArrayList<Integer>();
            yArr.add(p1.y);
            yArr.add(p2.y);
            System.out.println("yArr: " + yArr);
            Collections.sort(yArr);
            System.out.println("PostSort yArr: " + yArr);
            yMin = yArr.get(0);
            yMax = yArr.get(1);

            int yRand = r.nextInt((yMax - yMin) + 1) + min;

            return new Pt(xRand-min, yRand-min);*/


    public static Pt randPoint(Pt p1, Pt p2){
        int xOffset = 0;
        int yOffset = 0;
        int xMin = 0;
        int xMax = 0;
        int yMin = 0;
        int yMax = 0;
        try{

            Random r = new Random();
            if(p1.x < p2.x){
                xOffset = Math.abs(p1.x) * 2;
                xMin = xOffset + p1.x;
                xMax = xOffset + p2.x;
            }
            if(p2.x < p1.x){
                xOffset = Math.abs(p2.x) * 2;
                xMin = xOffset + p2.x;
                xMax = xOffset + p1.x;
            }
            if(p1.y < p2.y){
                yOffset = Math.abs(p1.y) * 2;
                yMin = yOffset + p1.y;
                yMax = yOffset + p2.y;
            }
            if(p2.y < p1.y){
                yOffset = Math.abs(p2.y) * 2;
                yMin = yOffset + p2.y;
                yMax = yOffset + p1.y;
            }

            int xRand = (r.nextInt((xMax - xMin) + 1) + xMin)-xOffset;
            int yRand = (r.nextInt((yMax - yMin) + 1) + yMin)-yOffset;

            return new Pt(xRand, yRand);

        } catch (Exception e){
            e.printStackTrace();
            return new Pt(0, 0);
        }

    }


    public static int makeNewBorder(double decreasePercent){
        Random r = new Random();

        Line tempLine;
        int tempRad = (int) (mainBorder.radius * decreasePercent);
        int newRad = mainBorder.radius - tempRad;
        tempBorder = new Border(mainBorder.center, tempRad);

        //Pick random side of temp border
        int side = r.nextInt(4 );
        System.out.println("Side: " + side);
        if (side == 0){
            tempLine = tempBorder.top;
            System.out.println("Top");
        } else if (side == 1){
            tempLine = tempBorder.right;
            System.out.println("Right");
        } else if (side == 2){
            tempLine = tempBorder.bottom;
            System.out.println("Bottom");
        }else if (side == 3){
            tempLine = tempBorder.left;
            System.out.println("Left");
        }else{
            tempLine = new Line(new Pt(0, 0), new Pt(0, 0));
        }

        //System.out.println("PT1X: " + tempLine.corner1.x + " PT1Y: " + tempLine.corner1.y + " PT2X: " + tempLine.corner2.x + " PT2Y: " + tempLine.corner2.y);
        Pt randPt = randPoint(tempLine.corner1, tempLine.corner2);

        System.out.println("RX: " + randPt.x + " RY: " + randPt.y);

        newBorder = new Border(randPt, newRad);

        return 0;
    }

    public static int setMainToNew(){
        mainBorder = newBorder;
        return 0;
    }


    //Close seconds is an integer amount of time for the Border to close in Seconds
    public static void calcTickRates(int closeSeconds){
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

        System.out.println("Close Seconds: " + closeSeconds);
        System.out.println("mainTop: " + mainTop);
        System.out.println("newTop: " + newTop);
        System.out.println("topDiff: " + topDiff);

        ttickRate = (int) Math.abs((closeSeconds/topDiff) * 200); // Ticks per Block
        btickRate = (int) Math.abs((closeSeconds/bottomDiff) * 200);
        ltickRate = (int) Math.abs((closeSeconds/leftDiff) * 200);
        rtickRate = (int) Math.abs((closeSeconds/rightDiff) * 200);

        System.out.println("ttickRate: " + ttickRate);
        System.out.println("btickRate: " + btickRate);
        System.out.println("ltickRate: " + ltickRate);
        System.out.println("rtickRate: " + rtickRate);

    }

    public static void allSidesOn(){
        tOn = true;
        bOn = true;
        rOn = true;
        lOn = true;
        System.out.println("All Sides On!!!");
    }

    public static int moveBorder(World world, double decreasePercent){
        GasBorder.drawWall(world, ModBlocks.GAS_BLOCK.get().getDefaultState(), mainBorder);
        if(newBorder == null){
            makeNewBorder(decreasePercent);
        }
        calcTickRates(30);
        allSidesOn();
        return 0;
    }



    @SubscribeEvent
    public static void shrinkTopEdge(TickEvent.WorldTickEvent event){
        //System.out.println("tTick Counter: " + ttickCounter + " tTick Goal: " + ttickRate);
        if(newBorder != null) {
            if (tOn && ttickCounter % ttickRate == 0) {
                //System.out.println("Moving Top!!!");
                GasBorder.drawTop(event.world, Blocks.AIR.getDefaultState(), mainBorder.ltCorner, mainBorder.rtCorner);
                //System.out.println("ltCornerX: " + mainBorder.ltCorner.x + " ltCornerY: " + mainBorder.ltCorner.y + " rtCornerX: " + mainBorder.rtCorner.x + " rtCornerY: " + mainBorder.rtCorner.y);
                mainBorder.shrinkTopByOne();
                //System.out.println("ltCornerX: " + mainBorder.ltCorner.x + " ltCornerY: " + mainBorder.ltCorner.y + " rtCornerX: " + mainBorder.rtCorner.x + " rtCornerY: " + mainBorder.rtCorner.y);
                GasBorder.drawTop(event.world, ModBlocks.GAS_BLOCK.get().getDefaultState(), mainBorder.ltCorner, mainBorder.rtCorner);
                ttickCounter = 0;
            }

            if (mainBorder.checkTopEq(newBorder)) {
                //System.out.println("Top Off!");
                tOn = false;
            }
            if (tOn) {
                ttickCounter += 1;
            }
        }
    }

    @SubscribeEvent
    public static void shrinkBottomEdge(TickEvent.WorldTickEvent event){
        if(newBorder != null) {
            if (bOn && btickCounter % btickRate == 0) {
                GasBorder.drawTop(event.world, Blocks.AIR.getDefaultState(), mainBorder.lbCorner, mainBorder.rbCorner);
                mainBorder.shrinkBottomByOne();
                GasBorder.drawTop(event.world, ModBlocks.GAS_BLOCK.get().getDefaultState(), mainBorder.lbCorner, mainBorder.rbCorner);
                btickCounter = 0;
            }
            if (mainBorder.checkBottomEq(newBorder)) {
                bOn = false;
            }
            if (bOn) {
                btickCounter += 1;
            }
        }
    }

    @SubscribeEvent
    public static void shrinkLeftEdge(TickEvent.WorldTickEvent event){
        if(newBorder != null) {
            //System.out.println("I entered shrink left");
            if (lOn && ltickCounter % ltickRate == 0) {
                System.out.println("I tried to remove the gas blocks");
                GasBorder.drawLeft(event.world, Blocks.AIR.getDefaultState(), mainBorder.lbCorner, mainBorder.ltCorner);
                System.out.println("I successfully removed the gas blocks and am trying to shrink the border.");
                mainBorder.shrinkLeftByOne();
                System.out.println("I shrunk the border and am trying to draw the gas blocks");
                GasBorder.drawLeft(event.world, ModBlocks.GAS_BLOCK.get().getDefaultState(), mainBorder.lbCorner, mainBorder.ltCorner);
                System.out.println("I drew the gas blocks.");
                ltickCounter = 0;
                System.out.println("I reset counter to zero.");
            }
            if (mainBorder.checkLeftEq(newBorder)) {
                lOn = false;
            }
            if (lOn) {
                ltickCounter += 1;
            }
        }
    }

    @SubscribeEvent
    public static void shrinkRightEdge(TickEvent.WorldTickEvent event){
        if(newBorder != null) {
            if (rOn && rtickCounter % rtickRate == 0) {
                GasBorder.drawRight(event.world, Blocks.AIR.getDefaultState(), mainBorder.rtCorner, mainBorder.rbCorner);
                mainBorder.shrinkRightByOne();
                GasBorder.drawRight(event.world, ModBlocks.GAS_BLOCK.get().getDefaultState(), mainBorder.rtCorner, mainBorder.rbCorner);
                rtickCounter = 0;
            }
            if (mainBorder.checkRightEq(newBorder)) {
                rOn = false;
            }
            if (rOn) {
                rtickCounter += 1;
            }
        }
    }

    @SubscribeEvent
    public static void checkBorderEquals(TickEvent.ServerTickEvent event){
        if(newBorder != null){
            if(mainBorder == newBorder){
                mainBorder = newBorder;
            }
        }
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
        d.register(Commands.literal("drawBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), ModBlocks.GAS_BLOCK.get().getDefaultState(), BorderHandler.mainBorder)));

        d.register(Commands.literal("clearBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.AIR.getDefaultState(), BorderHandler.mainBorder)));

        d.register(Commands.literal("pickNewBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> BorderHandler.makeNewBorder(.2)));

        d.register(Commands.literal("drawNewBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.GLASS.getDefaultState(), BorderHandler.newBorder)));

        d.register(Commands.literal("clearNewBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.AIR.getDefaultState(), BorderHandler.newBorder)));

        d.register(Commands.literal("drawTempBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.COBBLESTONE.getDefaultState(), BorderHandler.tempBorder)));

        d.register(Commands.literal("clearTempBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.drawWall(command.getSource().getWorld(), Blocks.AIR.getDefaultState(), BorderHandler.tempBorder)));

        d.register(Commands.literal("drawBorderCenter").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.spawnTower(command.getSource().getWorld(), new BlockPos(BorderHandler.mainBorder.center.x, 0, BorderHandler.mainBorder.center.y), Blocks.DIAMOND_BLOCK.getDefaultState(), true)));

        d.register(Commands.literal("drawNewBorderCenter").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> GasBorder.spawnTower(command.getSource().getWorld(), new BlockPos(BorderHandler.newBorder.center.x, 0, BorderHandler.newBorder.center.y), Blocks.GOLD_BLOCK.getDefaultState(), true)));

        d.register(Commands.literal("setMainToNew").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> BorderHandler.setMainToNew()));

        d.register(Commands.literal("moveBorder").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).executes(command -> BorderHandler.moveBorder(command.getSource().getWorld(), 20)));

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
        ltCorner = new Pt(ltCorner.x, ltCorner.y);
        rtCorner = new Pt(rtCorner.x, rtCorner.y);
        Pt curPt = ltCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(rtCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            //System.out.println("ltCorner: "+ ltCorner.x + ", "+ltCorner.y);
            //System.out.println("finCorner: "+finCorner.x + ", " + finCorner.y);
            spawnTower(world, curPos, block, false);
            //Increment curPos.x and curPt.x
            curPos = curPos.add(1,0,0);
            curPt.x += 1;
        }
    }

    public static void drawRight(World world, BlockState block, Pt rtCorner, Pt rbCorner){
        //System.out.println("Second Wall");
        rbCorner = new Pt(rbCorner.x, rbCorner.y);
        rtCorner = new Pt(rtCorner.x, rtCorner.y);
        Pt curPt = rtCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(rbCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnTower(world, curPos, block, false);
            //Decrement curPos.y and curPt.y
            curPos = curPos.add(0,0,-1);
            curPt.y -= 1;
        }
    }

    public static void drawBottom(World world, BlockState block, Pt rbCorner, Pt lbCorner){
        //System.out.println("Third Wall");
        rbCorner = new Pt(rbCorner.x, rbCorner.y);
        lbCorner = new Pt(lbCorner.x, lbCorner.y);
        Pt curPt = rbCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        while(!curPt.equals(lbCorner)){
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnTower(world, curPos, block, false);
            //Decrement curPos.x and curPt.x
            curPos = curPos.add(-1,0,0);
            curPt.x -= 1;
        }
    }

    public static void drawLeft(World world, BlockState block, Pt lbCorner, Pt finCorner){
        //System.out.println("Second Wall");
        lbCorner = new Pt(lbCorner.x, lbCorner.y);
        finCorner = new Pt(finCorner.x, finCorner.y);
        Pt curPt = lbCorner;
        BlockPos curPos = new BlockPos( (int)curPt.x, 0, (int)curPt.y);
        System.out.println("GoalPtX: "+ finCorner.x + " GoalPtY: " + finCorner.y);
        while(!curPt.equals(finCorner)){
            System.out.println("curPtX: " + curPt.x + " curPtY: "+ curPt.y);
            //System.out.println("Drawing Border at " + curPos.getX() + ", " + curPos.getY() + ", " + curPos.getZ());
            spawnTower(world, curPos, block, false);
            //Decrement curPos.y and curPt.y
            curPos = curPos.add(0,0,1);
            curPt.y += 1;
        }
    }


    public static int drawWall(World world, BlockState block, Border  border){
        try {
            border = new Border(border.center, border.radius);
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

    public static int spawnBlock(World world, BlockPos pos, BlockState block){
        world.setBlockState(pos, block);
        return 0;
    }

    public static int spawnTower(World world, BlockPos pos, BlockState block, boolean repBlocks){
        int y = 0;
        while(y<10){
            //System.out.println(y);
            //System.out.println(pos.getY());

            if(world.getBlockState(pos).getMaterial() == Material.AIR || world.getBlockState(pos).getMaterial() == Material.GLASS || repBlocks){
                //System.out.println("Placing block...");
                world.setBlockState(pos, block);
            }
            pos = pos.add(0, 1, 0);
            y+=1;
        }
        return 0;
    }

}
