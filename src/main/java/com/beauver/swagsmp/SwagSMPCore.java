package com.beauver.swagsmp;

import co.aikar.commands.PaperCommandManager;
import com.beauver.swagsmp.commands.ChangeModelCommand;
import com.beauver.swagsmp.commands.PvPCommand;
import com.beauver.swagsmp.commands.ReportCommand;
import com.beauver.swagsmp.commands.VerifyDiscordCommand;
import com.beauver.swagsmp.commands.moderation.BanCommand;
import com.beauver.swagsmp.commands.moderation.ClearChatCommand;
import com.beauver.swagsmp.commands.moderation.KickCommand;
import com.beauver.swagsmp.commands.moderation.MuteCommand;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.handlers.BanHandler;
import com.beauver.swagsmp.handlers.MuteHandler;
import com.beauver.swagsmp.listeners.ChatListener;
import com.beauver.swagsmp.listeners.PlayerDamage;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.beauver.swagsmp.listeners.PlayerJoin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class SwagSMPCore extends JavaPlugin {
    private PlayerDataManager playerDataManager;
    private VerifyDiscordCommand verifyDiscordCommand;
    private DiscordBot discordBot;
    private MuteHandler muteHandler;
    private BanHandler banHandler;
    private JDA jda;
    private static SwagSMPCore plugin;
    public Map<String, String> playerLinkCodes = new HashMap<>();
    public static SwagSMPCore getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            enableClasses();
        } catch (LoginException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Enable commands, listeners, etc.
        enableCommands();
        enableListeners();

        getLogger().info("|                                                        |");
        getLogger().info("|-----------------------------[ ENABLED SUCCESSFULLY ]---|");
    }

    public void enableCommands(){
        PaperCommandManager manager = new PaperCommandManager(this);
        //Moderation commands
        manager.registerCommand(new BanCommand(playerDataManager, discordBot));
        manager.registerCommand(new KickCommand(discordBot));
        manager.registerCommand(new MuteCommand(playerDataManager, discordBot));
        manager.registerCommand(new ClearChatCommand(discordBot));
        //player commands
        manager.registerCommand(new ChangeModelCommand());
        manager.registerCommand(new PvPCommand(playerDataManager, discordBot));
        manager.registerCommand(new ReportCommand(playerDataManager, discordBot));
        manager.registerCommand(new VerifyDiscordCommand(playerDataManager));

        getLogger().info("|   Enabled commands                                     |");
    }

    public void enableListeners(){
        this.getServer().getPluginManager().registerEvents(new ChatListener(playerDataManager, discordBot), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(playerDataManager, discordBot), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(playerDataManager, discordBot), this);
        getLogger().info("|   Enabled listeners                                    |");

    }

    public void enableDiscord() throws LoginException, InterruptedException {
        this.jda = JDABuilder.createDefault(this.getConfig().getString("DiscordBotToken"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new DiscordBot(jda, verifyDiscordCommand, playerDataManager, muteHandler, banHandler))
                .build();
        getLogger().info("|   Discord Bot Enabled                                  |");
        jda.awaitReady();
    }


    public void enableClasses() throws LoginException, InterruptedException {
        playerDataManager = new PlayerDataManager(this);
        verifyDiscordCommand = new VerifyDiscordCommand(playerDataManager);
        muteHandler = new MuteHandler(playerDataManager);
        banHandler = new BanHandler(playerDataManager);

        enableDiscord();
        discordBot = new DiscordBot(jda, verifyDiscordCommand, playerDataManager, muteHandler, banHandler);
        muteHandler.setMuteHandler(discordBot);
        banHandler.setBanHandler(discordBot);

        getLogger().info("|   Enabled Classes                                      |");
    }

    @Override
    public void onDisable() {
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");
        try {
            disableDiscord();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("|                                                        |");
        getLogger().info("|----------------------------[ DISABLED SUCCESSFULLY ]---|");
    }

    public void disableDiscord() throws InterruptedException{

        try {
            TextChannel textChannel = jda.getTextChannelById(plugin.getConfig().getString("MinecraftDiscordChannel"));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":x: Server closing!")
                    .setColor(Color.RED); // Customize the embed color
            textChannel.sendMessageEmbeds(embed.build()).queue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
                jda.shutdownNow();

                try {
                    jda.awaitShutdown();
                    getLogger().info("|   Discord Bot Disabled                                 |");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }
}
