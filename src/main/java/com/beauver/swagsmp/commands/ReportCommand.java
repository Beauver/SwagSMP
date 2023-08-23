package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.util.GuiItems;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("report")
@Description("A way to report other uses if they're breaking the rules")
public class ReportCommand extends BaseCommand {

    private final PlayerDataManager playerDataManager;

    public ReportCommand(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Default
    @CommandCompletion("@players")
    public void onReport(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        //splits the playerName from the joined argument list
        String target = splitArgs[0];
        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);


        //various target checks
        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report", "please specify a player."));
            return;
        }else if(targetPlayer.getUniqueId().equals(player.getUniqueId())){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report", "You can not report yourself."));
            return;
        }else if(args.length == 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report", "Please add a reasoning for your report."));
            return;
        }

        //join the string together to get the reason of the report
        String[] remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        String reportReason = String.join(" ", remainingArgs);

        //get the amount of times the target has been reported
        int reportNum = playerDataManager.readDataInt(targetPlayer.getUniqueId(),"reportAmount");
        //if the reportNum returns a -1 value (PlayerDataManager says that the file or dataType does not exist)
        if(reportNum == -1){
            reportNum = 0;
            playerDataManager.createData(targetPlayer.getUniqueId(), "reportAmount", reportNum);
        }
        reportNum++;
        //get the targets report list
        List<String> reportList = (playerDataManager.readDataListString(targetPlayer.getUniqueId(), "reportList"));
        //if the list does not exist
        if(reportList == null){
            //make new one
            ArrayList<String> reportList1 = new ArrayList<>();
            //add the ID + reason
            reportList1.add(reportNum + reportReason);
            //save it
            playerDataManager.createDataListString(targetPlayer.getUniqueId(), "reportList", reportList1);
            playerDataManager.updateData(targetPlayer.getUniqueId(), "reportAmount", reportNum);
        }else{
            //if it does exist - add new report to the report list with the ID + reason
            reportList.add(reportNum + " " + reportReason);
            //save the list
            playerDataManager.updateDataArrayString(targetPlayer.getUniqueId(), "reportList", reportList);
            playerDataManager.updateData(targetPlayer.getUniqueId(), "reportAmount", reportNum);
        }
        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Report", "Successfully reported " + targetPlayer.getName() + " for: " + reportReason));


        for(Player player1 : Bukkit.getOnlinePlayers()){
            if(player1.hasPermission("swagsmp.report.list")){
                player1.sendMessage(MessageManager.messageGenerator("WARNING", "Report",
                        Component.text(player.getName() + " just reported " + targetPlayer.getName() + " for:"))
                        .append(Component.text("\n" + reportReason)).color(TextColor.fromHexString("#f09c0b")));
            }
        }
    }

    @Subcommand("list")
    @CommandCompletion("@players")
    @CommandPermission("swagsmp.report.list")
    @Description("See the reports of another player")
    public void onReportList(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        //splits the playerName from the joined argument list
        String target = splitArgs[0];

        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report List", "please specify a player."));
            return;
        }
        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        
        List<String> reportList = playerDataManager.readDataListString(targetPlayer.getUniqueId(), "reportList");
        int reportCount = playerDataManager.readDataInt(targetPlayer.getUniqueId(), "reportAmount");

        //creating dynamic GUI Size
        int guiSize = 9 * ((reportList.size() + 2) / 9 + 1);
        if(guiSize < 9){
            guiSize = 9;
        }
        //creating the GUI
        Inventory gui = Bukkit.createInventory(player, guiSize, Component.text(targetPlayer.getName() + "'s Reports"));

        //creating itemstack which shows amount of total reports
        ItemStack reportCountMenuStack = new ItemStack(Material.PAPER);
        ItemMeta reportCountMenuMeta = reportCountMenuStack.getItemMeta();
        reportCountMenuMeta.displayName(Component.text(reportCount + " total reports"));
        reportCountMenuMeta.setCustomModelData(1);
        reportCountMenuStack.setItemMeta(reportCountMenuMeta);

        //setting the items in the GUI slots
        gui.setItem(0, GuiItems.playerSkull(targetPlayer));
        gui.setItem(1, reportCountMenuStack);
        gui.setItem(8, GuiItems.closeGui());

        for(String reports : reportList){

            ItemStack reportItem = new ItemStack(Material.FILLED_MAP);
            ItemMeta meta = reportItem.getItemMeta();
            meta.displayName(Component.text(reports));
            meta.setCustomModelData(2);
            reportItem.setItemMeta(meta);
            gui.addItem(reportItem);
        }
        player.openInventory(gui);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() == player && event.getInventory() == gui) {
                    event.setCancelled(true); // Cancel the event to prevent moving any items in the GUI
                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem != null && clickedItem.getType() == Material.FILLED_MAP && clickedItem.getItemMeta().getCustomModelData() ==  2) {
                        ItemMeta itemMeta = clickedItem.getItemMeta();
                        //get the item name
                        String itemName = itemMeta.getDisplayName();
                        //get the first character
                        char firstChar = itemName.charAt(0);
                        //make the first character into a string
                        String firstCharString = String.valueOf(firstChar);
                        //make it into an int
                        int firstCharInt = Integer.parseInt(firstCharString);
                        //remove function
                        onReportRemoveGUI(player, targetPlayer, firstCharInt);
                    }else if(clickedItem != null && clickedItem.getType() == Material.BARRIER && clickedItem.getItemMeta().getCustomModelData() ==  1){
                        gui.close();
                    }
                }
            }
        }, SwagSMPCore.getPlugin());
    }

    @Subcommand("remove")
    @CommandPermission("swagsmp.report.remove")
    @Description("Remove a players report if it's inaccurate")
    public void onReportRemove(Player player, String[] args){

        String[] splitArgs = String.join(" ", args).split("(?<=\\S)\\s+(?=\\S)");
        //splits the playerName from the joined argument list
        String target = splitArgs[0];

        //multiple target checks
        if(args.length < 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report Removal", "please specify a player."));
            return;
        }if(args.length == 1){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report Removal", "please specify a report ID."));
            return;
        }

        //gets the player from the target name
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
        //get the reportID
        int reportId = Integer.parseInt(splitArgs[1]);

        //get the reportlist
        List<String> reportList = playerDataManager.readDataListString(targetPlayer.getUniqueId(), "reportList");

        int firstCharInt;

        if(reportList.isEmpty()){
           firstCharInt = 0;
        }else{
            String lastString = reportList.get(reportList.size() - 1);
            char firstCharacter = lastString.charAt(0);
            String firstCharString = String.valueOf(firstCharacter);
            firstCharInt = Integer.parseInt(firstCharString);
        }

        if(reportId > firstCharInt){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report Removal", "Please check if your report ID is valid."));
        }else{
            //remove report from reportList if it starts with the reportID
            reportList.removeIf(report -> report.startsWith(String.valueOf(reportId)));
            playerDataManager.updateDataArrayString(targetPlayer.getUniqueId(), "reportList", reportList);
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Report Removal", "Successfully removed " + targetPlayer.getName() + "'s report."));
        }
    }

    public void onReportRemoveGUI(Player player, OfflinePlayer targetPlayer, int reportId){
        //get the reportlist
        List<String> reportList = playerDataManager.readDataListString(targetPlayer.getUniqueId(), "reportList");

        int firstCharInt;

        if(reportList.isEmpty()){
            firstCharInt = 0;
        }else{
            String lastString = reportList.get(reportList.size() - 1);
            char firstCharacter = lastString.charAt(0);
            String firstCharString = String.valueOf(firstCharacter);
            firstCharInt = Integer.parseInt(firstCharString);
        }

        //if the reportID is more than the first character (this is always an ID) of the last string in the array; send error
        if(reportId > firstCharInt){
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Report Removal", "Please check if your report ID is valid."));
        }else{
            //remove report from reportList if it starts with the reportID
            reportList.removeIf(report -> report.startsWith(String.valueOf(reportId)));
            playerDataManager.updateDataArrayString(targetPlayer.getUniqueId(), "reportList", reportList);
            player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Report Removal", "Successfully removed " + targetPlayer.getName() + "'s report."));
        }
    }
}
