package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;
import java.util.UUID;

public class PlayerJoin implements Listener {
    private final PlayerDataManager playerDataManager;

    public PlayerJoin(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        //setting the basic data to player file
        playerDataManager.createData(player.getUniqueId(), "username", player.getName());
        playerDataManager.createData(player.getUniqueId(), "pvpStatus", "off");
        playerDataManager.createData(player.getUniqueId(), "isOnline", true);

        //sending messages
        player.sendMessage(MessageManager.messageGenerator("WARNING", "PvP",
                        Component.text("You can currently not engage in PvP."))
                .append(Component.text("\nTo change this please write: /pvp")).clickEvent(ClickEvent.runCommand("/pvp")));
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.createData(player.getUniqueId(), "isOnline", false);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerProfile playerProfile = event.getPlayerProfile();
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
            }else{

            }
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
