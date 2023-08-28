package com.beauver.swagsmp.handlers;

import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.CalculateDates;
import com.beauver.swagsmp.util.CodeGenerators;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Date;

public class MuteHandler {

    private final PlayerDataManager playerDataManager;
    private DiscordBot discordBot;

    public MuteHandler(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void setMuteHandler(DiscordBot discordBot){
        this.discordBot = discordBot;
    }


    public void onUnmute(String args) {

        String targetName = args;
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        playerDataManager.updateData(target.getUniqueId(), "muted", false);
        playerDataManager.deleteData(target.getUniqueId(), "mutedBy");
        playerDataManager.deleteData(target.getUniqueId(), "mutedReason");
        playerDataManager.deleteData(target.getUniqueId(), "muteExpires");
        playerDataManager.deleteData(target.getUniqueId(), "mutedAppealCode");

        if (target.isOnline()) {
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Unmute", "You have been unmuted."));
        }
        discordBot.embedBuilderMod("DISCORD | CONSOLE", "New Unmute", "Just unmuted: " + target.getName(), Color.RED);
    }

    public void onMute(String mcUsername, String reason, String discordUser, SlashCommandInteractionEvent event){

        OfflinePlayer target = Bukkit.getOfflinePlayer(mcUsername);

        String appealCode = "M_" + CodeGenerators.codeEight();

        int mutedAmount;

        if(playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1){
            mutedAmount = 0;
        }else{
            mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
        }
        mutedAmount++;

        playerDataManager.createData(target.getUniqueId(), "username", target.getName());
        playerDataManager.createData(target.getUniqueId(), "muted", true);
        playerDataManager.createData(target.getUniqueId(), "mutedBy", "Discord | " + discordUser);
        playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
        playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
        playerDataManager.createData(target.getUniqueId(), "muteExpires", "never");
        playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

        if(target.isOnline()){
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You have been permanently muted by: " + "[Discord | " + discordUser + "] for: " + reason + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
        }
        discordBot.embedBuilderMod("DISCORD | " + discordUser, "New Mute", "Just muted: " + target.getName(), "Reason:" , reason,"Expires:", "Never", Color.RED);
        event.reply("Muted: " + target.getName() + "\nFor: " + reason + "\nExpires: Never").setEphemeral(true).queue();
    }

    public void onTempmute(String targetName, String date, String reason, String discordUser, SlashCommandInteractionEvent event) {

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        String appealCode = "M_" + CodeGenerators.codeEight();

        boolean isValidDuration = CalculateDates.isValidDuration(date);
        if (!isValidDuration) {
            event.reply("Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc.").setEphemeral(true).queue();
            return;
        }
        Date unmuteDate = CalculateDates.calculateDate(date);

        int mutedAmount;

        if (playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1) {
            mutedAmount = 0;
        } else {
            mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
        }
        mutedAmount++;

        playerDataManager.createData(target.getUniqueId(), "username", target.getName());
        playerDataManager.createData(target.getUniqueId(), "muted", true);
        playerDataManager.createData(target.getUniqueId(), "mutedBy", "Discord | " + discordUser);
        playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
        playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
        playerDataManager.createData(target.getUniqueId(), "muteExpires", String.valueOf(unmuteDate.getTime()));
        playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

        if (target.isOnline()) {
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Tempmute", "You have been temporarily muted by: " + "[Discord | " + discordUser + "] for: " + reason + "\nYou will be unmuted on: " + unmuteDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
        }
        discordBot.embedBuilderMod("Discord | " + discordUser, "New Mute", "Just muted: " + target.getName(), "Reason:", reason, "Expires:", String.valueOf(unmuteDate), Color.RED);
        event.reply("Temporarily muted " + target.getName() + "\nFor: " + reason + "\nExpires: " + unmuteDate).setEphemeral(true).queue();
    }
}
