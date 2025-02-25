package fr.skyzen.vanillaplus.listener.world;

import java.util.Arrays;
import java.util.List;

import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlocker implements Listener {
    private final List<String> blockedCommands = Arrays.asList(
            "/bukkit",
            "/plugins",
            "/pl",
            "/about",
            "/ver",
            "/version",
            "/icanhasbukkit",
            "/?",
            "/me",
            "/tell",
            "/w",
            "/minecraft:",
            "/paper:",
            "/spigot:",
            "/timings",
            "/deep",
            "/seed"
    );

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].toLowerCase();
        Bukkit.getServer().getConsoleSender().sendMessage(Messages.info + Players.getPlayerName(player) + ChatColor.GRAY + " a fait la commande: " + ChatColor.AQUA + command);
        if (this.blockedCommands.contains(command)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Commande inconnue.");
        }
    }
}
