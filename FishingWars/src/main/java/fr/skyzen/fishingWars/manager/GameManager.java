package fr.skyzen.fishingWars.manager;

import fr.skyzen.fishingWars.utils.Countdown;
import fr.skyzen.fishingWars.utils.GameStatus;
import fr.skyzen.fishingWars.utils.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.*;
import java.util.*;

public class GameManager {
    private GameStatus status;
    private final Set<Player> players = new HashSet<>();
    private final KitManager kitManager;

    // 📌 Coordonnées du lobby (modifier selon ton serveur)
    private final Location lobbyLocation = Bukkit.getWorld("world").getSpawnLocation();

    // 📌 Liste des points de spawn pour la partie (modifier selon ton arène)
    private final List<Location> spawnLocations = Arrays.asList(
            new Location(Bukkit.getWorld("world"), 200, 65, 200),
            new Location(Bukkit.getWorld("world"), 210, 65, 210),
            new Location(Bukkit.getWorld("world"), 220, 65, 220),
            new Location(Bukkit.getWorld("world"), 230, 65, 230),
            new Location(Bukkit.getWorld("world"), 240, 65, 240),
            new Location(Bukkit.getWorld("world"), 250, 65, 250),
            new Location(Bukkit.getWorld("world"), 260, 65, 260),
            new Location(Bukkit.getWorld("world"), 270, 65, 270)
    );

    public GameManager(KitManager kitManager) {
        this.status = GameStatus.WAITING;
        this.kitManager = kitManager;
    }

    public void addPlayer(Player player) {
        players.add(player);
        player.teleport(lobbyLocation); // Téléporte le joueur au lobby
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public GameStatus getStatus() {
        return status;
    }

    public void startGame() {
//        if (getPlayerCount() < 2) {
//            Bukkit.broadcastMessage(ChatColor.RED + "Pas assez de joueurs pour démarrer !");
//            return;
//        }

        status = GameStatus.STARTING;
        Bukkit.broadcastMessage(ChatColor.GREEN + "La partie commence dans 10 secondes !");

        new Countdown(10, () -> {
            status = GameStatus.IN_GAME;
            Bukkit.broadcastMessage(ChatColor.GOLD + "GO ! La partie commence !");
            teleportPlayersToArena();
        }).start();
    }

    private void teleportPlayersToArena() {
        int i = 0;
        for (Player player : players) {
            player.teleport(spawnLocations.get(i));
            kitManager.giveKit(player);
            player.sendTitle(ChatColor.GOLD + "GO !", ChatColor.RED + "Combattez !", 10, 40, 10);
            i++;
            if (i >= spawnLocations.size()) i = 0;
        }
    }
}
