package fr.skyzen.fishingWars.utils.scoreboard;

import fr.skyzen.fishingWars.FishingWars;
import fr.skyzen.fishingWars.manager.GameManager;
import fr.skyzen.fishingWars.utils.GameStatus;
import fr.skyzen.fishingWars.utils.kits.KitManager;
import fr.skyzen.fishingWars.utils.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard {
    private static GameManager gameManager;
    private static KitManager kitManager;
    private static final String mapName = "Île Mystérieuse"; // Tu peux changer ça dynamiquement plus tard

    public LobbyScoreboard(GameManager gameManager, KitManager kitManager) {
        LobbyScoreboard.gameManager = gameManager;
        LobbyScoreboard.kitManager = kitManager;
    }

    public void setLobbyScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("lobby", "dummy", ChatColor.GOLD + "" + ChatColor.BOLD + "Fishing Wars");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateScoreboard(player, scoreboard, objective);

        player.setScoreboard(scoreboard);

        // Mise à jour régulière du scoreboard
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || gameManager.getStatus() != GameStatus.WAITING) {
                    cancel();
                    return;
                }
                updateScoreboard(player, scoreboard, objective);
            }
        }.runTaskTimer(FishingWars.getInstance(), 0, 20); // Mise à jour toutes les secondes
    }

    public static void updateScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
        scoreboard.getEntries().forEach(scoreboard::resetScores);

        KitType kitType = kitManager.getPlayerKit(player);
        int playerCount = gameManager.getPlayerCount();
        GameStatus status = gameManager.getStatus();

        objective.getScore(ChatColor.YELLOW + "Carte: " + ChatColor.AQUA + mapName).setScore(4);
        objective.getScore(ChatColor.YELLOW + "Kit: " + ChatColor.GREEN + kitType.name()).setScore(3);
        objective.getScore(ChatColor.YELLOW + "Joueurs: " + ChatColor.RED + playerCount).setScore(2);
        objective.getScore(ChatColor.YELLOW + "Statut: " + ChatColor.LIGHT_PURPLE + status.name()).setScore(1);
    }
}
