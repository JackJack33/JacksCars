package me.jackjack33.jackscars.Events;

import me.jackjack33.jackscars.Main;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignEvent implements Listener {

    private Main plugin;
    public SignEvent(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        if (!(event.getLine(1).equals(plugin.getConfig().getString("signs.1-check")))) return;
        event.setLine(1, plugin.getConfig().getString("sings.1-finish"));
    }

}
