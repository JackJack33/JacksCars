package me.jackjack33.jackscars.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLConfig {

    private File YamlConfig;
    private FileConfiguration config;
    private File pluginDataFolder;
    private String name;

    public YAMLConfig(File pluginDataFolder, String name) {

        StringBuilder fileName = new StringBuilder();
        fileName.append(name).append(".yml");
        this.name = fileName.toString();

        YamlConfig = new File(pluginDataFolder, this.name);
        this.pluginDataFolder = pluginDataFolder;
        config = YamlConfiguration.loadConfiguration(YamlConfig);
    }

    public void createConfig() {

        if (! YamlConfig.exists()) {

            if (! this.pluginDataFolder.exists()) {

                this.pluginDataFolder.mkdir();
            }

            try {
                YamlConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getDirectory() {
        return pluginDataFolder;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return YamlConfig;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void addDefault(String key, String value) {
        if (config.getString(key) == null) {

            config.set(key, value);

            try {
                config.save(YamlConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addDefaults(Map<String,Object> defaults) {
        for (String s : defaults.keySet()) {
            this.config.set(s, defaults.get(s));
            try {
                config.save(YamlConfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            config.save(YamlConfig);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile() {
        YamlConfig.delete();
    }

    public void deleteParentDir() {
        this.getDirectory().delete();
    }

    public void reset() {
        this.deleteFile();
        try {
            YamlConfig.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wipeDirectory() {
        this.getDirectory().delete();
        this.pluginDataFolder.mkdir();
    }

    public void createSubDirectory(String name) throws IOException {
        if (!pluginDataFolder.exists()) {
            throw new IOException("Data folder not found.");
        }

        File subDir = new File(pluginDataFolder, name);

        if (subDir.exists()) {
            throw new IOException("Sub directory already existing.");
        }

        subDir.mkdir();
    }

    public boolean contains(String value) {
        return config.contains(value);
    }

}
