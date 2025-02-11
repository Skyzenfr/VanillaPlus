package fr.skyzen.vanillaplus.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;

public class PersistentDataUtil {

    private static JavaPlugin plugin;

    // Initialisation avec le plugin principal
    public static void init(JavaPlugin mainPlugin) {
        plugin = mainPlugin;
    }

    // 🔹 Récupérer une donnée persistante (supporte les joueurs offline)
    public static <T, Z> Z getPersistentData(UUID playerUUID, String key, PersistentDataType<T, Z> type, Z defaultValue) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) { // Joueur en ligne
            return getPersistentData(player, key, type, defaultValue);
        }
        return defaultValue; // ⚠ Impossible d'accéder aux données PDC d'un joueur offline
    }

    // 🔹 Vérifier si une donnée existe (supporte les joueurs offline)
    public static boolean hasPersistentData(UUID playerUUID, String key, PersistentDataType<?, ?> type) {
        Player player = Bukkit.getPlayer(playerUUID);
        return player != null && hasPersistentData(player, key, type);
    }

    // 🔹 Récupérer une donnée persistante (version pour joueurs en ligne uniquement)
    public static <T, Z> Z getPersistentData(Player player, String key, PersistentDataType<T, Z> type, Z defaultValue) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        return dataContainer.has(namespacedKey, type) ? dataContainer.get(namespacedKey, type) : defaultValue;
    }

    // 🔹 Définir une donnée persistante (version pour joueurs en ligne uniquement)
    public static <T, Z> void setPersistentData(Player player, String key, PersistentDataType<T, Z> type, Z value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(namespacedKey, type, value);
    }

    // 🔹 Supprimer une donnée persistante (version pour joueurs en ligne uniquement)
    public static void removePersistentData(Player player, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.remove(namespacedKey);
    }

    // 🔹 Vérifier si une donnée existe (version pour joueurs en ligne uniquement)
    public static boolean hasPersistentData(Player player, String key, PersistentDataType<?, ?> type) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        return dataContainer.has(namespacedKey, type);
    }
}

