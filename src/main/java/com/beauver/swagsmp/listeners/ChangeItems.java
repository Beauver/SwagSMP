package com.beauver.swagsmp.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChangeItems implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.PAPER) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.getCustomModelData() == 100) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Authentic Swag Coin"));
                itemMeta.lore(lore);
                itemMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
                item.setItemMeta(itemMeta);
            }
        }
    }
    @EventHandler
    public void onPlayerInvClick(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        if (item != null && item.getType() == Material.PAPER) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.getCustomModelData() == 100) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Authentic Swag Coin"));
                itemMeta.lore(lore);
                itemMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
                item.setItemMeta(itemMeta);
            }
        }
    }
    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event){
        ItemStack item = event.getMainHandItem();
        ItemStack otherHand = event.getOffHandItem();
        if (item != null && item.getType() == Material.PAPER) {
            ItemMeta itemMeta = item.getItemMeta();

            if(otherHand == null){
                return;
            }

            ItemMeta otherHandItemMeta = otherHand.getItemMeta();

            if(otherHandItemMeta != null && otherHandItemMeta.getCustomModelData() == 100) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Authentic Swag Coin"));
                otherHandItemMeta.lore(lore);
                otherHandItemMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
                otherHand.setItemMeta(otherHandItemMeta);
            }

            if (itemMeta != null && itemMeta.getCustomModelData() == 100) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Authentic Swag Coin"));
                itemMeta.lore(lore);
                itemMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
                item.setItemMeta(itemMeta);
            }
        }
    }
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.PAPER) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.getCustomModelData() == 100) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text("Authentic Swag Coin"));
                itemMeta.lore(lore);
                itemMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
                item.setItemMeta(itemMeta);
            }
        }
    }
}
