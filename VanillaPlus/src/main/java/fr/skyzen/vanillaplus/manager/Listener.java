package fr.skyzen.vanillaplus.manager;

import fr.skyzen.vanillaplus.VanillaPlus;
import fr.skyzen.vanillaplus.commands.Warp;
import fr.skyzen.vanillaplus.listener.Market;
import fr.skyzen.vanillaplus.listener.entity.Entity;
import fr.skyzen.vanillaplus.listener.players.*;
import fr.skyzen.vanillaplus.listener.world.*;
import fr.skyzen.vanillaplus.utils.gui.NickNameGUI;
import fr.skyzen.vanillaplus.utils.gui.StatistiquesGUI;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public class Listener {

    private final VanillaPlus plugin;

    public Listener(VanillaPlus plugin) {
        this.plugin = plugin;
    }

    public void registerListeners() {
        List<org.bukkit.event.Listener> listeners = Arrays.asList(
                plugin,
                new Warp(),
                new Entity(),
                new PlayerAnother(),
                new PlayerConnection(),
                new PlayerDisconnection(),
                new WorldListener(),
                new ServerListPing(),
                new CommandBlocker(),
                new StatistiquesGUI(),
                new Market(),
                new Economy(),
                new NickNameGUI()
        );

        listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, plugin));
    }
}
