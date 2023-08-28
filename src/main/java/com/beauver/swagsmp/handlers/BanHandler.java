package com.beauver.swagsmp.handlers;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.CalculateDates;
import com.beauver.swagsmp.util.CodeGenerators;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Date;

public class BanHandler {

    private final PlayerDataManager playerDataManager;
    private DiscordBot discordBot;

    public BanHandler(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    public void setBanHandler(DiscordBot discordBot){
        this.discordBot = discordBot;
    }

    public void onUnban(String target){
        Plugin plugin = SwagSMPCore.getPlugin();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        if(targetPlayer.isBanned()) {
            BanList<PlayerProfile> banList = plugin.getServer().getBanList(BanList.Type.PROFILE);
            BanEntry<PlayerProfile> banEntry = banList.getBanEntry(targetPlayer.getPlayerProfile());
            banEntry.remove();
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banReason");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "bannedBy");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banExpires");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "appealCode");
            playerDataManager.updateData(targetPlayer.getUniqueId(), "banned", false);
        }
        discordBot.embedBuilderMod("DISCORD | CONSOLE", "New Unban", "Just Unbanned: " + targetPlayer.getName(), java.awt.Color.RED);
    }

    public void onBan(String mcUsername, String reason, String discordUsername, SlashCommandInteractionEvent event) {

        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);

        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(mcUsername);

        String appealCode = "B_" + CodeGenerators.codeEight();

        int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

        if (playerBannedAmount == -1) {
            playerBannedAmount = 0;
        }
        playerBannedAmount++;

        playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
        playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", "Discord | " + discordUsername);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);

        //adding the target to the banList
        banList.addBan(targetPlayer.getPlayerProfile(), reason, (Date) null, "Discord | " + discordUsername);

        //if target is online, get their online player and kick them

        if (targetPlayer.isOnline()) {
            Bukkit.getScheduler().runTask(SwagSMPCore.getPlugin(), () -> {
                Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
                KickHandler.kickBanPlayer(targetPlayerOnline, reason, "Discord | " + discordUsername, appealCode);
            });
        }
        discordBot.embedBuilderMod("Discord | " + discordUsername, "New Ban", "Just Banned: " + targetPlayer.getName(), "Reason:", reason, "Duration:", "Never", java.awt.Color.RED);
        event.reply("Banned " + targetPlayer.getName() + "\nFor: " + reason + "\nExpires: Never").setEphemeral(true).queue();
    }

    public void onTempBan(String target, String date, String reason, String discordUser, SlashCommandInteractionEvent event) {
        //getting server banlist
        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);

        boolean isValidDuration = CalculateDates.isValidDuration(date);

        if (!isValidDuration) {
            event.reply("Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc.").setEphemeral(true).queue();
            return;
        }

        Date unbanDate = CalculateDates.calculateDate(date);
        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        String appealCode = "B_" + CodeGenerators.codeEight();

        int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

        if (playerBannedAmount == -1) {
            playerBannedAmount = 0;
        }
        playerBannedAmount++;

        playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
        playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", "Discord | " + discordUser);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banExpires", String.valueOf(unbanDate));

        banList.addBan(targetPlayer.getPlayerProfile(), reason, unbanDate, "Discord | " + discordUser);
        discordBot.embedBuilderMod("Discord | " + discordUser, "New Ban", "Just Banned: " + targetPlayer.getName(), "Reason:", reason, "Duration:", String.valueOf(unbanDate), java.awt.Color.RED);
        event.reply("Temporarily banned " + targetPlayer.getName() + "\nFor: " + reason + "\nExpires: " + unbanDate).setEphemeral(true).queue();

        if (targetPlayer.isOnline()) {
            Bukkit.getScheduler().runTask(SwagSMPCore.getPlugin(), () -> {
                Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
                KickHandler.kickBanPlayer(targetPlayerOnline, reason, "Discord | " + discordUser, appealCode, unbanDate);
            });
        }
    }
}
