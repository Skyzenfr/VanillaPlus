package fr.skyzen.fishingWars.listeners;

import fr.skyzen.fishingWars.manager.GameManager;
import fr.skyzen.fishingWars.utils.GameStatus;
import fr.skyzen.fishingWars.utils.scoreboard.LobbyScoreboard;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class PlayerJoinListener implements Listener {
    private final GameManager gameManager;
    private final LobbyScoreboard lobbyScoreboard;

    public PlayerJoinListener(GameManager gameManager, LobbyScoreboard lobbyScoreboard) {
        this.gameManager = gameManager;
        this.lobbyScoreboard = lobbyScoreboard;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(""); // Désactive le message par défaut

        if (gameManager.getStatus() != GameStatus.WAITING) {
            player.sendMessage(ChatColor.RED + "La partie a déjà commencé, tu es en mode spectateur.");
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        gameManager.addPlayer(player);
        giveGameItems(player);
        player.setGameMode(GameMode.ADVENTURE);


        // 📢 Message global annonçant l'arrivée du joueur
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " a rejoint la partie ! "
                + ChatColor.GRAY + "[" + gameManager.getPlayerCount() + "/8]");
        sendWelcomeMessage(player);

        // ✅ Vérification supplémentaire pour éviter l’erreur
        if (lobbyScoreboard != null) {
            lobbyScoreboard.setLobbyScoreboard(player);
        } else {
            player.sendMessage(ChatColor.RED + "Erreur: Scoreboard non disponible !");
        }
    }

    private void giveGameItems(Player player) {
        // Item pour quitter
        ItemStack quitItem = new ItemStack(Material.RED_BED);
        ItemMeta quitMeta = quitItem.getItemMeta();
        if (quitMeta != null) {
            quitMeta.setDisplayName(ChatColor.RED + "Quitter la partie");
            quitItem.setItemMeta(quitMeta);
        }

        // Item pour choisir un kit
        ItemStack kitSelector = new ItemStack(Material.COMPASS);
        ItemMeta kitMeta = kitSelector.getItemMeta();
        if (kitMeta != null) {
            kitMeta.setDisplayName(ChatColor.GREEN + "Sélectionner un kit");
            kitSelector.setItemMeta(kitMeta);
        }

        // Ajout des items dans l'inventaire du joueur
        player.getInventory().clear();
        // Placement des items
        player.getInventory().setItem(4, kitSelector); // 🎒 Kit au milieu
        player.getInventory().setItem(8, quitItem); // 🛏 Quitter tout à droite
    }

    private void sendWelcomeMessage(Player player) {
        String mapName = "Île Mystérieuse"; // À changer si tu veux que ce soit dynamique
        int playersInGame = gameManager.getPlayerCount();
        String gameStatus = gameManager.getStatus().name();

        player.sendMessage(ChatColor.AQUA + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage(ChatColor.GOLD + "Bienvenue sur " + ChatColor.BOLD + "Fishing Wars" + ChatColor.RESET + ", " + ChatColor.YELLOW + player.getName() + " !");
        player.sendMessage(ChatColor.GREEN + "⚔ Prépare-toi pour l'affrontement ! ⚔");
        player.sendMessage(ChatColor.WHITE + "Carte actuelle : " + ChatColor.BLUE + mapName);
        player.sendMessage(ChatColor.WHITE + "Joueurs en attente : " + ChatColor.RED + playersInGame + "/8");
        player.sendMessage(ChatColor.WHITE + "Statut du jeu : " + ChatColor.LIGHT_PURPLE + gameStatus);
        player.sendMessage(ChatColor.AQUA + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}
