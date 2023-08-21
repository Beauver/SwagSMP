package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        player.sendMessage(MessageManager.messageGenerator("WARNING",
                        Component.text("You can currently not engage in PvP."))
                .append(Component.text("To change this please write: /pvp")).clickEvent(ClickEvent.runCommand("/pvp")));
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerDataManager.createData(player.getUniqueId(), "isOnline", false);
    }

}
