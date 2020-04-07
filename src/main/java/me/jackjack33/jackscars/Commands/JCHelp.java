package me.jackjack33.jackscars.Commands;

import me.jackjack33.jackscars.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class JCHelp implements CommandExecutor {

    private Main plugin;
    public JCHelp(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("jchelp").setExecutor(this);

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String prefix = plugin.getConfig().getString("prefix");
            if (p.hasPermission("jc.help")) {
                if (plugin.getConfig().getBoolean("is-prefix-set")) {
                    p.sendMessage(prefix + " Help Menu");
                }
                List<String> rules = plugin.getConfig().getStringList("msg-help");
                for (String s2 : rules){
                    p.sendMessage(s2);
                }
            }
        }
        return false;
    }
}
