package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void onPlayerPreLogin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(playerDataManager.readDataBoolean(player.getUniqueId(), "banned")){
            String appealCode = playerDataManager.readDataString(player.getUniqueId(), "appealCode");
            String banReason = playerDataManager.readDataString(player.getUniqueId(), "banReason");
            String bannedBy = playerDataManager.readDataString(player.getUniqueId(), "bannedBy");

            KickHandler.kickBanPlayer(player, banReason, bannedBy, appealCode);
        }

    }
}
