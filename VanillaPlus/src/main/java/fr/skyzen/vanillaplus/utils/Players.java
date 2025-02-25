package fr.skyzen.vanillaplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class Players {

    public static void savePlayerLevel(Player player) {
        int level = player.getLevel();
        PersistentData.setPersistentData(player.getUniqueId(), "player_level", PersistentDataType.INTEGER, level);
    }

    public static int getPlayerLevel(UUID playerUUID) {
        // Vérifie si le joueur est en ligne et retourne son niveau directement
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null && player.isOnline()) {
            return player.getLevel();
        }

        // Sinon, récupère le niveau stocké via PersistentData
        return PersistentData.getPersistentData(playerUUID, "player_level", PersistentDataType.INTEGER, 0);
    }

    public static String getPlayerName(Player player) {
        UUID uuid = player.getUniqueId();

        // 📌 Récupération des couleurs et icônes sélectionnées
        String selectedColor = PersistentData.getPersistentData(uuid, "selected_color", PersistentDataType.STRING, ChatColor.GRAY.toString());
        String selectedIcon = PersistentData.getPersistentData(uuid, "selected_icon", PersistentDataType.STRING, "");

        return selectedColor + (selectedIcon.isEmpty() ? "" : selectedIcon + " ") + player.getName();
    }

    public static String getPlayerHealth(Player player) {
        double health = player.getHealth();
        return ChatColor.RED + "❤ " + ChatColor.WHITE + (int) health + "/20" + ChatColor.RESET;
    }

    public static String getPlayerPing(Player player) {
        int ping = player.getPing();
        ChatColor color = (ping < 50) ? ChatColor.GREEN : (ping < 100) ? ChatColor.YELLOW : ChatColor.RED;
        return color + "" + ping + "ms" + ChatColor.RESET;
    }
}