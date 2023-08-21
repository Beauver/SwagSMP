package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("pvp")
@Description("Allows you to toggle between enabling or disabling pvp")
public class PvPCommand extends BaseCommand {
    private final PlayerDataManager playerDataManager;

    public PvPCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Default
    @CommandPermission("swagsmp.pvp")
    public void onPvPCommand(Player player){
        try{
            //read for the pvpStatus variable in the playerData
            String pvpStatus = playerDataManager.readDataString(player.getUniqueId(), "pvpStatus");
            if(pvpStatus == null){
                //if it doesn't exist. (IT SHOULD!)
                //Create new data and set it to "on"
                playerDataManager.createData(player.getUniqueId(), "pvpStatus", "on");
                //send message
                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "PvP", "Your PvP status has been toggled on."));
            }else{
                //if it does exist
                //if the pvpStatus is currently on
                if(pvpStatus.equalsIgnoreCase("on")){
                    //set it to off
                    playerDataManager.updateData(player.getUniqueId(), "pvpStatus", "off");
                    //send message that it's off
                    player.sendMessage(MessageManager.messageGenerator("SUCCESS", "PvP", "Your PvP status has been toggled off."));

                    //if the data is set to off
                }else if(pvpStatus.equalsIgnoreCase("off")){
                    //set it to on
                    playerDataManager.updateData(player.getUniqueId(), "pvpStatus", "on");
                    //send message
                    player.sendMessage(MessageManager.messageGenerator("SUCCESS", "PvP", "Your PvP status has been toggled on."));
                }
            }
        } catch (Exception e) {
            //if a random error occurs, send error message
            player.sendMessage(MessageManager.messageGenerator("ERROR", "ERROR", "An unknown error occurred, please try again later."));
            throw new RuntimeException(e);
        }
    }

    @Subcommand("overwrite")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.pvp.overwrite")
    @Description("Overwrites yours and the specified person to enable PvP")
    public void onOverwrite(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        //splits the playerName from the joined argument list
        String target = splitArgs[0];
        //gets the player from the target name
        Player targetPlayer = Bukkit.getPlayer(target);

        //if the target does not exist, say they're offline.
        if(targetPlayer == null){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "PvP OVERWRITE", Bukkit.getOfflinePlayer(target).getName() + "is currently offline."));
            return;
        }

        if(targetPlayer.getUniqueId().equals(player.getUniqueId())){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "PvP OVERWRITE", "You can not overwrite your own PvP status, click here to enable/disable your PvP").clickEvent(ClickEvent.runCommand("/pvp")));
            return;
        }

        //change the data to 'on'
        playerDataManager.updateData(player.getUniqueId(), "pvpStatus", "on");
        playerDataManager.updateData(targetPlayer.getUniqueId(), "pvpStatus", "on");

        //send both players a message that the PvP rule has been updated
        player.sendMessage(MessageManager.messageGenerator("WARNING", "PvP OVERWRITE", "You have overwritten " + Bukkit.getOfflinePlayer(target).getName() + "'s PvP rules."));

        targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "PvP OVERWRITE",
                Component.text(player.getName() + " has enabled your PvP."))
                .append(Component.text("\nIf they are abusing this, please contact a moderator imminently!")).color(TextColor.fromHexString("#f09c0b")));

        //Send every admin with the permission "swagsmp.pvp.overwrite.spy" a message
        for(Player player1 : Bukkit.getOnlinePlayers()){
            if(player1.hasPermission("swagsmp.pvp.overwrite.spy")){
                player1.sendMessage(MessageManager.messageGenerator("WARNING", "PvP OVERWRITE", player.getName() + " has just overwritten " + Bukkit.getOfflinePlayer(target).getName() + "'s PvP rules."));
            }
        }

    }
}
