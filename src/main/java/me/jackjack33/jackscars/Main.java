package me.jackjack33.jackscars;

import me.jackjack33.jackscars.Commands.JC;
import me.jackjack33.jackscars.Commands.JCHelp;

import me.jackjack33.jackscars.Events.CarEvent;
import me.jackjack33.jackscars.util.YAMLConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public YAMLConfig carConfig = new YAMLConfig(this.getDataFolder(), "data");
    private static Economy economy = null;

    @Override
    public void onEnable() {

        ConsoleCommandSender console = Bukkit.getConsoleSender();

        // Vault Intergration
        if (!setupEconomy()) {
            String[] error = {"", "§cAn error occurred whilst attempting to start up JacksCars", "§cVault was not detected during startup! Please install the latest version @ https://www.spigotmc.org/resources/vault.34315/", ""};
            console.sendMessage(error);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        carConfig.createConfig();
        this.saveDefaultConfig();

        new JCHelp(this);
        new JC(this);

        getServer().getPluginManager().registerEvents(new CarEvent(this), this);

        Bukkit.getConsoleSender().sendMessage("JacksCars has started successfully.");
    }

    @Override
    public void onDisable() {
        carConfig.save();
    }

    // Vault-API: Economy
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
