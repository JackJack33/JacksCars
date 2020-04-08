package me.jackjack33.jackscars.Events;

import me.jackjack33.jackscars.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CarEvent implements Listener {

    private Main plugin;
    public CarEvent(Main plugin) {
        this.plugin = plugin;
    }

    public HashMap<Player, Integer> speed = new HashMap<>();

    @EventHandler
    public void onCarEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Minecart minecart = (Minecart)event.getVehicle();
    }

    @EventHandler
    public void onCarUpdate(VehicleUpdateEvent event) {
        if (event.getVehicle().getPassengers().size() == 0) return;
        Player p = (Player)event.getVehicle().getPassengers().get(0);

        Vector carVelocity = event.getVehicle().getVelocity();
        Vector playerLocationVelocity = p.getLocation().getDirection();

        NamespacedKey speedKey = new NamespacedKey(plugin, "JacksCars-speed");
        boolean useRoads = plugin.getConfig().getBoolean("custom-block.road-block.enabled");
        double currentSpeed = event.getVehicle().getPersistentDataContainer().get(speedKey, PersistentDataType.INTEGER);
        double preSpeed = currentSpeed;
        String drivingText = plugin.getConfig().getString("driving-speed");

        List<String> roadBlocks = plugin.getConfig().getStringList("custom-blocks.road-block.block");
        double reducedPercent = plugin.getConfig().getInt("custom-blocks.road-block.slow-percent");
        reducedPercent = reducedPercent/100;

        List<Material> road = new ArrayList<>();
        for (String block : roadBlocks) {
            Material material = Material.getMaterial(block);
            road.add(material);
        }

        if (!(road.contains(event.getVehicle().getLocation().add(0, -1, 0).getBlock().getType()))) currentSpeed = currentSpeed * (1 - reducedPercent);

        if (!useRoads) currentSpeed = preSpeed;

        carVelocity.setX((playerLocationVelocity.getX() / 100) * currentSpeed);
        carVelocity.setZ((playerLocationVelocity.getZ() / 100) * currentSpeed);

        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(drivingText+currentSpeed));

        event.getVehicle().setVelocity(carVelocity);
    }

    @EventHandler
    public void onCarCreate(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Material block = event.getClickedBlock().getType();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta==null) return;

        String checkName = plugin.getConfig().getString("car-name");
        if (checkName == null) return;

        checkName = ChatColor.stripColor(checkName).toLowerCase();
        String actualName = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
        if (!(checkName.equalsIgnoreCase(actualName))) return;

        if (block.equals(Material.RAIL) || block.equals(Material.POWERED_RAIL) || block.equals(Material.DETECTOR_RAIL) || block.equals(Material.ACTIVATOR_RAIL)) {
            event.setCancelled(true);
            return;
        }

        player.getInventory().getItemInMainHand().setAmount(item.getAmount()-1);

        Minecart minecart = (Minecart) player.getWorld().spawnEntity(event.getClickedBlock().getLocation().add(0, 1, 0), EntityType.MINECART);
        minecart.setMaxSpeed(1000D);

        List<String> lore = meta.getLore();
        if (lore == null || lore.size() < 4) return;

        String[] getOwnerList = lore.get(0).split(" ");
        String[] getLevelList = lore.get(1).split(" ");
        String[] getSpeedList = lore.get(2).split(" ");
        String[] getFuelList = lore.get(3).split(" ");

        String ownerStr = getOwnerList[getOwnerList.length - 1];
        int level = Integer.parseInt(getLevelList[getLevelList.length - 1]);
        int speed = Integer.parseInt(getSpeedList[getSpeedList.length - 1]);
        int fuel = Integer.parseInt(getFuelList[getFuelList.length - 1]);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerStr);
        UUID ownerUUID = owner.getUniqueId();
        String ownerName = owner.getName();
        if (ownerName==null) return;

        NamespacedKey ownerKey = new NamespacedKey(plugin, "JacksCars-owner");
        minecart.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, ownerName);
        NamespacedKey ownerUUIDKey = new NamespacedKey(plugin, "JacksCars-ownerUUID");
        minecart.getPersistentDataContainer().set(ownerUUIDKey, PersistentDataType.STRING, ownerUUID.toString());
        NamespacedKey levelKey = new NamespacedKey(plugin, "JacksCars-level");
        minecart.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
        NamespacedKey speedKey = new NamespacedKey(plugin, "JacksCars-speed");
        minecart.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, speed);
        NamespacedKey fuelKey = new NamespacedKey(plugin, "JacksCars-fuel");
        minecart.getPersistentDataContainer().set(fuelKey, PersistentDataType.INTEGER, fuel);
    }

    @EventHandler
    public void onCarBreak(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) return;
        Minecart minecart = (Minecart)event.getVehicle();
        Player player = (Player)event.getAttacker();

        NamespacedKey ownerKey = new NamespacedKey(plugin, "JacksCars-owner");
        NamespacedKey ownerUUIDKey = new NamespacedKey(plugin, "JacksCars-ownerUUID");
        NamespacedKey levelKey = new NamespacedKey(plugin, "JacksCars-level");
        NamespacedKey speedKey = new NamespacedKey(plugin, "JacksCars-speed");
        NamespacedKey fuelKey = new NamespacedKey(plugin, "JacksCars-fuel");
        PersistentDataContainer container = event.getVehicle().getPersistentDataContainer();
        String saveOwner = container.get(ownerKey, PersistentDataType.STRING);
        UUID saveOwnerUUID = UUID.fromString(container.get(ownerUUIDKey, PersistentDataType.STRING));
        Integer saveLevel = container.get(levelKey, PersistentDataType.INTEGER);
        Integer saveSpeed = container.get(speedKey, PersistentDataType.INTEGER);
        Integer saveFuel = container.get(fuelKey, PersistentDataType.INTEGER);

        if (!(player.getUniqueId().equals(saveOwnerUUID))) {
            if (player.isOp()) {
                if (!(plugin.getConfig().getBoolean("ops-break"))) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getConfig().getString("msg-cant-pickup"));
                    return;
                }
            }
            else {
                event.setCancelled(true);
                player.sendMessage(plugin.getConfig().getString("msg-cant-pickup"));
                return;
            }
        }

        String ownerLine = plugin.getConfig().getString("car-lore.owner");
        String levelLine = plugin.getConfig().getString("car-lore.level");
        String speedLine = plugin.getConfig().getString("car-lore.speed");
        String fuelLine = plugin.getConfig().getString("car-lore.fuel");
        List<String> extra = plugin.getConfig().getStringList("car-lore.extra");

        List<String> lore = new ArrayList<>();
        lore.add(ownerLine + " " + saveOwner);
        lore.add(levelLine + " " + saveLevel);
        lore.add(speedLine + " " + saveSpeed);
        lore.add(fuelLine + " " + saveFuel);
        lore.addAll(extra);

        ItemStack cart = new ItemStack(Material.MINECART, 1);
        ItemMeta meta = cart.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(plugin.getConfig().getString("car-name"));
        meta.setLore(lore);
        cart.setItemMeta(meta);
        player.getInventory().addItem(cart);
    }
}
