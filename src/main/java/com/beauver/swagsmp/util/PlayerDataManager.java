package com.beauver.swagsmp.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {

    private final Plugin plugin;
    private final File dataFolder;

    public PlayerDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(Bukkit.getPluginsFolder().getAbsolutePath(), "SwagSMP/playerData");

        boolean directoriesCreated = dataFolder.mkdirs();
        if (!directoriesCreated && !(dataFolder.exists())) {
            plugin.getLogger().warning("Failed to create data directories.");
        }
    }

    private File getPlayerFile(UUID playerUUID) {
        File playerFile = new File(dataFolder, playerUUID.toString() + ".yml");

        if (!playerFile.exists()) {
            try {
                if (!playerFile.createNewFile()) {
                    plugin.getLogger().warning("Failed to create player data file.");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return playerFile;
    }

    //ALL CREATE FUNCTIONS

    public void createData(UUID playerUUID, String dataType, String value) {
        File playerFile = getPlayerFile(playerUUID);
        if(playerFile == null){
            plugin.getLogger().warning("Player File does not exist.");
        }else{
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            config.set(dataType, value);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createData(UUID playerUUID, String dataType, boolean value) {
        File playerFile = getPlayerFile(playerUUID);
        if(playerFile == null){
            plugin.getLogger().warning("Player File does not exist.");
        }else{
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            config.set(dataType, value);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createData(UUID playerUUID, String dataType, int value) {
        File playerFile = getPlayerFile(playerUUID);
        if(playerFile == null){
            plugin.getLogger().warning("Player File does not exist.");
        }else{
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            config.set(dataType, value);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataListString(UUID playerUUID, String dataType, List<String> values) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, values);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataListInt(UUID playerUUID, String dataType, List<Integer> values) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, values);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataListBoolean(UUID playerUUID, String dataType, List<Boolean> values) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, values);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ALL READ FUNCTIONS
    public String readDataString(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return null; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getString(dataType);
    }

    public int readDataInt(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return -1; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getInt(dataType);
    }

    public boolean readDataBoolean(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return false; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getBoolean(dataType);
    }


    public List<String> readDataListString(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return null; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getStringList(dataType);
    }

    public List<Integer> readDataListInt(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return null; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getIntegerList(dataType);
    }

    public List<Boolean> readDataListBoolean(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return null; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getBooleanList(dataType);
    }



    //ALL UPDATE FUNCTIONS
    public void updateData(UUID playerUUID, String dataType, String newValue) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, newValue);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateData(UUID playerUUID, String dataType, boolean newValue) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, newValue);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateData(UUID playerUUID, String dataType, int newValue) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, newValue);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateDataArrayString(UUID playerUUID, String dataType, List<String> newValues) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, newValues);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDataArrayInt(UUID playerUUID, String dataType, List<Integer> newValues) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, newValues);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDataArrayBoolean(UUID playerUUID, String dataType, List<Boolean> newValues) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            config.set(dataType, newValues);
            try {
                config.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //DELETE FUNCTION
    public void deleteData(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);

        if (playerFile == null || !playerFile.exists()) {
            plugin.getLogger().warning("Player File does not exist.");
            return; // Return a default value or handle the case when the file doesn't exist
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, null);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
