package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.util.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class MuteCommand extends BaseCommand {

    private final PlayerDataManager playerDataManager;
    private final DiscordBot discordBot;

    public MuteCommand(PlayerDataManager playerDataManager, DiscordBot discordBot) {
        this.playerDataManager = playerDataManager;
        this.discordBot = discordBot;
    }

    @CommandAlias("mute")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.moderation.mute")
    @Description("Permanently mute a user with a reason.")
    public void onMute(Player player, String[] args){

        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Mute", "please specify a player."));
        }else if(args.length == 1){
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            String reason = "You've been muted by a moderator.";
            String appealCode = "M_" + CodeGenerators.codeEight();

            int mutedAmount;

            if(playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1){
                mutedAmount = 0;
            }else{
                mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
            }
            mutedAmount++;

            playerDataManager.createData(target.getUniqueId(), "username", target.getName());
            playerDataManager.createData(target.getUniqueId(), "muted", true);
            playerDataManager.createData(target.getUniqueId(), "mutedBy", player.getName());
            playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
            playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
            playerDataManager.createData(target.getUniqueId(), "muteExpires", "never");
            playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

            if(target.isOnline()){
                Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You have been permanently muted by: " + player.getName() + " for: " + reason + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
            }
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Mute", "Permanently muted " + target.getName() + " for: " + reason));
            discordBot.embedBuilderMod(player.getName(), "New Mute", "Just muted: " + target.getName(), "Reason:" , reason,"Expires:", "Never", Color.RED);
        }else{
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
            String reason = String.join(" ", remainingArgs);
            String appealCode = "M_" + CodeGenerators.codeEight();

            int mutedAmount;

            if(playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1){
                mutedAmount = 0;
            }else{
                mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
            }
            mutedAmount++;

            playerDataManager.createData(target.getUniqueId(), "username", target.getName());
            playerDataManager.createData(target.getUniqueId(), "muted", true);
            playerDataManager.createData(target.getUniqueId(), "mutedBy", player.getName());
            playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
            playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
            playerDataManager.createData(target.getUniqueId(), "muteExpires", "never");
            playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

            if(target.isOnline()){
                Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Mute", "You have been permanently muted by: " + player.getName() + " for: " + reason + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
            }
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Mute", "Permanently muted: " + target.getName() + " for: " + reason));
            discordBot.embedBuilderMod(player.getName(), "New Mute", "Just muted: " + target.getName(), "Reason:" , reason,"Expires:", "Never", Color.RED);
        }
    }

    @CommandAlias("tempmute")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.moderation.mute")
    @Description("Temporarily mute a person with a reason")
    public void onTempmute(Player player, String[] args) {

        if (args.length < 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Tempmute", "please specify a player."));
            return;
        } else if (args.length == 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Tempmute", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
            return;
        } else if (args.length == 2) {
            String targetName = args[0];
            String date = args[1];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            String reason = "You've been muted by a moderator";
            String appealCode = "M_" + CodeGenerators.codeEight();

            boolean isValidDuration = CalculateDates.isValidDuration(date);
            if (!isValidDuration) {
                player.sendMessage(MessageManager.messageGenerator("ERROR", "Tempmute", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
                return;
            }
            Date unmuteDate = CalculateDates.calculateDate(date);

            int mutedAmount;

            if (playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1) {
                mutedAmount = 0;
            } else {
                mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
            }
            mutedAmount++;

            playerDataManager.createData(target.getUniqueId(), "username", target.getName());
            playerDataManager.createData(target.getUniqueId(), "muted", true);
            playerDataManager.createData(target.getUniqueId(), "mutedBy", player.getName());
            playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
            playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
            playerDataManager.createData(target.getUniqueId(), "muteExpires", String.valueOf(unmuteDate.getTime()));
            playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

            if (target.isOnline()) {
                Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Tempmute", "You have been temporarily muted by: " + player.getName() + " for: " + reason + "\nYou will be unmuted on: " + unmuteDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
            }
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Tempmute", "Temporarily muted " + target.getName() + " for: " + reason + "\nUntil: " + unmuteDate));
            discordBot.embedBuilderMod(player.getName(), "New Mute", "Just muted: " + target.getName(), "Reason:", reason, "Expires:", String.valueOf(unmuteDate), Color.RED);
            return;
        }
        String targetName = args[0];
        String date = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        String[] remainingArgs = Arrays.copyOfRange(args, 2, args.length);
        String reason = String.join(" ", remainingArgs);
        String appealCode = "M_" + CodeGenerators.codeEight();

        boolean isValidDuration = CalculateDates.isValidDuration(date);
        if (!isValidDuration) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Tempmute", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
            return;
        }
        Date unmuteDate = CalculateDates.calculateDate(date);

        int mutedAmount;

        if (playerDataManager.readDataInt(target.getUniqueId(), "timesMuted") == -1) {
            mutedAmount = 0;
        } else {
            mutedAmount = playerDataManager.readDataInt(target.getUniqueId(), "timesMuted");
        }
        mutedAmount++;

        playerDataManager.createData(target.getUniqueId(), "username", target.getName());
        playerDataManager.createData(target.getUniqueId(), "muted", true);
        playerDataManager.createData(target.getUniqueId(), "mutedBy", player.getName());
        playerDataManager.createData(target.getUniqueId(), "mutedReason", reason);
        playerDataManager.createData(target.getUniqueId(), "timesMuted", mutedAmount);
        playerDataManager.createData(target.getUniqueId(), "muteExpires", String.valueOf(unmuteDate.getTime()));
        playerDataManager.createData(target.getUniqueId(), "mutedAppealCode", appealCode);

        if (target.isOnline()) {
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Tempmute", "You have been temporarily muted by: " + player.getName() + " for: " + reason + "\nYou will be unmuted on: " + unmuteDate + "\nYour unmute appeal code is: " + appealCode + " (click to copy)").clickEvent(ClickEvent.copyToClipboard(appealCode)));
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Tempmute", "Temporarily muted " + target.getName() + " for: " + reason + "\nUntil: " + unmuteDate));
        discordBot.embedBuilderMod(player.getName(), "New Mute", "Just muted: " + target.getName(), "Reason:", reason, "Expires:", String.valueOf(unmuteDate), Color.RED);
    }

    @CommandAlias("unmute")
    @CommandCompletion("@players")
    @Description("Unmute a player.")
    @CommandPermission("swagsmp.moderation.mute")
    public void onUnmute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Unmute", "please specify a player."));
            return;
        }
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        playerDataManager.updateData(target.getUniqueId(), "muted", false);
        playerDataManager.deleteData(target.getUniqueId(), "mutedBy");
        playerDataManager.deleteData(target.getUniqueId(), "mutedReason");
        playerDataManager.deleteData(target.getUniqueId(), "muteExpires");
        playerDataManager.deleteData(target.getUniqueId(), "mutedAppealCode");

        if (target.isOnline()) {
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Unmute", "You have been unmuted."));
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Unmute", "Successfully unmuted: " + target.getName()));
        discordBot.embedBuilderMod(player.getName(), "New Unmute", "Just unmuted: " + target.getName(), Color.RED);
    }

    public void onUnmute(String args) {

        String targetName = args;
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        playerDataManager.updateData(target.getUniqueId(), "muted", false);
        playerDataManager.deleteData(target.getUniqueId(), "mutedBy");
        playerDataManager.deleteData(target.getUniqueId(), "mutedReason");
        playerDataManager.deleteData(target.getUniqueId(), "muteExpires");
        playerDataManager.deleteData(target.getUniqueId(), "mutedAppealCode");

        if (target.isOnline()) {
            Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());
            targetPlayer.sendMessage(MessageManager.messageGenerator("WARNING", "Unmute", "You have been unmuted."));
        }
        discordBot.embedBuilderMod("DISCORD | CONSOLE", "New Unmute", "Just unmuted: " + target.getName(), Color.RED);
    }

    @CommandAlias("mutelist")
    @CommandPermission("swagsmp.moderation.mutelist")
    @Description("See the list of people who are muted.")
    public void onMuteList(Player player, String[] args){

        String playerDataFolderPath = Bukkit.getPluginsFolder().getAbsolutePath() + "/SwagSMPCore/playerData";

        int pageSize = 45; // Number of bans per page
        int pageNumber = 0; // Start with the first page

        if (args.length > 0) {
            try {
                int requestedPage = Integer.parseInt(args[0]); // Convert user's input to 0-based index
                if (requestedPage >= 0) { // Ensure the requested page is non-negative
                    pageNumber = requestedPage;
                }
            } catch (NumberFormatException ignored) {
                // Invalid page number, default to page 1
            }
        }

        try {
            List<String> mutedPlayers = getMutedPlayers(playerDataFolderPath);
            int startIndex = pageNumber * pageSize;
            int endIndex = Math.min(startIndex + pageSize, mutedPlayers.size());

            Inventory gui = Bukkit.createInventory(player, 54, Component.text("Muted Players (Page: " + (pageNumber + 1) + ")"));
            gui.setItem(49, GuiItems.closeGui());

            if (endIndex < mutedPlayers.size()) {
                ItemStack nextPageArrow = new ItemStack(Material.ARROW);
                ItemMeta nextPageMeta = nextPageArrow.getItemMeta();
                nextPageMeta.displayName(Component.text("Next Page"));
                nextPageMeta.setCustomModelData(0);
                nextPageArrow.setItemMeta(nextPageMeta);
                gui.setItem(53, nextPageArrow);
            }

            if(pageNumber != 0){
                ItemStack previousPageArrow = new ItemStack(Material.ARROW);
                ItemMeta previousPageMeta = previousPageArrow.getItemMeta();
                previousPageMeta.displayName(Component.text("Previous Page"));
                previousPageMeta.setCustomModelData(1);
                previousPageArrow.setItemMeta(previousPageMeta);
                gui.setItem(45, previousPageArrow);
            }

            for (int i = startIndex; i < endIndex; i++) {
                String player1 = mutedPlayers.get(i);
                OfflinePlayer target = Bukkit.getOfflinePlayer(player1);

                String expiration = playerDataManager.readDataString(target.getUniqueId(), "muteExpires");
                String reason = playerDataManager.readDataString(target.getUniqueId(), "mutedReason");
                String mutedBy = playerDataManager.readDataString(target.getUniqueId(), "mutedBy");
                String appealCode = playerDataManager.readDataString(target.getUniqueId(), "mutedAppealCode");

                //add all the details of the ban to the lore of the skull
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Muted By: " + mutedBy));
                lore.add(Component.text("Reason: " + reason));

                if (expiration != null) {
                    if (expiration.equalsIgnoreCase("never")) {
                        lore.add(Component.text("Expires: Never"));
                    } else if(new Date().after(new Date(Long.parseLong(playerDataManager.readDataString(target.getUniqueId(), "muteExpires"))))) {
                        lore.add(Component.text("Expires: Expired (Mute is pending to be removed)"));
                    } else {
                        long timestamp = Long.parseLong(playerDataManager.readDataString(target.getUniqueId(), "muteExpires"));
                        Date expirationDate = new Date(timestamp);
                        lore.add(Component.text("Expires: " + expirationDate));
                    }
                }


                lore.add(Component.text("Appeal code: "+ appealCode));

                ItemStack skullPlayer = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) skullPlayer.getItemMeta();
                skullMeta.setOwningPlayer(target);
                skullMeta.displayName(Component.text(target.getName()));
                skullMeta.lore(lore);
                skullMeta.setCustomModelData(0);
                skullPlayer.setItemMeta(skullMeta);
                gui.addItem(skullPlayer);
            }
            player.openInventory(gui);

            int finalNextPage = pageNumber;
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onInventoryClick(InventoryClickEvent event) {
                    if (event.getWhoClicked() == player && event.getInventory() == gui) {
                        event.setCancelled(true); // Cancel the event to prevent moving any items in the GUI
                        ItemStack clickedItem = event.getCurrentItem();
                        if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getCustomModelData() ==  0) {
                            event.setCancelled(true);
                            ItemMeta itemMeta = clickedItem.getItemMeta();
                            //get the item name
                            String itemName = itemMeta.getDisplayName();
                            //remove function
                            onUnmute(player, new String[]{itemName});
                            onMuteList(player, new String[]{String.valueOf(finalNextPage)});
                        }else if(clickedItem != null && clickedItem.getType() == Material.BARRIER && clickedItem.getItemMeta().getCustomModelData() ==  1){
                            event.setCancelled(true);
                            gui.close();
                        }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 0){
                            event.setCancelled(true);
                            int nextPageNumber = finalNextPage + 1;
                            onMuteList(player, new String[]{String.valueOf(nextPageNumber)});
                        }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 1){
                            event.setCancelled(true);
                            int previousPageNumber = finalNextPage - 1;
                            onMuteList(player, new String[]{String.valueOf(previousPageNumber)});
                        }
                    }
                }
            }, SwagSMPCore.getPlugin());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getMutedPlayers(String directoryPath) throws IOException {
        Path dirPath = Paths.get(directoryPath);
        List<String> mutedPlayers = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.yml")) {
            for (Path entry : stream) {
                // Read the file content and check if the player is muted
                if (isPlayerMuted(entry)) {
                    String username = getPlayerUsername(entry);
                    mutedPlayers.add(username);
                }
            }
        }

        return mutedPlayers;
    }

    public static boolean isPlayerMuted(Path playerDataFile) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(Files.newInputStream(playerDataFile));
            return data != null && data.containsKey("muted") && (boolean) data.get("muted");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPlayerUsername(Path playerDataFile) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(Files.newInputStream(playerDataFile));
            if (data != null && data.containsKey("username")) {
                return (String) data.get("username");
            } else {
                return null; // Or handle the case when the field is missing
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
