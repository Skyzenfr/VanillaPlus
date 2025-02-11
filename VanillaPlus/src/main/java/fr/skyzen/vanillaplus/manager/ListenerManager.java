package fr.skyzen.vanillaplus.manager;

import fr.skyzen.vanillaplus.VanillaPlus;
import fr.skyzen.vanillaplus.commands.Warp;
import fr.skyzen.vanillaplus.listener.MarketListener;
import fr.skyzen.vanillaplus.listener.entity.Entity;
import fr.skyzen.vanillaplus.listener.players.*;
import fr.skyzen.vanillaplus.listener.world.*;
import fr.skyzen.vanillaplus.utils.gui.StatistiquesGUI;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class ListenerManager {

    private final VanillaPlus plugin;

    public ListenerManager(VanillaPlus plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        List<Listener> listeners = Arrays.asList(
                plugin,
                new Warp(),
                new Entity(),
                new PlayerAnother(),
                new PlayerConnection(),
                new PlayerDisconnection(),
                new World(),
                new ServerListPing(),
                new CommandBlocker(),
                new StatistiquesGUI(),
                new MarketListener()
        );

        listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
    }
}
