package me.jackjack33.jackscars;

import me.jackjack33.jackscars.Commands.JC;
import me.jackjack33.jackscars.Commands.JCHelp;

import me.jackjack33.jackscars.Events.CarEvent;
import me.jackjack33.jackscars.util.YAMLConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public YAMLConfig carConfig = new YAMLConfig(this.getDataFolder(), "data");

    @Override
    public void onEnable() {

        carConfig.createConfig();
        this.saveDefaultConfig();

        new JCHelp(this);
        new JC(this);

        getServer().getPluginManager().registerEvents(new CarEvent(this), this);

        Bukkit.getConsoleSender().sendMessage("JacksCars has started successfully.");
    }
}
