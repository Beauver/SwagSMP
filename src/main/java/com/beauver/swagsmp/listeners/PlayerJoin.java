package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.Date;
import java.util.UUID;

public class PlayerJoin implements Listener {
    private final PlayerDataManager playerDataManager;

    private final DiscordBot discordBot;

    public PlayerJoin(PlayerDataManager playerDataManager, DiscordBot discordBot) {
        this.playerDataManager = playerDataManager;
        this.discordBot = discordBot;
    }
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //setting the basic data to player file
        playerDataManager.createData(player.getUniqueId(), "username", player.getName());
        playerDataManager.createData(player.getUniqueId(), "pvpStatus", "off");
        playerDataManager.createData(player.getUniqueId(), "isOnline", true);

        //sending messages
        player.sendMessage(MessageManager.messageGenerator("WARNING", "PvP",
                        Component.text("You can currently not engage in PvP."))
                .append(Component.text("\nTo change this please write: /pvp or click here\n")).clickEvent(ClickEvent.runCommand("/pvp")).color(TextColor.fromHexString("#f09c0b")));

        //Unmute on join (if needed)
        if(playerDataManager.readDataBoolean(player.getUniqueId(), "muted")){
            if(playerDataManager.readDataString(player.getUniqueId(), "muteExpires").equalsIgnoreCase("never")){
                String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                String reason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                player.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You permanently are muted for: " + reason + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
            }else{
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
                }else{
                    String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                    String reason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                    player.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You are muted for: " + reason + "\nUntil: " + expirationDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
                }
            }
        }

        try {
            TextChannel textChannel = discordBot.getTextChannel("MinecraftDiscordChannel");

            if(textChannel == null){
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":wave: " + player.getName() + " joined the server.")
                    .setColor(Color.GREEN); // Customize the embed color
            textChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.createData(player.getUniqueId(), "isOnline", false);

        try {
            TextChannel textChannel = discordBot.getTextChannel("MinecraftDiscordChannel");

            if(textChannel == null){
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":wave: " + player.getName() + " left the server.")
                    .setColor(Color.RED); // Customize the embed color
            textChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerProfile playerProfile = event.getPlayerProfile();
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerProfile.getId());
        UUID playerUUID = event.getUniqueId();
        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);

        if (banList.isBanned(playerProfile)) {
            BanEntry<PlayerProfile> banEntry = banList.getBanEntry(playerProfile);

            if (banEntry != null) {
                String appealCode = playerDataManager.readDataString(playerUUID, "appealCode");
                String banReason = banEntry.getReason();
                String bannedBy = banEntry.getSource();
                Date banExpiresDate = banEntry.getExpiration();

                if (appealCode == null) {
                    appealCode = "null";
                }

                Component kickMessage = MessageManager.messageGenerator("ERROR", "Ban", Component.text("", Style.style(TextDecoration.BOLD)))
                        .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                        .append(Component.text(banReason).color(TextColor.fromHexString("#f09c0b")))
                        .append(Component.text("\nBanned By: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                        .append(Component.text(bannedBy).color(TextColor.fromHexString("#f09c0b")))
                        .append(Component.text("\nAppeal Code: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                        .append(Component.text(appealCode).color(TextColor.fromHexString("#f09c0b")));

                if (banExpiresDate != null) {
                    kickMessage = kickMessage.append(Component.text("\nExpires: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                            .append(Component.text(banExpiresDate.toString()).color(TextColor.fromHexString("#f09c0b")));
                } else {
                    kickMessage = kickMessage.append(Component.text("\nExpires: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                            .append(Component.text("never").color(TextColor.fromHexString("#f09c0b")));
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
            }
        }else if(!player.isWhitelisted()){

            Component kickMessage = MessageManager.messageGenerator("ERROR", "Kick", Component.text("", Style.style(TextDecoration.BOLD)))
                    .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                    .append(Component.text("You are not whitelisted on our server.").color(TextColor.fromHexString("#f09c0b")));

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMessage);
        }else{
            if(playerDataManager.readDataString(playerUUID, "banReason") != null){
                playerDataManager.deleteData(playerUUID, "banReason");
                playerDataManager.deleteData(playerUUID, "bannedBy");
                playerDataManager.deleteData(playerUUID, "banExpires");
                playerDataManager.deleteData(playerUUID, "appealCode");
                playerDataManager.updateData(playerUUID, "banned", false);
            }
        }
    }
}
