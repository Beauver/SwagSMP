package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.util.MessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

    @CommandAlias("clearchat")
    @Description("Clears the chat for everyone.")
    @CommandPermission("swagsmp.moderation.clearchat")
    public static void onClearChat(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.hasPermission("swagsmp.moderation.clearchat")){
                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Clearchat", "Your chat has not been cleared due to you being an admin."));
            }else{
                for(int i = 0; i < 100; i++){
                    player.sendMessage(Component.text(" "));
                }
                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Clearchat", "An admin has cleared the chat."));
            }
        }
    }
}
