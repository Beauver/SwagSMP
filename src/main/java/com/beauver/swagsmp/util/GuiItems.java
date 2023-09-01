package com.beauver.swagsmp.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiItems {

    public static ItemStack playerSkull(OfflinePlayer target){

        ItemStack skullPlayer = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullPlayer.getItemMeta();
        skullMeta.setOwningPlayer(target);
        skullMeta.setCustomModelData(0);
        skullPlayer.setItemMeta(skullMeta);
        return skullPlayer;
    }

    public static ItemStack closeGui(){

        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Close"));
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack swagCoins(){
        ItemStack swagCoins = new ItemStack(Material.PAPER);
        ItemMeta swagCoinsMeta = swagCoins.getItemMeta();
        swagCoinsMeta.setCustomModelData(100);
        swagCoinsMeta.displayName(Component.text("Swag Coin").color(TextColor.fromHexString("#FFD700")));
        swagCoins.setItemMeta(swagCoinsMeta);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Authentic Swag Coin"));
        swagCoinsMeta.lore(lore);

        return swagCoins;
    }
}
