package me.jackjack33.jackscars.Events;

import me.jackjack33.jackscars.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SignEvent implements Listener {

    private Main plugin;
    public SignEvent(Main plugin) {
        this.plugin = plugin;
    }
    List<Player> confirmUpgrade = new ArrayList<>();

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getType().toString().contains("SIGN")) return;
        String prefix = "";
        if (plugin.getConfig().getBoolean("is-prefix-set")) prefix = plugin.getConfig().getString("prefix") + " ";
        String perm = plugin.getConfig().getString("msg-permission");
        Player player = event.getPlayer();
        Sign block = (Sign) event.getClickedBlock().getState();
        String line1 = block.getLine(0);
        String line2 = block.getLine(1);
        String line4 = block.getLine(3);
        String getCost = ChatColor.stripColor(line4);
        String symbol = plugin.getConfig().getString("symbol");
        getCost = getCost.substring(symbol.length());
        Double cost = Double.parseDouble(getCost);
        Boolean doCost = true;
        Double balance = plugin.economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
        if (!line1.contains(plugin.getConfig().getString("signs.1-finish"))) return;
        if (line2.contains("Purchase")) {
            if (!player.hasPermission("jc.signs.purchase")) {
                player.sendMessage(prefix + perm);
                return;
            }
            if (balance >= cost) {
                plugin.economy.withdrawPlayer(player, cost);
                player.sendMessage(prefix + plugin.getConfig().getString("msg-success") + " §7(-" + symbol + cost + ")");
                plugin.getCar(player, 1, plugin.getConfig().getInt("default-speed"), plugin.getConfig().getInt("default-fuel"));
                return;
            }
            player.sendMessage(prefix + plugin.getConfig().getString("msg-funds") + " §7(" + symbol + balance + " / " + symbol + cost + ")");
            return;
        }
        ItemMeta toolMeta = player.getInventory().getItemInMainHand().getItemMeta();

        if (toolMeta == null) {
            player.sendMessage(prefix + plugin.getConfig().getString("msg-hold"));
            return;
        }

        String toolName = toolMeta.getDisplayName();
        List<String> lore = toolMeta.getLore();
        NamespacedKey isCar = new NamespacedKey(plugin, "JacksCars-car");
        toolMeta.getPersistentDataContainer().get(isCar, PersistentDataType.STRING);
        String isACar = toolMeta.getPersistentDataContainer().get(isCar, PersistentDataType.STRING);

        if (isACar == null) {
            player.sendMessage(prefix + plugin.getConfig().getString("msg-hold"));
            return;
        }

        String levelStr = lore.get(1);
        String levelRep = levelStr.substring(levelStr.lastIndexOf(' ')+1);
        String fuelStr = lore.get(3);
        String fuelRep = fuelStr.substring(fuelStr.lastIndexOf(' ')+1);
        Integer level = Integer.parseInt(levelRep);
        Integer fuel = Integer.parseInt(fuelRep);
        Integer maxFuelThing = (plugin.getConfig().getInt("upgrade.fuel") * (level - 1) + (plugin.getConfig().getInt("default-fuel")));
        Boolean doPurchase = false;
        Boolean doRefuel = false;
        Boolean doUpgrade = false;

        switch (line2) {
            case "Upgrade":
                if (!player.hasPermission("jc.signs.upgrade")) {
                    player.sendMessage(prefix + perm);
                    return;
                }
                if (plugin.getConfig().getBoolean("upgrade-exp")) {
                    if (level >= plugin.getConfig().getInt("upgrade-cap")) {
                        player.sendMessage(prefix + plugin.getConfig().getString("msg-upgrade-cap"));
                        return;
                    }
                    Double exp = plugin.getConfig().getDouble("upgrade-exp-amt");
                    for (int i = level - 1; i > 0; i--) {
                        cost = Math.pow(cost, exp);
                    }
                    cost = Math.floor(cost * 100) / 100;
                    if (!confirmUpgrade.contains(player)) {
                        player.sendMessage(prefix + plugin.getConfig().getString("msg-upgrade-query") + " " + symbol + cost);
                        player.sendMessage(plugin.getConfig().getString("msg-upgrade-confirm"));
                        doCost = false;
                        confirmUpgrade.add(player);
                        new BukkitRunnable() {
                            @Override
                            public void run() {confirmUpgrade.remove(player);}
                        }.runTaskLater(plugin, 100);
                    }
                    else {
                        doUpgrade = true;
                        confirmUpgrade.remove(player);
                    }

                }
                break;
            case "Refuel":
                if (!player.hasPermission("jc.signs.refuel")) {
                    player.sendMessage(prefix + perm);
                    return;
                }
                if (fuel >= maxFuelThing) {
                    player.sendMessage(prefix + plugin.getConfig().getString("msg-refuel-full"));
                    return;
                }
                doRefuel = true;
                break;
        }
        if (doCost) {
            if (balance < cost) {
                player.sendMessage(prefix + plugin.getConfig().getString("msg-funds") + " §7(" + symbol + balance + " / " + symbol + cost + ")");
                return;
            }
            else {
                plugin.economy.withdrawPlayer(player, cost);
                player.sendMessage(prefix + plugin.getConfig().getString("msg-success") + " §7(-" + symbol + cost + ")");
                if (doUpgrade) {
                    level++;
                    Integer newSpeed = Integer.parseInt(lore.get(2).substring(lore.get(2).lastIndexOf(" ")+1)) + plugin.getConfig().getInt("upgrade.speed");
                    Integer newFuel = Integer.parseInt(lore.get(3).substring(lore.get(3).lastIndexOf(" ")+1)) + plugin.getConfig().getInt("upgrade.fuel");
                    lore.set(1, plugin.getConfig().getString("car-lore.level") + " " + level);
                    lore.set(2, plugin.getConfig().getString("car-lore.speed") + " " + newSpeed);
                    lore.set(3, plugin.getConfig().getString("car-lore.fuel") + " " + newFuel);
                    toolMeta.setLore(lore);
                    player.getInventory().getItemInMainHand().setItemMeta(toolMeta);
                }
                if (doRefuel) {
                    lore.set(3, plugin.getConfig().getString("car-lore.fuel") + " " + maxFuelThing);
                    toolMeta.setLore(lore);
                    player.getInventory().getItemInMainHand().setItemMeta(toolMeta);
                }
            }
        }


    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        String prefix = "";
        if (plugin.getConfig().getBoolean("is-prefix-set")) prefix = plugin.getConfig().getString("prefix") + " ";
        String line1 = ChatColor.stripColor(event.getLine(0));
        String line1check = ChatColor.stripColor(plugin.getConfig().getString("signs.1-check"));
        String line1fin = plugin.getConfig().getString("signs.1-finish");
        String line2 = ChatColor.stripColor(event.getLine(1));
        boolean line2check = false;
        String line4 = ChatColor.stripColor(event.getLine(3));
        double price = 0.00;
        String perm = plugin.getConfig().getString("msg-permission");
        String usage = plugin.getConfig().getString("msg-usage");
        Player player = event.getPlayer();
        List<String> types = new ArrayList<>();
        types.add("Purchase");
        types.add("Refuel");
        types.add("Upgrade");

        if (!(line1.equalsIgnoreCase(line1check))) return;
        if (!(player.hasPermission("jc.signs"))) {
            player.sendMessage(perm);
            return;
        }

        for (String checkType : types) {
            if (line2.equalsIgnoreCase(checkType)) {
                event.setLine(1, checkType);
                line2check = true;
                switch(checkType) {
                    case "Purchase":
                        price = plugin.getConfig().getDouble("cost.new");
                        break;
                    case "Refuel":
                        price = plugin.getConfig().getDouble("cost.refuel");
                        break;
                    case "Upgrade":
                        price = plugin.getConfig().getDouble("cost.upgrade-start");
                        if (plugin.getConfig().getBoolean("upgrade-exp")) {
                            event.setLine(2, plugin.getConfig().getString("signs.4-color") + "Starting Price:");
                        }
                        break;
                }

            }
        }
        if (!line2check) {
            player.sendMessage(usage);
            return;
        }
        if (!(line4.isEmpty())) {
            if (plugin.isNumeric(line4)) {
                price = Double.parseDouble(line4);
                player.sendMessage(prefix + plugin.getConfig().getString("msg-sign.double-correct"));
            }
            else {
                player.sendMessage(prefix + plugin.getConfig().getString("msg-sign.double-error"));
            }
        }

        if (!(event.getLine(2).contains(plugin.getConfig().getString("signs.4-color")))) event.setLine(2, "");
        event.setLine(0, line1fin);
        event.setLine(3, plugin.getConfig().getString("signs.4-color") + plugin.getConfig().getString("symbol") + price);

    }

}
