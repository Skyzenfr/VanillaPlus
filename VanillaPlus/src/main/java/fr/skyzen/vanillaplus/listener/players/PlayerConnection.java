package fr.skyzen.vanillaplus.listener.players;

import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnection implements Listener {

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Met à jour la tablist et autres informations
        Messages.updatePlayerInfo(player);

        // Gestion de l'invulnérabilité du joueur
        player.setInvulnerable(false);

        // Supprime le message par défaut du join
        event.setJoinMessage(null);

        // Envoi du message de connexion
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        String joinMessage = Players.getPlayerName(player) + ChatColor.GRAY + " vient de rejoindre le serveur "
                + ChatColor.GREEN + "(" + onlinePlayers + "/" + maxPlayers + ")";
        Bukkit.broadcastMessage(joinMessage);

        // Message de bienvenue personnalisé
        Messages.welcomeMessage(player);
    }
}
