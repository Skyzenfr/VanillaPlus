package fr.skyzen.vanillaplus.listener.world;

import fr.skyzen.vanillaplus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPing implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerPing(ServerListPingEvent event) {
        if (VanillaPlus.getInstance().getConfig().getString("motd.") != null){
            World world = Bukkit.getWorld("world");
            if (world != null){
                String weather = world.isClearWeather() ? "Ensoleillé" : "Pluvieux";
                String time = world.getTime() < 12000 ? "Jour" : "Nuit";
                String line2 = ChatColor.GRAY + "Temps: " + time + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + "Météo: " + weather;
                String motd = VanillaPlus.getInstance().getConfig().getString("motd.line1") + "\n" + line2;
                event.setMotd(motd);
            }
        } else{
            event.setMotd("Plugin crée par Skyzen");
        }
    }
}
