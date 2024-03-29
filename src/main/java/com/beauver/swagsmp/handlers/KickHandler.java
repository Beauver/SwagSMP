package com.beauver.swagsmp.handlers;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.util.MessageManager;
import com.beauver.swagsmp.util.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.util.Date;

public class KickHandler {

    Plugin plugin = SwagSMPCore.getPlugin();

    public static void kickPlayer(Player player, String reason, String playerWhoKicked){

        player.kick(MessageManager.messageGenerator("ERROR", "Kick", Component.text("", Style.style(TextDecoration.BOLD)))
                .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(reason).color(TextColor.fromHexString("#f09c0b")))
                .append(Component.text("\nKicked By: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(playerWhoKicked)).color(TextColor.fromHexString("#f09c0b")));
    }

    public static void kickWhitelist(Player player){

        player.kick(MessageManager.messageGenerator("ERROR", "Kick", Component.text("", Style.style(TextDecoration.BOLD)))
                .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text("You are not whitelisted on our server.").color(TextColor.fromHexString("#f09c0b"))));

    }

    public static void kickBanPlayer(Player player, String reason, String playerWhoKicked, String appealCode) {

        player.kick(MessageManager.messageGenerator("ERROR", "Ban", Component.text("", Style.style(TextDecoration.BOLD)))
                .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(reason).color(TextColor.fromHexString("#f09c0b")))
                .append(Component.text("\nBanned By: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(playerWhoKicked)).color(TextColor.fromHexString("#f09c0b"))
                .append(Component.text("\nExpires: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text("never")).color(TextColor.fromHexString("#f09c0b"))
                .append(Component.text("\nAppeal Code: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(appealCode)).color(TextColor.fromHexString("#f09c0b")));

    }

    public static void kickBanPlayer(Player player, String reason, String playerWhoKicked, String appealCode, Date date) {

        player.kick(MessageManager.messageGenerator("ERROR", "Ban", Component.text("", Style.style(TextDecoration.BOLD)))
                .append(Component.text("\nReason: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(reason).color(TextColor.fromHexString("#f09c0b")))
                .append(Component.text("\nBanned By: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(playerWhoKicked)).color(TextColor.fromHexString("#f09c0b"))
                .append(Component.text("\nExpires: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(String.valueOf(date))).color(TextColor.fromHexString("#f09c0b"))
                .append(Component.text("\nAppeal Code: ", Style.style(TextDecoration.BOLD)).color(TextColor.fromHexString("#d82625")))
                .append(Component.text(appealCode)).color(TextColor.fromHexString("#f09c0b")).clickEvent(ClickEvent.copyToClipboard(appealCode)));

    }
}
