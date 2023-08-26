package com.beauver.swagsmp.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.beauver.swagsmp.enums.ItemModels;
import com.beauver.swagsmp.util.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
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
            if (customItem.isValidMaterial(heldMaterial) && customItem.name().equals(itemName)) {
                ItemMeta currentItemMeta = currentItem.getItemMeta();
                if (currentItemMeta == null) {
                    currentItemMeta = Bukkit.getItemFactory().getItemMeta(currentItem.getType());
                }
                currentItemMeta.setCustomModelData(customItem.getCustomModelDataId());
                currentItem.setItemMeta(currentItemMeta);

                player.sendMessage(MessageManager.messageGenerator("SUCCESS", "Change Model", "Successfully changed your item model"));
                return;
            }
        }
        player.sendMessage(MessageManager.messageGenerator("ERROR", "Change Model", "You are holding the incorrect item to apply this model to.\nTo see the list of custom models and their available items, click here or write: /modellist.").clickEvent(ClickEvent.runCommand("/modellist")));
    }

    @CommandAlias("modellist")
    @CommandPermission("swagsmp.changeitemmodel")
    @Description("See all the custom models that exist")
    public void onModelList(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("Custom Models List"));

        for (ItemModels customItem : ItemModels.values()) {

            ItemStack displayItem = new ItemStack(customItem.getValidMaterials()[0]);
            ItemMeta displayItemMeta = displayItem.getItemMeta();

            displayItemMeta.displayName(Component.text(customItem.name().toLowerCase()));
            displayItemMeta.setCustomModelData(customItem.getCustomModelDataId());
            List<Component> lore = new ArrayList<>();

            lore.add(Component.text("Available Materials:"));
            for (Material material : customItem.getValidMaterials()) {
                lore.add(Component.text("- " + material.name()));
            }

            displayItemMeta.lore(lore);
            displayItem.setItemMeta(displayItemMeta);
            inventory.addItem(displayItem);
        }

        player.openInventory(inventory);

    }
}
