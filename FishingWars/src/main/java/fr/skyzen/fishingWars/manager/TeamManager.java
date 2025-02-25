package fr.skyzen.fishingWars.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.*;

public class TeamManager {
    private final Map<Player, String> playerTeams = new HashMap<>();
    private final List<String> teams = Arrays.asList("Rouge", "Bleu");

    public void assignToBalancedTeam(Player player) {
        String team = getSmallestTeam();
        playerTeams.put(player, team);
        player.sendMessage(ChatColor.GREEN + "Tu as rejoint l'équipe " + getTeamColor(team) + team);
    }

    public void changeTeam(Player player, String team) {
        if (!teams.contains(team)) {
            player.sendMessage(ChatColor.RED + "Cette équipe n'existe pas !");
            return;
        }
        playerTeams.put(player, team);
        player.sendMessage(ChatColor.GREEN + "Tu as changé pour l'équipe " + getTeamColor(team) + team);
    }

    public String getTeam(Player player) {
        return playerTeams.getOrDefault(player, "SANS ÉQUIPE");
    }

    public String getSmallestTeam() {
        Map<String, Long> teamCount = new HashMap<>();
        for (String team : teams) {
            teamCount.put(team, playerTeams.values().stream().filter(t -> t.equals(team)).count());
        }
        return teamCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
    }

    public ChatColor getTeamColor(String team) {
        return switch (team) {
            case "Rouge" -> ChatColor.RED;
            case "Bleu" -> ChatColor.BLUE;
            default -> ChatColor.GRAY;
        };
    }

    public void removePlayer(Player player) {
        playerTeams.remove(player);
    }
}
