package com.beauver.swagsmp.handlers;

import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;

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

}
