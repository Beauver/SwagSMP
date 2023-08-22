package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.CodeGenerators;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class BanCommand extends BaseCommand {

    private final PlayerDataManager playerDataManager;

    public BanCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @CommandAlias("ban")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.moderation.ban")
    @Description("ban a player from the server.")
    public void onBan(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");

        String reason;
        //getting server banlist
        BanList<Player> banList = Bukkit.getBanList(BanList.Type.PROFILE);

        //various target checks
        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Ban", "please specify a player."));
            return;
        }else if(args.length == 1){
            //splits the playerName from the joined argument list
            String target = splitArgs[0];
            //gets the player from the target name
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

            reason = "You were banned by a moderator.";
            String appealCode = "B_" + CodeGenerators.codeEight();

            int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

            if(playerBannedAmount == -1){
                playerBannedAmount = 0;
            }
            playerBannedAmount++;

            playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
            playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
            playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
            playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", player.getName());
            playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);

//            banList.addBan(targetPlayer.getPlayerProfile(), reason, (Date) null, player.getName());

            if(player.isOnline()){
                Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
                KickHandler.kickBanPlayer(targetPlayerOnline, reason, player.getName(), appealCode);
            }
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Ban", "Banned " + targetPlayer.getName() + " for: " + reason));
            return;
        }

        //splits the playerName from the joined argument list
        String target = splitArgs[0];
        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);

        reason = String.join(" ", remainingArgs);;
        String appealCode = "B_" + CodeGenerators.codeEight();

        int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

        if(playerBannedAmount == -1){
            playerBannedAmount = 0;
        }
        playerBannedAmount++;

        playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
        playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", player.getName());
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);

//        banList.addBan((Player) targetPlayer, reason, (Date) null, player.getName());

        if(player.isOnline()){
            Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
            KickHandler.kickBanPlayer(targetPlayerOnline, reason, player.getName(), appealCode);
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Ban", "Banned " + targetPlayer.getName() + " for: " + reason));
    }
}
