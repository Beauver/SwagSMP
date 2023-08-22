package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class KickCommand extends BaseCommand {

    @CommandAlias("kick")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.moderation.kick")
    @Description("kick the specified player, with reason")
    public static void onKick(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        //splits the playerName from the joined argument list
        String target = splitArgs[0];
        //gets the player from the target name
        Player targetPlayer = Bukkit.getPlayer(target);

        String reason;

        //various target checks
        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Kick", "please specify a player."));
            return;
        }else if(targetPlayer == null){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Kick", Bukkit.getOfflinePlayer(target).getName() + " is currently offline."));
            return;
        }else if(args.length == 1){
            reason = "You were kicked by a moderator.";
            KickHandler.kickPlayer(targetPlayer, reason, player.getName());
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Kick", "Kicked " + targetPlayer.getName() + " for: " + reason));
            return;
        }

        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        reason = String.join(" ", remainingArgs);

        KickHandler.kickPlayer(targetPlayer, reason, player.getName());
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Kick", "Kicked " + targetPlayer.getName() + " for: " + reason));
    }
}
