package fr.skyzen.vanillaplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamsTagsManager {
    private final String prefix;
    private final String suffix;

    public TeamsTagsManager(String name, String prefix, String suffix, Scoreboard current) throws Exception {
        this.prefix = prefix;
        this.suffix = suffix;
        Team team = current.getTeam(name);
        if (team == null) {
            team = current.registerNewTeam(name);
        }

        team.setCanSeeFriendlyInvisibles(false);
        team.setAllowFriendlyFire(false);
        int prefixLength = 0;
        int suffixLength = 0;
        if (prefix != null) {
            prefixLength = prefix.length();
        }

        if (suffix != null) {
            suffixLength = suffix.length();
        }

        if (prefixLength + suffixLength >= 32) {
            throw new Exception("prefix and suffix lengths are greater than 16");
        } else {
            if (suffix != null) {
                team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));
            }

            if (prefix != null) {
                team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
            }

        }
    }

    public TeamsTagsManager(String name, String prefix, String suffix) throws Exception {
        this(name, prefix, suffix, getNonNullMainScoreboard());
    }

    public void set(Player player) {
        setPlayerNameTag(player, this.prefix, this.suffix);
    }

    public static void setPlayerNameTag(Player player, String prefix, String suffix) {
        if (Bukkit.getScoreboardManager() != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());
            if (team == null) {
                team = scoreboard.registerNewTeam(player.getName());
            }

            team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
            team.setSuffix(ChatColor.translateAlternateColorCodes('&', suffix));
            team.setCanSeeFriendlyInvisibles(false);
            team.setAllowFriendlyFire(false);
            team.addEntry(player.getName());
        }

    }

    public static void removePlayerNameTag(Player player) {
        if (Bukkit.getScoreboardManager() != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());
            if (team != null) {
                team.unregister();
            }

            ConsoleCommandSender var10000 = Bukkit.getServer().getConsoleSender();
            ChatColor var10001 = ChatColor.DARK_BLUE;
            var10000.sendMessage(var10001 + "NameTag " + ChatColor.GRAY + "Le nametag de " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " a bien été réinitialisé");
        }

    }

    public static void removeAllPlayerNameTags() {
        if (Bukkit.getScoreboardManager() != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

            for (Team team : scoreboard.getTeams()) {
                team.unregister();
            }

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_BLUE + "NameTag " + ChatColor.GRAY + "Les nametags ont bien été supprimé");
        }

    }

    public static boolean hasPlayerNameTag(Player player) {
        if (Bukkit.getScoreboardManager() != null) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(player.getName());
            return team != null;
        } else {
            return false;
        }
    }

    private static Scoreboard getNonNullMainScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        assert scoreboardManager != null;

        return scoreboardManager.getMainScoreboard();
    }
}
