package com.tfowa.tfowamod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tfowa.tfowamod.TFOWAMod;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.Random;

import static net.minecraft.util.math.MathHelper.clamp;

@Mod.EventBusSubscriber(modid = TFOWAMod.MOD_ID)
public class EchoCommand {

    public static int sendMessage(CommandContext<CommandSource> commandContext, String message) throws CommandSyntaxException{
        TranslationTextComponent finalText = new TranslationTextComponent("chat.type.announcement",
                commandContext.getSource().getDisplayName(), new StringTextComponent(message));

        Entity entity = commandContext.getSource().getEntity();
        if(entity != null){
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.CHAT, entity.getUniqueID());
            //func_232641_a is sendMessage()
        } else {
            commandContext.getSource().getServer().getPlayerList().func_232641_a_(finalText, ChatType.SYSTEM, Util.DUMMY_UUID);
        }
        return 1;
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event){
        CommandDispatcher<CommandSource> d = event.getServer().getCommandManager().getDispatcher();

        //Echo Command
        d.register(Commands.literal("echo").requires(source -> {
            try{
                return source.asPlayer() != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        }).then(Commands.argument("msg", MessageArgument.message())
                .executes(command -> {
            ITextComponent iTextComponent = MessageArgument.getMessage(command, "msg");
            sendMessage(command, iTextComponent.getUnformattedComponentText());
            return 1;
        })));

        d.register(Commands.literal("sendMessage").requires(source -> source.hasPermissionLevel(1))
                .executes(command -> sendMessage(command, "THIS IS A TEST MESSAGE!!!")));

    }

}
