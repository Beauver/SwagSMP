package com.beauver.swagsmp.commands.moderation;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.util.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
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

import java.util.*;

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
        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);

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

            //adding the target to the banList
            banList.addBan(targetPlayer.getPlayerProfile(), reason, (Date) null, player.getName());

            //if target is online, get their online player and kick them
            if(targetPlayer.isOnline()){
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

        banList.addBan(targetPlayer.getPlayerProfile(), reason, (Date) null, player.getName());

        if(targetPlayer.isOnline()){
            Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
            KickHandler.kickBanPlayer(targetPlayerOnline, reason, player.getName(), appealCode);
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Ban", "Banned " + targetPlayer.getName() + " for: " + reason));
    }

    @CommandAlias("tempban")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.moderation.ban")
    @Description("Temporarly ban a player from the server.")
    public void onTempBan(Player player, String[] args) {

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        String reason;
        //getting server banlist
        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);

        //various target checks
        if (args.length < 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Ban", "please specify a player."));
            return;
        } else if (args.length == 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Ban", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
            return;
        } else if(args.length == 2){
            //splits the playerName from the joined argument list
            String target = splitArgs[0];
            String date = splitArgs[1];
            boolean isValidDuration = CalculateDates.isValidDuration(date);

            if (!isValidDuration) {
                player.sendMessage(MessageManager.messageGenerator("ERROR", "Ban", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
                return;
            }

            Date unbanDate = CalculateDates.calculateDate(date);
            //gets the player from the target name
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

            reason = "You were banned by a moderator.";
            String appealCode = "B_" + CodeGenerators.codeEight();

            int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

            if (playerBannedAmount == -1) {
                playerBannedAmount = 0;
            }
            playerBannedAmount++;

            playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
            playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
            playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
            playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", player.getName());
            playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);
            playerDataManager.createData(targetPlayer.getUniqueId(), "banExpires", String.valueOf(unbanDate));

            banList.addBan(targetPlayer.getPlayerProfile(), reason, unbanDate, player.getName());

            if (targetPlayer.isOnline()) {
                Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
                KickHandler.kickBanPlayer(targetPlayerOnline, reason, player.getName(), appealCode, unbanDate);

                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Ban", "Temporarily banned " + targetPlayer.getName() + " for: " + reason));
                return;
            }
        }

        //splits the playerName from the joined argument list
        String target = splitArgs[0];
        String date = splitArgs[1];

        boolean isValidDuration = CalculateDates.isValidDuration(date);
        if (!isValidDuration) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Ban", "Please specify a valid duration. \nExample: 1d = 1 day, 3M = 3 months, 1m = 1 minute, etc."));
            return;
        }

        Date unbanDate = CalculateDates.calculateDate(date);
        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        String[] remainingArgs = Arrays.copyOfRange(args, 2, args.length);

        reason = String.join(" ", remainingArgs);

        String appealCode = "B_" + CodeGenerators.codeEight();

        int playerBannedAmount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "bannedAmount");

        if (playerBannedAmount == -1) {
            playerBannedAmount = 0;
        }
        playerBannedAmount++;

        playerDataManager.createData(targetPlayer.getUniqueId(), "banned", true);
        playerDataManager.createData(targetPlayer.getUniqueId(), "appealCode", appealCode);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banReason", reason);
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedBy", player.getName());
        playerDataManager.createData(targetPlayer.getUniqueId(), "bannedAmount", playerBannedAmount);
        playerDataManager.createData(targetPlayer.getUniqueId(), "banExpires", String.valueOf(unbanDate));

        banList.addBan(targetPlayer.getPlayerProfile(), reason, unbanDate, player.getName());

        if (targetPlayer.isOnline()) {
            Player targetPlayerOnline = Bukkit.getPlayer(targetPlayer.getUniqueId());
            KickHandler.kickBanPlayer(targetPlayerOnline, reason, player.getName(), appealCode, unbanDate);
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Ban", "Temporarily banned " + targetPlayer.getName() + " for: " + reason));
    }

    @CommandAlias("unban")
    @CommandPermission("swagsmp.moderation.ban")
    @Description("Unban a player that has previously been banned.")
    public void onUnban(Player player, String[] args){
        Plugin plugin = SwagSMPCore.getPlugin();

        if(args.length < 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Unban", "please specify a player."));
            return;
        }

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        String target = splitArgs[0];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        if(targetPlayer.isBanned()) {
            BanList<PlayerProfile> banList = plugin.getServer().getBanList(BanList.Type.PROFILE);
            BanEntry<PlayerProfile> banEntry = banList.getBanEntry(targetPlayer.getPlayerProfile());
            banEntry.remove();
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banReason");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "bannedBy");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banExpires");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "appealCode");
            playerDataManager.updateData(targetPlayer.getUniqueId(), "banned", false);
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Unban", targetPlayer.getName() + " is now unbanned."));
        }else{
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Unban", targetPlayer.getName() + " is currently not banned."));
        }
    }

    public void onUnban(Player player, String target){
        Plugin plugin = SwagSMPCore.getPlugin();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        if(targetPlayer.isBanned()) {
            BanList<PlayerProfile> banList = plugin.getServer().getBanList(BanList.Type.PROFILE);
            BanEntry<PlayerProfile> banEntry = banList.getBanEntry(targetPlayer.getPlayerProfile());
            banEntry.remove();
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banReason");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "bannedBy");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "banExpires");
            playerDataManager.deleteData(targetPlayer.getUniqueId(), "appealCode");
            playerDataManager.updateData(targetPlayer.getUniqueId(), "banned", false);
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Unban", targetPlayer.getName() + " is now unbanned."));
        }
    }

    @CommandAlias("banlist")
    @CommandPermission("swagsmp.moderation.banlist")
    @Description("See the ban list of everyone who's currently banned.")
    public void onBanlist(Player player, String[] args){

        Plugin plugin = SwagSMPCore.getPlugin();
        BanList<PlayerProfile> banList = plugin.getServer().getBanList(BanList.Type.PROFILE);

        player.sendMessage(MessageManager.messageGenerator("WARNING", "banlist", "Loading banned players..."));

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

        // Create a sublist of bans for the current page
        ArrayList bans = new ArrayList<>(banList.getBanEntries());
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, bans.size());

        //creating the GUI
        Inventory gui = Bukkit.createInventory(player, 54, Component.text("Banned Players (Page: " + (pageNumber + 1) + ")" ));
        gui.setItem(49, GuiItems.closeGui());

        if (endIndex < bans.size()) {
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
            BanEntry<PlayerProfile> banEntry = (BanEntry<PlayerProfile>) bans.get(i);
            Date expiration = banEntry.getExpiration();
            String reason = banEntry.getReason();
            String bannedBy = banEntry.getSource();

            //DEPRECATED METHOD
            String bannedPlayer = banEntry.getTarget();
            OfflinePlayer target = Bukkit.getOfflinePlayer(bannedPlayer);
            String appealCode = playerDataManager.readDataString(target.getUniqueId(), "appealCode");

            //add all the details of the ban to the lore of the skull
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Banned By: " + bannedBy));
            lore.add(Component.text("Reason: " + reason));
            if (expiration != null) {
                lore.add(Component.text("Expires: " + expiration.toString()));
            } else {
                lore.add(Component.text("Expires: Never"));
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
                        ItemMeta itemMeta = clickedItem.getItemMeta();
                        //get the item name
                        String itemName = itemMeta.getDisplayName();
                        //remove function
                        onUnban(player, itemName);
                    }else if(clickedItem != null && clickedItem.getType() == Material.BARRIER && clickedItem.getItemMeta().getCustomModelData() ==  1){
                        gui.close();
                    }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 0){
                        int nextPageNumber = finalNextPage + 1;
                        onBanlist(player, new String[]{String.valueOf(nextPageNumber)});
                    }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 1){
                        int previousPageNumber = finalNextPage - 1;
                        onBanlist(player, new String[]{String.valueOf(previousPageNumber)});
                    }
                }
            }
        }, SwagSMPCore.getPlugin());
    }
}
