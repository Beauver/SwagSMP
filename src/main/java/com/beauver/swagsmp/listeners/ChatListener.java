package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.commands.moderation.MuteCommand;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.util.Date;

public class ChatListener implements Listener {

    private final PlayerDataManager playerDataManager;
    private final DiscordBot discordBot;

    public ChatListener(PlayerDataManager playerDataManager, DiscordBot discordBot) {
        this.playerDataManager = playerDataManager;
        this.discordBot = discordBot;
    }


    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if(playerDataManager.readDataBoolean(player.getUniqueId(), "muted")){
            if(playerDataManager.readDataString(player.getUniqueId(), "muteExpires").equalsIgnoreCase("never")){
                event.setCancelled(true);
                String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                String reason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                player.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You are permanently muted for: " + reason + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
            }else{
                event.setCancelled(true);
                long timestamp = Long.parseLong(playerDataManager.readDataString(player.getUniqueId(), "muteExpires"));
                Date expirationDate = new Date(timestamp);
                if(new Date().after(expirationDate)){
                    playerDataManager.updateData(player.getUniqueId(), "muted", false);
                    playerDataManager.deleteData(player.getUniqueId(), "mutedBy");
                    playerDataManager.deleteData(player.getUniqueId(), "mutedReason");
                    playerDataManager.deleteData(player.getUniqueId(), "muteExpires");
                    playerDataManager.deleteData(player.getUniqueId(), "mutedAppealCode");

                    player.sendMessage(MessageManager.messageGenerator("WARNING", "Unmute", "You have been unmuted."));
                    discordBot.embedBuilderMod("CONSOLE", "New Unmute", "Just unmuted: " + player.getName(), Color.RED);
                    event.setCancelled(false);
                }else{
                    String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                    String reason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                    player.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You are muted for: " + reason + "\nUntil: " + expirationDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
                }
            }
        }
    }
}
