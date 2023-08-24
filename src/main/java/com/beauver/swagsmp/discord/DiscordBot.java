package com.beauver.swagsmp.discord;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.commands.VerifyDiscordCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DiscordBot extends ListenerAdapter {
    private JDA jda;
    private VerifyDiscordCommand verifyDiscordCommand;

    Plugin plugin = SwagSMPCore.getPlugin();

    public DiscordBot(JDA jda, VerifyDiscordCommand verifyDiscordCommand) {
        this.jda = jda;
        this.verifyDiscordCommand = verifyDiscordCommand;
    }

//    @Override
//    public void onReady(ReadyEvent event) {
//        TextChannel channel = event.getJDA().getTextChannelById(plugin.getConfig.getString("RandomChannel"));
//        if (channel != null) {
//            // Send a message to the channel
//            channel.sendMessage("Bot is now online and ready to interact!").queue();
//        }
//    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check if the message was sent in the #verify channel and is a command
        if (event.getChannel().getId().equals(plugin.getConfig().getString("LinkChannel")) &&
                event.getMessage().getContentRaw().startsWith("!link")) {

            // Extract the code from the message
            String[] parts = event.getMessage().getContentRaw().split("\\s+");
            if (parts.length >= 2) {
                String code = parts[1];

                // Process the command, e.g., link verification code with your logic
                verifyDiscordCommand.processLinkCommand(event, code);
            }
        }
    }

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

    public void embedBuilderMod(String title, String author, String description, Color color){
        TextChannel channel = jda.getTextChannelById(Objects.requireNonNull(plugin.getConfig().getString("AdminChannel")));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setAuthor(author)
                .setDescription(description)
                .setColor(color) // Customize the embed color
                .setTimestamp(Instant.now());
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void embedBuilderMod(String title, String author, String description, String field1Name, String field1Description, Color color){
        TextChannel channel = jda.getTextChannelById(Objects.requireNonNull(plugin.getConfig().getString("AdminChannel")));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setAuthor(author)
                .setDescription(description)
                .addField(field1Name, field1Description, false)
                .setColor(color) // Customize the embed color
                .setTimestamp(Instant.now());
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void embedBuilderMod(String title, String author, String description, String field1Name, String field1Description, String field2Name, String field2Description, Color color){
        TextChannel channel = jda.getTextChannelById(Objects.requireNonNull(plugin.getConfig().getString("AdminChannel")));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setAuthor(author)
                .setDescription(description)
                .addField(field1Name, field1Description, false)
                .addField(field2Name, field2Description, false)
                .setColor(color) // Customize the embed color
                .setTimestamp(Instant.now());
        channel.sendMessageEmbeds(embed.build()).queue();
    }

}
