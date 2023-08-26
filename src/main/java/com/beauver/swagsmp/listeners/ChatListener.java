package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.awt.*;
import java.time.Instant;
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
            event.setCancelled(true);
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
                    event.setCancelled(true);
                    String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                    String reason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                    player.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You are muted for: " + reason + "\nUntil: " + expirationDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
                }
            }
        }else{
            try {
                TextChannel textChannel = discordBot.getTextChannel("MinecraftDiscordChannel");

                if(textChannel == null){
                    return;
                }

                Component messageComponent = event.message();
                String message = LegacyComponentSerializer.legacySection().serialize(messageComponent);

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("<" + player.getName() + "> " + message)
                        .setColor(Color.ORANGE); // Customize the embed color
                textChannel.sendMessageEmbeds(embed.build()).queue();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event){
        try {
            TextChannel textChannel = discordBot.getTextChannel("MinecraftDiscordChannel");

            Component messageComponent = event.message();

            if(messageComponent == null){
                return;
            }

            if(textChannel == null){
                return;
            }

            String message = LegacyComponentSerializer.legacySection().serialize(messageComponent);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":trophy: " + message)
                    .setColor(Color.GREEN); // Customize the embed color
            textChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
