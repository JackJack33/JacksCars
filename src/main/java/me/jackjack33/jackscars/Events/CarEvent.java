package me.jackjack33.jackscars.Events;

import me.jackjack33.jackscars.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getOfflinePlayer;

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

        MetadataValue s = event.getVehicle().getMetadata("speed").get(0);
        Integer currentSpeed = s.asInt();

        carVelocity.setX((playerLocationVelocity.getX() / 100) * currentSpeed);
        carVelocity.setZ((playerLocationVelocity.getZ() / 100) * currentSpeed);

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
        checkName = ChatColor.stripColor(checkName).toLowerCase();
        String actualName = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
        if (!(checkName.equalsIgnoreCase(actualName))) return;
        player.sendMessage("check");
        if (block.equals(Material.RAIL) || block.equals(Material.POWERED_RAIL) || block.equals(Material.DETECTOR_RAIL) || block.equals(Material.ACTIVATOR_RAIL)) {
            event.setCancelled(true);
            return;
        }
        //player.getInventory().getItemInMainHand().setAmount(item.getAmount()-1);
        Minecart minecart = (Minecart) player.getWorld().spawnEntity(event.getClickedBlock().getLocation().add(0, 1, 0), EntityType.MINECART);
        minecart.setMaxSpeed(1000D);
        ItemStack tool = player.getInventory().getItemInMainHand();
        List<String> lore = tool.getItemMeta().getLore();
        String[] getOwnerList = lore.get(0).split(" ");
        String[] getLevelList = lore.get(1).split(" ");
        String[] getSpeedList = lore.get(2).split(" ");
        String[] getFuelList = lore.get(3).split(" ");
        String ownerStr = getOwnerList[getOwnerList.length - 1];
        int level = Integer.parseInt(getLevelList[getLevelList.length - 1]);
        int speed = Integer.parseInt(getSpeedList[getSpeedList.length - 1]);
        int fuel = Integer.parseInt(getFuelList[getFuelList.length - 1]);
        OfflinePlayer owner = getOfflinePlayer(ownerStr);
        UUID ownerUUID = owner.getUniqueId();
        player.sendMessage("level" + level + "speed" + speed + "fuel" + fuel);
        minecart.setMetadata("owner", new FixedMetadataValue(plugin, owner));
        minecart.setMetadata("ownerUUID", new FixedMetadataValue(plugin, ownerUUID));
        minecart.setMetadata("level", new FixedMetadataValue(plugin, level));
        minecart.setMetadata("speed", new FixedMetadataValue(plugin, speed));
        minecart.setMetadata("fuel", new FixedMetadataValue(plugin, fuel));
    }

    @EventHandler
    public void onCarBreak(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) return;
        Minecart minecart = (Minecart)event.getVehicle();
    }
}
