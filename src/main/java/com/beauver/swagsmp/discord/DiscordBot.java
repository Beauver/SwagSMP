package com.beauver.swagsmp.discord;

import com.beauver.swagsmp.SwagSMPCore;
import com.beauver.swagsmp.commands.VerifyDiscordCommand;
import com.beauver.swagsmp.commands.moderation.BanCommand;
import com.beauver.swagsmp.commands.moderation.MuteCommand;
import com.beauver.swagsmp.handlers.BanHandler;
import com.beauver.swagsmp.handlers.MuteHandler;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordBot extends ListenerAdapter {
    private final JDA jda;
    private final VerifyDiscordCommand verifyDiscordCommand;
    private final PlayerDataManager playerDataManager;
    private final MuteHandler muteHandler;
    private final BanHandler banHandler;

    Plugin plugin = SwagSMPCore.getPlugin();

    public DiscordBot(JDA jda, VerifyDiscordCommand verifyDiscordCommand, PlayerDataManager playerDataManager, MuteHandler muteHandler, BanHandler banHandler) {
        this.jda = jda;
        this.verifyDiscordCommand = verifyDiscordCommand;
        this.playerDataManager = playerDataManager;
        this.muteHandler = muteHandler;
        this.banHandler = banHandler;
    }

    @Override
    public void onReady(ReadyEvent event) {
        try {
            TextChannel textChannel = event.getJDA().getTextChannelById(plugin.getConfig().getString("MinecraftDiscordChannel"));

            if(textChannel == null){
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":white_check_mark: Server started!")
                    .setColor(Color.GREEN); // Customize the embed color
            textChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Category category = event.getJDA().getCategoryById(plugin.getConfig().getString("AppealCategory"));

        if(category == null){
            return;
        }

        // Check if the message was sent in the #verify channel and is a command
        if (event.getChannel().getId().equals(plugin.getConfig().getString("LinkChannel"))){
            if(event.getMessage().getContentRaw().startsWith("!link")){
                // Extract the code from the message
                String[] parts = event.getMessage().getContentRaw().split("\\s+");
                event.getMessage().delete().queue();
                if (parts.length >= 2) {
                    String code = parts[1];

                    // Process the command, e.g., link verification code with your logic
                    verifyDiscordCommand.processLinkCommand(event, code);
                }
            }else{
                event.getMessage().delete().queue();
            }
        }else if(event.getChannel().getId().equals(plugin.getConfig().getString("AppealChannel"))){
            //getting the appeal category
            Category category1 = event.getJDA().getCategoryById(Objects.requireNonNull(plugin.getConfig().getString("AppealCategory")));
            if(category1 == null){
                return;
            }
            //getting the message content from discord
            String[] parts = event.getMessage().getContentRaw().split("\\s+");
            //removing any message
            event.getMessage().delete().queue();
            //getting the data pathfolder
            String playerDataFolderPath = Bukkit.getPluginsFolder().getAbsolutePath() + "/SwagSMPCore/playerData";
            //check if it's a real appeal code
            if(parts[0].startsWith("M")){
                try {
                    //make a list which stores all muted players
                    List<String> mutedPlayers = MuteCommand.getMutedPlayers(playerDataFolderPath);
                    //go thru every String (aka mc player name) in that list
                    for(String string : mutedPlayers){
                        //turn the name into a player object
                        OfflinePlayer player = Bukkit.getOfflinePlayer(string);
                        //get the appeal code from that player
                        String appealCode = playerDataManager.readDataString(player.getUniqueId(), "mutedAppealCode");
                        //check if it matches
                        if(parts[0].equals(appealCode)){
                            //if it does, create channel
                            category1.createTextChannel(player.getName() + "-" + "mute-appeal" + "-" + parts[0])
                                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ATTACH_FILES), null) // grant permissions to the member
                                    .queue(createdChannel -> {

                                        String mutedBy = playerDataManager.readDataString(player.getUniqueId(), "mutedBy");
                                        String mutedReason = playerDataManager.readDataString(player.getUniqueId(), "mutedReason");
                                        String muteExpires = playerDataManager.readDataString(player.getUniqueId(), "muteExpires");

                                        if (muteExpires != null) {
                                            if (muteExpires.equalsIgnoreCase("never")) {
                                                muteExpires = "Never";
                                            } else if(new Date().after(new Date(Long.parseLong(muteExpires)))) {
                                                muteExpires = "Already Expired";
                                            } else {
                                                long timestamp = Long.parseLong(muteExpires);
                                                Date expirationDate = new Date(timestamp);
                                                muteExpires = String.valueOf(expirationDate);
                                            }
                                        }else{
                                            muteExpires = "Null";
                                        }

                                        EmbedBuilder embed = new EmbedBuilder()

                                                .setTitle("Hey, " + event.getAuthor().getGlobalName())
                                                .setAuthor("Mute Appeal")
                                                .setDescription("Please check if the following data is correct. If not, please update us!\nMake sure to also still write your appeal here!\n")
                                                .addField("Minecraft Username:", player.getName(), false)
                                                .addField("Muted By:", mutedBy, false)
                                                .addField("Mute Reason:", mutedReason, false)
                                                .addField("Expires:", muteExpires, false)
                                                .addField("Appeal Code:", appealCode, false)
                                                .setColor(Color.ORANGE) // Customize the embed color
                                                .setTimestamp(Instant.now());
                                        createdChannel.sendMessageEmbeds(embed.build()).queue();
                                    });
                            //then stop
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(parts[0].startsWith("B")){
                try {
                    //make a list which stores all muted players
                    List<String> bannedPlayers = BanCommand.getBannedPlayers(playerDataFolderPath);
                    //go thru every String (aka mc player name) in that list
                    for(String string : bannedPlayers){
                        //turn the name into a player object
                        OfflinePlayer player = Bukkit.getOfflinePlayer(string);
                        //get the appeal code from that player
                        String appealCode = playerDataManager.readDataString(player.getUniqueId(), "appealCode");
                        //check if it matches
                        if(parts[0].equals(appealCode)){
                            //if it does, create channel
                            category1.createTextChannel(player.getName() + "-" + "ban-appeal" + "-" + parts[0])
                                    .addPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ATTACH_FILES), null) // grant permissions to the member
                                    .queue(createdChannel -> {

                                        BanList<PlayerProfile> banList = SwagSMPCore.getPlugin().getServer().getBanList(BanList.Type.PROFILE);
                                        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(player.getPlayerProfile());

                                        String banReason = banEntry.getReason();
                                        String bannedBy = banEntry.getSource();
                                        Date banExpiresDate = banEntry.getExpiration();
                                        String expires;

                                        if(banExpiresDate == null){
                                            expires = "Never";
                                        }else{
                                            expires = String.valueOf(banExpiresDate);
                                        }

                                        EmbedBuilder embed = new EmbedBuilder()
                                                .setTitle("Hey, " + event.getAuthor().getGlobalName())
                                                .setAuthor("Ban Appeal")
                                                .setDescription("Please check if the following data is correct. If not, please update us!\nMake sure to also still write your appeal here!\n")
                                                .addField("Minecraft Username:", player.getName(), false)
                                                .addField("Banned By:", bannedBy, false)
                                                .addField("Banned Reason:", banReason, false)
                                                .addField("Expires:", expires, false)
                                                .addField("Appeal Code:", appealCode, false)
                                                .setColor(Color.ORANGE) // Customize the embed color
                                                .setTimestamp(Instant.now());
                                        createdChannel.sendMessageEmbeds(embed.build()).queue();
                                    });
                            //then stop
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else if(category.getChannels().contains(event.getChannel())){

            if(event.getChannel().getName().contains("mute-appeal")){

                if(!(event.getAuthor().isBot())){
                    String content = event.getMessage().getContentRaw();
                    String[] args = content.split(" ");

                    if (args.length >= 2) { // Make sure there are enough arguments
                        String command = args[0];
                        String argument = args[1];

                        if (command.startsWith("!approve")) {
                            EmbedBuilder embed2 = new EmbedBuilder()
                                    .setTitle("Approved")
                                    .setAuthor("Mute Appeal")
                                    .setDescription("Your mute appeal has been approved!\nRemoving channel in 1 minute!")
                                    .setColor(Color.GREEN) // Customize the embed color
                                    .setTimestamp(Instant.now());
                            event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                            muteHandler.onUnmute(argument);
                            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                            scheduler.schedule(() -> {
                                event.getChannel().delete().queue();
                            }, 1, TimeUnit.MINUTES);
                        }
                    }else if(args.length == 1 && args[0].startsWith("!approve")){
                        EmbedBuilder embed2 = new EmbedBuilder()
                                .setTitle("ERROR")
                                .setAuthor("Mute Appeal")
                                .setDescription("Please provide the username of the Minecraft player you'd like to unmute.")
                                .setColor(Color.RED) // Customize the embed color
                                .setTimestamp(Instant.now());
                        event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                    }else if(args.length == 1 && args[0].startsWith("!decline")){
                        EmbedBuilder embed2 = new EmbedBuilder()
                                .setTitle("Declined")
                                .setAuthor("Mute Appeal")
                                .setDescription("Your mute appeal has been declined!\nRemoving channel in 1 minute!")
                                .setColor(Color.RED) // Customize the embed color
                                .setTimestamp(Instant.now());
                        event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                        event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                        scheduler.schedule(() -> {
                            event.getChannel().delete().queue();
                        }, 1, TimeUnit.MINUTES);
                    }
                }
            }else if(event.getChannel().getName().contains("ban-appeal")){
                if(!(event.getAuthor().isBot())){
                    String content = event.getMessage().getContentRaw();
                    String[] args = content.split(" ");

                    if (args.length >= 2) { // Make sure there are enough arguments
                        String command = args[0];
                        String argument = args[1];

                        if (command.startsWith("!approve")) {
                            EmbedBuilder embed2 = new EmbedBuilder()
                                    .setTitle("Approved")
                                    .setAuthor("Ban Appeal")
                                    .setDescription("Your ban appeal has been approved!\nRemoving channel in 1 minute")
                                    .setColor(Color.GREEN) // Customize the embed color
                                    .setTimestamp(Instant.now());
                            event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                            banHandler.onUnban(argument);

                            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                            scheduler.schedule(() -> {
                                event.getChannel().delete().queue();
                            }, 1, TimeUnit.MINUTES);
                        }
                    }else if(args.length == 1 && args[0].startsWith("!approve")){
                        EmbedBuilder embed2 = new EmbedBuilder()
                                .setTitle("ERROR")
                                .setAuthor("Ban Appeal")
                                .setDescription("Please provide the username of the Minecraft player you'd like to unban.")
                                .setColor(Color.RED) // Customize the embed color
                                .setTimestamp(Instant.now());
                        event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                    }else if(args.length == 1 && args[0].startsWith("!decline")){
                        EmbedBuilder embed2 = new EmbedBuilder()
                                .setTitle("Declined")
                                .setAuthor("Ban Appeal")
                                .setDescription("Your ban appeal has been declined!\nRemoving channel in 1 minute!")
                                .setColor(Color.RED) // Customize the embed color
                                .setTimestamp(Instant.now());
                        event.getChannel().sendMessageEmbeds(embed2.build()).queue();
                        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                        scheduler.schedule(() -> {
                            event.getChannel().delete().queue();
                        }, 1, TimeUnit.MINUTES);
                    }
                }
            }
        }else if(event.getChannel().getId().equals(plugin.getConfig().getString("MinecraftDiscordChannel"))){

            if(!(event.getAuthor().isBot())){
                String content = event.getMessage().getContentRaw();
                Bukkit.broadcast(Component.text("<" )
                        .append(Component.text("Discord: ").color(TextColor.fromHexString("#00ffff")))
                        .append(Component.text(event.getAuthor().getGlobalName() + "> " + content)).color(TextColor.fromHexString("#FFFFFF")));
            }
        }
    }

    public void sendReport(String playerName, String targetName, String reportReason) {
        TextChannel channel = jda.getTextChannelById(Objects.requireNonNull(plugin.getConfig().getString("ReportChannel")));

        if(channel == null){
            return;
        }

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

        if(channel == null){
            return;
        }

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

        if(channel == null){
            return;
        }

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

        if(channel == null){
            return;
        }

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

    public TextChannel getTextChannel(String configPath){
        TextChannel channel = jda.getTextChannelById(plugin.getConfig().getString(configPath));
        return channel;
    }
}
