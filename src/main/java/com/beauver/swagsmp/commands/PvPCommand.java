package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import org.bukkit.entity.Player;

public class PvPCommand extends BaseCommand {
    private final PlayerDataManager playerDataManager;

    public PvPCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Default
    @CommandAlias("pvp")
    @CommandPermission("swagsmp.pvp")
    @Description("Allows you to toggle between enabling or disabling pvp")
    public void onPvPCommand(Player player){
        try{
            //read for the pvpStatus variable in the playerData
            String pvpStatus = playerDataManager.readDataString(player.getUniqueId(), "pvpStatus");
            if(pvpStatus == null){
                //if it doesn't exist. (IT SHOULD!)
                //Create new data and set it to "on"
                playerDataManager.createData(player.getUniqueId(), "pvpStatus", "on");
                //send message
                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Your PvP status has been toggled on."));
            }else{
                //if it does exist
                //if the pvpStatus is currently on
                if(pvpStatus.equalsIgnoreCase("on")){
                    //set it to off
                    playerDataManager.updateData(player.getUniqueId(), "pvpStatus", "off");
                    //send message that it's off
                    player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Your PvP status has been toggled " + pvpStatus + "."));

                    //if the data is set to off
                }else if(pvpStatus.equalsIgnoreCase("off")){
                    //set it to on
                    playerDataManager.updateData(player.getUniqueId(), "pvpStatus", "on");
                    //send message
                    player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Your PvP status has been toggled " + pvpStatus + "."));
                }
            }
        } catch (Exception e) {
            //if a random error occurs, send error message
            player.sendMessage(MessageManager.messageGenerator("ERROR", "An unknown error occurred, please try again later."));
            throw new RuntimeException(e);
        }
    }
}
