package com.beauver.swagsmp.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerDataManager {

    private final Plugin plugin;
    private final File dataFolder;

    public PlayerDataManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(Bukkit.getPluginsFolder().getAbsolutePath(), "SwagSMP/playerData");
        dataFolder.mkdirs();
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

    public void createData(UUID playerUUID, String dataType, Object values) {
        File playerFile = getPlayerFile(playerUUID);
        if (playerFile == null) {
            plugin.getLogger().warning("Player File does not exist.");
        } else {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            if (values instanceof String[]) {
                config.set(dataType, Arrays.asList((String[]) values));
            } else if (values instanceof int[]) {
                List<Integer> intList = Arrays.stream((int[]) values).boxed().collect(Collectors.toList());
                config.set(dataType, intList);
            } else if (values instanceof boolean[]) {
                boolean[] boolArray = (boolean[]) values;
                List<Boolean> booleanList = new ArrayList<>(boolArray.length);
                for (boolean value : boolArray) {
                    booleanList.add(value);
                }
                config.set(dataType, booleanList);
            }

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
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getString(dataType);
    }

    public int readDataInt(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getInt(dataType);
    }

    public boolean readDataBoolean(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config.getBoolean(dataType);
    }

    public Object readDataArray(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        List<?> dataList = config.getList(dataType);

        if (dataList != null) {
            Object[] dataArray = dataList.toArray();
            if (dataArray instanceof String[]) {
                return (String[]) dataArray;
            } else if (dataArray instanceof Integer[]) {
                return Arrays.stream((Integer[]) dataArray).mapToInt(Integer::intValue).toArray();
            } else if (dataArray instanceof Boolean[]) {
                boolean[] boolArray = new boolean[dataArray.length];
                for (int i = 0; i < dataArray.length; i++) {
                    boolArray[i] = (boolean) dataArray[i];
                }
                return boolArray;
            }
        }

        // Add more cases for other array types as needed

        return null;
    }


    //ALL UPDATE FUNCTIONS
    public void updateData(UUID playerUUID, String dataType, String newValue) {
        File playerFile = getPlayerFile(playerUUID);
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
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, newValue);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateDataArray(UUID playerUUID, String dataType, Object newValues) {
        File playerFile = getPlayerFile(playerUUID);
        assert playerFile != null;
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        if (newValues instanceof String[]) {
            config.set(dataType, Arrays.asList((String[]) newValues));
        } else if (newValues instanceof int[]) {
            List<Integer> intList = Arrays.stream((int[]) newValues).boxed().collect(Collectors.toList());
            config.set(dataType, intList);
        } else if (newValues instanceof boolean[]) {
            boolean[] boolArray = (boolean[]) newValues;
            List<Boolean> booleanList = new ArrayList<>(boolArray.length);
            for (boolean value : boolArray) {
                booleanList.add(value);
            }
            config.set(dataType, booleanList);
        }
        // Add more cases for other array types if needed

        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //DELETE FUNCTION
    public void deleteData(UUID playerUUID, String dataType) {
        File playerFile = getPlayerFile(playerUUID);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        config.set(dataType, null);
        try {
            config.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
