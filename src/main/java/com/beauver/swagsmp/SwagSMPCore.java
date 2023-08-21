package com.beauver.swagsmp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.beauver.swagsmp.commands.PvPCommand;
import com.beauver.swagsmp.util.PlayerDataManager;
import com.beauver.swagsmp.listeners.PlayerJoin;
import org.bukkit.plugin.java.JavaPlugin;


public final class SwagSMPCore extends JavaPlugin {
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");
        playerDataManager = new PlayerDataManager(this);
        enableCommands();
        enableListeners();
        getLogger().info("|                                                        |");
        getLogger().info("|-----------------------------[ ENABLED SUCCESSFULLY ]---|");
    }


    public void enableCommands(){
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new PvPCommand(playerDataManager));

        getLogger().info("|   Enabled commands                                  |");
    }

    public void enableListeners(){

        this.getServer().getPluginManager().registerEvents(new PlayerJoin(playerDataManager), this);
        getLogger().info("|   Enabled listeners                                  |");

    }

    @Override
    public void onDisable() {
        getLogger().info("|---[ SwagSMPCore ]--------------------------------------|");
        getLogger().info("|                                                        |");
        getLogger().info("|                                                        |");
        getLogger().info("|----------------------------[ DISABLED SUCCESSFULLY ]---|");
    }
}
