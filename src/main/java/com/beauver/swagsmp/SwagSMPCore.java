package com.beauver.swagsmp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.beauver.swagsmp.commands.PvPCommand;
import com.beauver.swagsmp.commands.ReportCommand;
import com.beauver.swagsmp.commands.moderation.BanCommand;
import com.beauver.swagsmp.commands.moderation.ClearChatCommand;
import com.beauver.swagsmp.commands.moderation.KickCommand;
import com.beauver.swagsmp.commands.moderation.MuteCommand;
import com.beauver.swagsmp.discord.DiscordBot;
import com.beauver.swagsmp.handlers.KickHandler;
import com.beauver.swagsmp.listeners.ChatListener;
import com.beauver.swagsmp.listeners.PlayerDamage;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.beauver.swagsmp.listeners.PlayerJoin;
import jdk.jshell.Snippet;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;


public final class SwagSMPCore extends JavaPlugin {
    private PlayerDataManager playerDataManager;
    private JDA jda;
    private DiscordBot discordBot;
    private static SwagSMPCore plugin;
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

        playerDataManager = new PlayerDataManager(this);

        // Initialize the discordBot instance
        enableDiscord();
        discordBot = new DiscordBot(jda);

        // Enable commands, listeners, etc.
        enableCommands();
        enableListeners();

        getLogger().info("|                                                        |");
        getLogger().info("|-----------------------------[ ENABLED SUCCESSFULLY ]---|");
    }


    public void enableCommands(){
        PaperCommandManager manager = new PaperCommandManager(this);
        //Moderation commands
        manager.registerCommand(new BanCommand(playerDataManager));
        manager.registerCommand(new KickCommand());
        manager.registerCommand(new MuteCommand());
        manager.registerCommand(new ClearChatCommand());
        //player commands
        manager.registerCommand(new PvPCommand(playerDataManager));
        manager.registerCommand(new ReportCommand(playerDataManager, discordBot));

        getLogger().info("|   Enabled commands                                     |");
    }

    public void enableListeners(){

        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(playerDataManager), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDamage(playerDataManager), this);
        getLogger().info("|   Enabled listeners                                    |");

    }

    public void enableDiscord(){
        this.jda = JDABuilder.createDefault(this.getConfig().getString("DiscordBotToken"))
                .addEventListeners(new DiscordBot(jda))
                .build();
        getLogger().info("|   Discord Bot Enabled                                  |");

    }

    @Override
    public void onDisable() {
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");
        getLogger().info("|                                                        |");
        getLogger().info("|----------------------------[ DISABLED SUCCESSFULLY ]---|");
    }
}
