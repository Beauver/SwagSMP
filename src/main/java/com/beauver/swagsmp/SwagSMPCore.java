package com.beauver.swagsmp;

import org.bukkit.plugin.java.JavaPlugin;

public final class SwagSMPCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        enableCommands();
        enableListeners();
    }


    public void enableCommands(){

    }

    public void enableListeners(){

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
