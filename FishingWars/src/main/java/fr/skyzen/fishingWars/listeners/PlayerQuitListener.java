package fr.skyzen.fishingWars.listeners;

import fr.skyzen.fishingWars.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final GameManager gameManager;

    public PlayerQuitListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        // Désactive le message par défaut
        event.setQuitMessage("");

        gameManager.removePlayer(event.getPlayer());

        // 📢 Message global annonçant le départ
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " a quitté la partie ! "
                + ChatColor.GRAY + "[" + gameManager.getPlayerCount() + "/8]");
    }
}