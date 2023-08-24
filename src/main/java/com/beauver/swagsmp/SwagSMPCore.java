package com.beauver.swagsmp;

import co.aikar.commands.PaperCommandManager;
import com.beauver.swagsmp.commands.PvPCommand;
import com.beauver.swagsmp.commands.ReportCommand;
import com.beauver.swagsmp.commands.VerifyDiscordCommand;
import com.beauver.swagsmp.commands.moderation.BanCommand;
import com.beauver.swagsmp.commands.moderation.ClearChatCommand;
import com.beauver.swagsmp.commands.moderation.KickCommand;
import com.beauver.swagsmp.commands.moderation.MuteCommand;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.listeners.ChatListener;
import com.beauver.swagsmp.listeners.PlayerDamage;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.beauver.swagsmp.listeners.PlayerJoin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class SwagSMPCore extends JavaPlugin {
    private PlayerDataManager playerDataManager;
    private VerifyDiscordCommand verifyDiscordCommand;
    private DiscordBot discordBot;
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

        enableClasses();

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
        manager.registerCommand(new PvPCommand(playerDataManager));
        manager.registerCommand(new ReportCommand(playerDataManager, discordBot));
        manager.registerCommand(new VerifyDiscordCommand(playerDataManager));

        getLogger().info("|   Enabled commands                                     |");
    }

    public void enableListeners(){

        this.getServer().getPluginManager().registerEvents(new ChatListener(playerDataManager, discordBot), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(playerDataManager, discordBot), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(playerDataManager), this);
        getLogger().info("|   Enabled listeners                                    |");

    }

    public void enableDiscord(){
        this.jda = JDABuilder.createDefault(this.getConfig().getString("DiscordBotToken"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new DiscordBot(jda, verifyDiscordCommand))
                .build();
        getLogger().info("|   Discord Bot Enabled                                  |");

    }

    public void enableClasses(){

        // Initialize the discordBot instance
        playerDataManager = new PlayerDataManager(this);
        verifyDiscordCommand = new VerifyDiscordCommand(playerDataManager);
        enableDiscord();
        discordBot = new DiscordBot(jda, verifyDiscordCommand);
    }

    @Override
    public void onDisable() {
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");
        disableDiscord();
        getLogger().info("|                                                        |");
        getLogger().info("|----------------------------[ DISABLED SUCCESSFULLY ]---|");
    }

    public void disableDiscord(){
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            jda.shutdownNow();
            getLogger().info("|   Discord Bot Disabled                                 |");
        }
    }
}
