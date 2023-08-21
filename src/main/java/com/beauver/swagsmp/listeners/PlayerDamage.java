package com.beauver.swagsmp.listeners;

import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamage implements Listener {

    private final PlayerDataManager playerDataManager;

    public PlayerDamage(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }
    @EventHandler
    public void onPlayerAttacked(EntityDamageByEntityEvent event){
        //if the people that attack and get hit are both players
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            //save them to a variable
            Player whoWasHit = (Player) event.getEntity();
            Player whoHit = (Player) event.getDamager();

            //check if both players have PvP enabled
            if(!(playerDataManager.readDataString(whoWasHit.getUniqueId(), "pvpStatus").equalsIgnoreCase("on"))){
                //set the event to cancelled
                event.setCancelled(true);
                //send message
                whoHit.sendMessage(MessageManager.messageGenerator("WARNING", "PvP",
                        Component.text("You can not fight this player because they have PvP disabled."))
                        .append(Component.text("\nTo overwrite this, please click here.")).clickEvent(ClickEvent.runCommand("/pvp overwrite " + whoWasHit.getName())));
            }else if(!(playerDataManager.readDataString(whoHit.getUniqueId(), "pvpStatus").equalsIgnoreCase("on"))){
                //set the event to cancelled
                event.setCancelled(true);
                //send message
                whoHit.sendMessage(MessageManager.messageGenerator("WARNING", "PvP",
                                Component.text("You can not fight this player because you have PvP disabled."))
                        .append(Component.text("\nTo enable PvP, please click here.")).clickEvent(ClickEvent.runCommand("/pvp")));
            }
        }
    }
}
