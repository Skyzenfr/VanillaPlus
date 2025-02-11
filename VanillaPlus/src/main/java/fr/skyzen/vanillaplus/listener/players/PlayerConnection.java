package fr.skyzen.vanillaplus.listener.players;

import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

        // Configuration de la tablist (Header & Footer)
        String header = LegacyComponentSerializer.legacySection().serialize(Component.text(Messages.tab_header));
        String footer = LegacyComponentSerializer.legacySection().serialize(Component.text(Messages.tab_footer));

        if (!header.isEmpty() && !footer.isEmpty()) {
            player.setPlayerListHeaderFooter(header, footer);
        }

        // Met à jour la tablist et autres informations
        Players.updateTabList(player);

        // Gestion de l'invulnérabilité du joueur
        player.setInvulnerable(false);

        // Supprime le message par défaut du join
        event.setJoinMessage(null);

        // Envoi du message de connexion
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int maxPlayers = Bukkit.getMaxPlayers();
        String player_name = player.isOp() ? ChatColor.GOLD + "✦ " + player.getName() + " (VIP)" : ChatColor.GREEN + player.getName() ;
        String joinMessage = player_name + ChatColor.GRAY + " vient de rejoindre le serveur "
                + ChatColor.GREEN + "(" + onlinePlayers + "/" + maxPlayers + ")";
        Bukkit.broadcastMessage(joinMessage);

        // Message de bienvenue personnalisé
        Messages.welcomeMessage(player);
    }
}
