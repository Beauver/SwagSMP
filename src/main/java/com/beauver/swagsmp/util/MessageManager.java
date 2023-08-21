package com.beauver.swagsmp.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MessageManager {

    public static Component messageGenerator(String severity, String type, String message) {
        String txtcolor = "#7d818d";
        switch (severity) {
            case "SUCCESS":
                txtcolor = "#83c916";
                break;
            case "WARNING":
                txtcolor = "#f09c0b";
                break;
            case "ERROR":
                txtcolor = "#d82625";
                break;
        }

        return Component.text()
                .append(Component.text("[SwagSMP - " + type + "]").color(TextColor.fromHexString(txtcolor)))
                .append(Component.text("\n" + message).color(TextColor.fromHexString(txtcolor)))
                .build();
    }

    public static Component messageGenerator(String severity, String type, Component message) {
        String txtcolor = "#7d818d";
        switch (severity) {
            case "SUCCESS":
                txtcolor = "#83c916";
                break;
            case "WARNING":
                txtcolor = "#f09c0b";
                break;
            case "ERROR":
                txtcolor = "#d82625";
                break;
        }

        return Component.text()
                .append(Component.text("[SwagSMP - " + type + "]").color(TextColor.fromHexString(txtcolor))
                .append(Component.text("\n").append(message.color(TextColor.fromHexString(txtcolor)))))
                .build();
    }
}
