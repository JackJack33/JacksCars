package me.jackjack33.jackscars;

import me.jackjack33.jackscars.Commands.JC;

import me.jackjack33.jackscars.Events.CarEvent;
import me.jackjack33.jackscars.Events.SignEvent;
import me.jackjack33.jackscars.util.YAMLConfig;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    public YAMLConfig carConfig = new YAMLConfig(this.getDataFolder(), "data");
    public static Economy economy = null;

    public void getCar(Player p, Integer level, Integer speed, Integer fuel) {
        NamespacedKey key = new NamespacedKey(this, "jackscars");
        String name = this.getConfig().getString("car-name");
        int dSpeed = this.getConfig().getInt("default-speed");
        int dFuel = this.getConfig().getInt("default-fuel");

        ItemStack cart = new ItemStack(Material.MINECART, 1);
        ItemMeta meta = cart.getItemMeta();
        if (meta == null) return;

        UUID ownerUUID = p.getUniqueId();
        String owner = p.getName();
        meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, ownerUUID.toString());

        List<String> lore = new ArrayList<>();
        String line1 = this.getConfig().getString("car-lore.owner") + " " + owner;
        String line2 = this.getConfig().getString("car-lore.level") + " 1";
        String line3 = this.getConfig().getString("car-lore.speed") + " " + dSpeed;
        String line4 = this.getConfig().getString("car-lore.fuel") + " " + dFuel;
        lore.add(line1);
        lore.add(line2);
        lore.add(line3);
        lore.add(line4);
        List<String> extra = this.getConfig().getStringList("car-lore.extra");
        lore.addAll(extra);

        meta.setDisplayName(name);
        meta.setLore(lore);
        NamespacedKey isCar = new NamespacedKey(this, "JacksCars-car");
        meta.getPersistentDataContainer().set(isCar, PersistentDataType.STRING, "true");
        cart.setItemMeta(meta);
        p.getInventory().addItem(cart);
    }

    @Override
    public void onEnable() {

        ConsoleCommandSender console = Bukkit.getConsoleSender();

        // Vault Integration
        if (!setupEconomy()) {
            String[] error = {"", "§cAn error occurred whilst attempting to start up JacksCars", "§cVault was not detected during startup! Please install the latest version @ https://www.spigotmc.org/resources/vault.34315/", ""};
            console.sendMessage(error);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        carConfig.createConfig();
        this.saveDefaultConfig();

        new JC(this);

        getServer().getPluginManager().registerEvents(new CarEvent(this), this);
        getServer().getPluginManager().registerEvents(new SignEvent(this), this);

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

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
