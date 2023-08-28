package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.enums.ItemModels;
import com.beauver.swagsmp.util.GuiItems;
import com.beauver.swagsmp.util.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChangeModelCommand extends BaseCommand {

    @CommandAlias("model")
    @CommandPermission("swagsmp.changeitemmodel")
    @Description("Change the custom model data for the current item you're holding")
    public void changeItemModel(Player player, String[] args){

        if (args.length != 1) {
            player.sendMessage(MessageManager.messageGenerator("ERROR", "Change Model", "Please specify a correct custom model.\nTo see the list of custom models, click here or write: /modellist.").clickEvent(ClickEvent.runCommand("/modellist")));
            return;
        }

        String itemName = args[0].toUpperCase();

        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material heldMaterial = currentItem.getType();

        for (ItemModels customItem : ItemModels.values()) {

            //getting the swagCoins
            ItemStack swagCoins = new ItemStack(Material.PAPER, customItem.getSwagCoinCost());
            ItemMeta swagCoinsMeta = swagCoins.getItemMeta();
            swagCoinsMeta.setCustomModelData(100);
            swagCoins.setItemMeta(swagCoinsMeta);

            if (customItem.isValidMaterial(heldMaterial) && customItem.name().equals(itemName)){
                if(customItem.getTransformable()){
                    if(player.getInventory().containsAtLeast(swagCoins, customItem.getSwagCoinCost())){
                        ItemMeta currentItemMeta = currentItem.getItemMeta();
                        if (currentItemMeta == null) {
                            currentItemMeta = Bukkit.getItemFactory().getItemMeta(currentItem.getType());
                        }
                        currentItemMeta.setCustomModelData(customItem.getCustomModelDataId());
                        currentItem.setItemMeta(currentItemMeta);

                        player.getInventory().removeItem(swagCoins);

                        player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Change Model", "Successfully changed your item model to: " + customItem.getTextureName()));
                    }else{
                        player.sendMessage(MessageManager.messageGenerator("ERROR", "Change Model", "You do not have enough swag coins in your inventory.\nYou need at least: " + customItem.getSwagCoinCost() + " swag coins"));
                    }
                    return;
                }else{
                    player.sendMessage(MessageManager.messageGenerator("ERROR", "Change Model", "This item is unobtainable via this command."));
                    return;
                }
            }
        }
        player.sendMessage(MessageManager.messageGenerator("ERROR", "Change Model", "You are holding the incorrect item to apply this model to.\nTo see the list of custom models and their available items, click here or write: /modellist.").clickEvent(ClickEvent.runCommand("/modellist")));
    }

    @CommandAlias("modellist")
    @CommandPermission("swagsmp.changeitemmodel")
    @Description("See all the custom models that exist")
    public void onModelList(Player player, String[] args) {

        int pageSize = 45; // Number of custom models per page
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

        Inventory inventory = Bukkit.createInventory(null, 54, Component.text("Custom Models List (Page: " + (pageNumber + 1) + ")"));

        List<ItemModels> itemModels = new ArrayList<>();
        Collections.addAll(itemModels, ItemModels.values());

        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, ItemModels.values().length);

        inventory.setItem(49, GuiItems.closeGui());

        if (endIndex < itemModels.size()) {
            ItemStack nextPageArrow = new ItemStack(Material.ARROW);
            ItemMeta nextPageMeta = nextPageArrow.getItemMeta();
            nextPageMeta.displayName(Component.text("Next Page"));
            nextPageMeta.setCustomModelData(0);
            nextPageArrow.setItemMeta(nextPageMeta);
            inventory.setItem(53, nextPageArrow);
        }

        if(pageNumber != 0){
            ItemStack previousPageArrow = new ItemStack(Material.ARROW);
            ItemMeta previousPageMeta = previousPageArrow.getItemMeta();
            previousPageMeta.displayName(Component.text("Previous Page"));
            previousPageMeta.setCustomModelData(1);
            previousPageArrow.setItemMeta(previousPageMeta);
            inventory.setItem(45, previousPageArrow);
        }

        for (int i = startIndex; i < endIndex; i++) {
            ItemModels customItem = itemModels.get(i); // Get the current ItemModels from the list

            ItemStack displayItem = new ItemStack(customItem.getValidMaterials()[0]);
            ItemMeta displayItemMeta = displayItem.getItemMeta();

            displayItemMeta.displayName(Component.text(customItem.name().toLowerCase()));
            displayItemMeta.setCustomModelData(customItem.getCustomModelDataId());
            List<Component> lore = new ArrayList<>();

            lore.add(Component.text("Available Materials:"));
            for (Material material : customItem.getValidMaterials()) {
                lore.add(Component.text("- " + material.name()));
            }

            if(customItem.getTransformable()){
                lore.add(Component.text("Cost: " + customItem.getSwagCoinCost() + " Swag Coins"));
            }else{
                lore.add(Component.text("Unobtainable via /model"));
            }

            displayItemMeta.lore(lore);
            displayItem.setItemMeta(displayItemMeta);
            inventory.addItem(displayItem);
        }

        player.openInventory(inventory);
        int finalNextPage = pageNumber;
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() == player && event.getInventory() == inventory) {
                    event.setCancelled(true); // Cancel the event to prevent moving any items in the GUI
                    ItemStack clickedItem = event.getCurrentItem();
                    if(clickedItem != null && clickedItem.getType() == Material.BARRIER && clickedItem.getItemMeta().getCustomModelData() ==  1){
                        event.setCancelled(true);
                        inventory.close();
                    }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 0){
                        event.setCancelled(true);
                        int nextPageNumber = finalNextPage + 1;
                        onModelList(player, new String[]{String.valueOf(nextPageNumber)});
                    }else if(clickedItem != null && clickedItem.getType() == Material.ARROW & clickedItem.getItemMeta().getCustomModelData() == 1){
                        event.setCancelled(true);
                        int previousPageNumber = finalNextPage - 1;
                        onModelList(player, new String[]{String.valueOf(previousPageNumber)});
                    }
                }
            }
        }, SwagSMPCore.getPlugin());
    }
}
