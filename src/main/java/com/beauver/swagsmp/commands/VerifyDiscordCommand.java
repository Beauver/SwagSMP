package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.CodeGenerators;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyDiscordCommand extends BaseCommand {

    private final PlayerDataManager playerDataManager;
    public VerifyDiscordCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @CommandAlias("link")
    @CommandPermission("swagsmp.link")
    @Description("Link your minecraft to your discord.")
    public void onVerify(Player player){
        //getting the discord name from the player file
        String discordName = playerDataManager.readDataString(player.getUniqueId(), "discordAccountName");
        //checking if the discordId exists, if it does, then say you're already linked
        if(playerDataManager.readDataString(player.getUniqueId(), "discordAccountId") != null){
            player.sendMessage(MessageManager.messageGenerator("WARNING", "Discord Link", Component.text("Your discord account is already linked to: " + discordName))
                    .append(Component.text("\nPlease click here if you wish to unlink.").clickEvent(ClickEvent.runCommand("/unlink")).color(TextColor.fromHexString("#f09c0b"))));
            return;
        }

        //generate link code
        String linkCode = "D_" + CodeGenerators.codeTwelve();

        if(!(SwagSMPCore.getPlugin().playerLinkCodes.containsKey(player.getName()))){
            SwagSMPCore.getPlugin().playerLinkCodes.put(player.getName(), linkCode);
        }else{
            linkCode = SwagSMPCore.getPlugin().playerLinkCodes.put(player.getName(), linkCode);
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Discord Link", "Your code is: " + linkCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard("!link " + linkCode)));
    }

    @CommandAlias("unlink")
    @CommandPermission("swagsmp.unlink")
    @Description("Link your minecraft to your discord.")
    public void onUnlink(Player player){

        if(playerDataManager.readDataString(player.getUniqueId(), "discordAccountId") == null){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Discord Link", "You are currently not linked to a discord account."));
            return;
        }

        playerDataManager.deleteData(player.getUniqueId(), "discordAccountId");
        playerDataManager.deleteData(player.getUniqueId(), "discordAccountName");
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Discord Link", "Your discord account has been unlinked."));
    }

    public void processLinkCommand(MessageReceivedEvent event, String code) {
        //for every entry in the HashMap
        for (Map.Entry<String, String> entry : SwagSMPCore.getPlugin().playerLinkCodes.entrySet()) {
            //get the username and code from the HashMap
            String storedUsername = entry.getKey();
            String storedCode = entry.getValue();

            if (code.equals(storedCode)) {
                //add the discord id to your mc playerdata
                playerDataManager.createData(Bukkit.getOfflinePlayer(storedUsername).getUniqueId(), "discordAccountId", event.getAuthor().getId());
                playerDataManager.createData(Bukkit.getOfflinePlayer(storedUsername).getUniqueId(), "discordAccountName", event.getAuthor().getGlobalName());

                //send message if player exists
                Objects.requireNonNull(Bukkit.getPlayer(storedUsername)).sendMessage(MessageManager.messageGenerator("SUCCESS", "Discord Link", "Your account is now linked to the discord user: " + event.getAuthor().getGlobalName()));
                event.getMessage().delete().queue();
                SwagSMPCore.getPlugin().playerLinkCodes.remove(storedUsername);

                try{
                    String discordUserName = event.getAuthor().getGlobalName();
                    Objects.requireNonNull(event.getMember()).modifyNickname(discordUserName + " [" + storedUsername + "]");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                break; // Stop the loop once a match is found
            }
        }
    }
}
