package com.beauver.swagsmp.handlers;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

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

}
