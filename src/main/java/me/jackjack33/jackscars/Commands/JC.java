package me.jackjack33.jackscars.Commands;

import me.jackjack33.jackscars.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JC implements CommandExecutor {

    private Main plugin;
    public JC(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("jc").setExecutor(this);

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            List<String> usageMsg = plugin.getConfig().getStringList("msg-help");
            String prefix = plugin.getConfig().getString("prefix");
            if (plugin.getConfig().getBoolean("is-prefix-set"))
                prefix = plugin.getConfig().getString("prefix") + " ";
            if (args.length < 1) {
                p.sendMessage(prefix+"Help Menu");
                for (String text : usageMsg) {
                    p.sendMessage(text);
                }
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("jc.reload")) {
                    String msg = plugin.getConfig().getString("msg-reload");
                    p.sendMessage(prefix + msg);
                    plugin.reloadConfig();
                    msg = plugin.getConfig().getString("msg-reload-done");
                    p.sendMessage(prefix + msg);
                }
            }
            else if (args[0].equalsIgnoreCase("give")) {
                if (p.hasPermission("jc.give")) {
                   plugin.getCar(p, 1, plugin.getConfig().getInt("default-speed"), plugin.getConfig().getInt("default-fuel"));
                   p.sendMessage(prefix + plugin.getConfig().getString("msg-give"));
                }
            }
            else if (args[0].equalsIgnoreCase("speed")) {
                if (p.hasPermission("jc.speed")) {
                    if (args.length > 1) {
                        String msg = plugin.getConfig().getString("msg-speed");
                        UUID uuid = p.getUniqueId();

                        ItemStack tool = p.getInventory().getItemInMainHand();
                        ItemMeta toolMeta = tool.getItemMeta();

                        if (toolMeta == null) {
                            p.sendMessage(prefix + plugin.getConfig().getString("msg-hold"));
                            return true;
                        }

                        NamespacedKey isCar = new NamespacedKey(plugin, "JacksCars-car");
                        toolMeta.getPersistentDataContainer().get(isCar, PersistentDataType.STRING);
                        String isACar = toolMeta.getPersistentDataContainer().get(isCar, PersistentDataType.STRING);

                        if (isACar == null) {
                            p.sendMessage(prefix + plugin.getConfig().getString("msg-hold"));
                            return true;
                        }

                        List<String> lore = toolMeta.getLore();

                        int newSpeed = Integer.parseInt(args[1]);
                        if (newSpeed > 1000) {
                            newSpeed = 1000;
                            String msg2 = plugin.getConfig().getString("msg-speed-max");
                            p.sendMessage(msg2);
                        }

                        lore.set(2, plugin.getConfig().getString("car-lore.speed") + " " + newSpeed);
                        toolMeta.setLore(lore);
                        p.getInventory().getItemInMainHand().setItemMeta(toolMeta);
                        p.sendMessage(prefix + msg + " " + newSpeed);
                    }
                    else {
                        p.sendMessage(prefix+"Help Menu");
                        for (String text : usageMsg) {
                            p.sendMessage(text);
                        }
                    }
                }
            }
            else {
                p.sendMessage(prefix+"Help Menu");
                for (String text : usageMsg) {
                    p.sendMessage(text);
                }
            }
        }
        return false;
    }
}
