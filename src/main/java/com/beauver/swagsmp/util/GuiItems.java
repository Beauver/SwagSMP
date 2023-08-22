package com.beauver.swagsmp.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
}
