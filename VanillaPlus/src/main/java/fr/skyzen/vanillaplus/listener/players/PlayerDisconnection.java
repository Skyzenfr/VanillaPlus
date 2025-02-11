package fr.skyzen.vanillaplus.listener.players;

import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.TeamsTagsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnection implements Listener {

    @EventHandler
    public void playerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        Cooldown.removeCooldown(player, "afk");

        if (TeamsTagsManager.hasPlayerNameTag(player))
            TeamsTagsManager.removePlayerNameTag(player);

        player.setInvulnerable(false);
        event.setQuitMessage(null);
        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GRAY + " vient de quitter le serveur " + ChatColor.RED + "(" + (Bukkit.getOnlinePlayers().size() - 1) + "/" + Bukkit.getMaxPlayers() + ")");
    }
}