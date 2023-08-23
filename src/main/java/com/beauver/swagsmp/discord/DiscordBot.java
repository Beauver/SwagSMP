package com.beauver.swagsmp.discord;

import com.beauver.swagsmp.SwagSMPCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class DiscordBot extends ListenerAdapter {
    private JDA jda;

    Plugin plugin = SwagSMPCore.getPlugin();

    public DiscordBot(JDA jda) {
        this.jda = jda;
    }

//    @Override
//    public void onReady(ReadyEvent event) {
//        TextChannel channel = event.getJDA().getTextChannelById(plugin.getConfig.getString("RandomChannel"));
//        if (channel != null) {
//            // Send a message to the channel
//            channel.sendMessage("Bot is now online and ready to interact!").queue();
//        }
//    }

    public void sendReport(String playerName, String targetName, String reportReason) {
        TextChannel channel = jda.getTextChannelById(Objects.requireNonNull(plugin.getConfig().getString("ReportChannel")));
        if (channel != null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(playerName)
                    .setAuthor("New Report")
                    .setDescription("Just reported: " + targetName)
                    .addField("Reason:", reportReason, false)
                    .setColor(Color.ORANGE) // Customize the embed color
                    .setTimestamp(Instant.now());
            channel.sendMessageEmbeds(embed.build()).queue();
        }
    }
}
