package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.MessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

    private final DiscordBot discordBot; // Make sure the discordBot is final

    public ClearChatCommand(DiscordBot discordBot) {
        this.discordBot = discordBot; // Initialize the discordBot instance
    }

    @CommandAlias("clearchat")
    @Description("Clears the chat for everyone.")
    @CommandPermission("swagsmp.moderation.clearchat")
    public void onClearChat(Player player1){
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
        discordBot.embedBuilderMod(player1.getName(), "New Clearchat", "Just cleared chat", java.awt.Color.RED);
    }
}
