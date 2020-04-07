package me.jackjack33.jackscars.Commands;

import me.jackjack33.jackscars.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.io.IOException;
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
            String usageMsg = plugin.getConfig().getString("msg-usage");
            if (usageMsg == null) { plugin.getLogger().warning("Option msg-usage is not set!"); return true;}
            String prefix = null;
            if (plugin.getConfig().getBoolean("is-prefix-set"))
                prefix = plugin.getConfig().getString("prefix") + " ";
            Player p = (Player) sender;
            if (args.length < 1) {
                p.sendMessage(usageMsg);
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
                    NamespacedKey key = new NamespacedKey(plugin, "jackscars");
                    String msg = plugin.getConfig().getString("msg-give");
                    String msg2 = plugin.getConfig().getString("car-name");
                    int dSpeed = plugin.getConfig().getInt("default-speed");
                    int dFuel = plugin.getConfig().getInt("default-fuel");

                    ItemStack cart = new ItemStack(Material.MINECART, 1);
                    ItemMeta meta = cart.getItemMeta();
                    if (meta == null) return true;

                    UUID ownerUUID = p.getUniqueId();
                    String owner = p.getName();
                    if (args.length > 1) {
                        ownerUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                        owner = args[1];
                    }
                    meta.getCustomTagContainer().setCustomTag(key, ItemTagType.STRING, ownerUUID.toString());

                    if (args.length > 2) {
                        dSpeed = Integer.parseInt(args[2]);
                    }

                    List<String> lore = new ArrayList<>();
                    String line1 = plugin.getConfig().getString("car-lore.owner") + " " + owner;
                    String line2 = plugin.getConfig().getString("car-lore.level") + " 1";
                    String line3 = plugin.getConfig().getString("car-lore.speed") + " " + dSpeed;
                    String line4 = plugin.getConfig().getString("car-lore.fuel") + " " + dFuel;
                    lore.add(line1);
                    lore.add(line2);
                    lore.add(line3);
                    lore.add(line4);
                    List<String> extra = plugin.getConfig().getStringList("car-lore.extra");
                    lore.addAll(extra);

                    meta.setDisplayName(msg2);
                    meta.setLore(lore);
                    cart.setItemMeta(meta);
                    p.getInventory().addItem(cart);
                    p.sendMessage(prefix + msg);
                }
            }
            else if (args[0].equalsIgnoreCase("speed")) {
                if (p.hasPermission("jc.speed")) {
                    if (args.length > 1) {
                        String msg = plugin.getConfig().getString("msg-speed");
                        UUID uuid = p.getUniqueId();
                        int newSpeed = Integer.parseInt(args[1]);
                        if (newSpeed > 1000) {
                            newSpeed = 1000;
                            String msg2 = plugin.getConfig().getString("msg-speed-max");
                            if (msg2 == null) { plugin.getLogger().warning("Option msg-speed-msg is not set!"); return true; }
                            p.sendMessage(msg2);
                        }
                        plugin.carConfig.getConfig().set("carspeed." + uuid.toString(), newSpeed);
                        plugin.carConfig.save();
                        p.sendMessage(prefix + msg + " " + newSpeed);
                    }
                    else {
                        p.sendMessage(usageMsg);
                    }
                }
            }
            else {
                p.sendMessage(usageMsg);
            }
        }
        return false;
    }
}
