package fr.skyzen.vanillaplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class PersistentData {

    private static JavaPlugin plugin;

    // 🔹 Initialisation avec le plugin principal
    public static void init(JavaPlugin mainPlugin) {
        plugin = mainPlugin;
    }

    // 🔹 Obtenir un container PDC pour un joueur (en ligne uniquement)
    private static Optional<PersistentDataContainer> getPlayerPDC(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return Optional.ofNullable(player).map(Player::getPersistentDataContainer);
    }

    // 🔹 Obtenir une donnée persistante (détection automatique en ligne/hors ligne)
    public static <T, Z> Z getPersistentData(UUID playerUUID, String key, PersistentDataType<T, Z> type, Z defaultValue) {
        return getPlayerPDC(playerUUID)
                .filter(pdc -> pdc.has(new NamespacedKey(plugin, key), type))
                .map(pdc -> pdc.get(new NamespacedKey(plugin, key), type))
                .orElse(defaultValue);
    }

    // 🔹 Définir une donnée persistante (uniquement pour les joueurs en ligne)
    public static <T, Z> void setPersistentData(UUID playerUUID, String key, PersistentDataType<T, Z> type, Z value) {
        getPlayerPDC(playerUUID).ifPresentOrElse(
                pdc -> pdc.set(new NamespacedKey(plugin, key), type, value),
                () -> Bukkit.getLogger().log(Level.WARNING, "Impossible de définir '" + key + "' : Joueur hors ligne.")
        );
    }

    // 🔹 Supprimer une donnée persistante
    public static void removePersistentData(UUID playerUUID, String key) {
        getPlayerPDC(playerUUID).ifPresentOrElse(
                pdc -> pdc.remove(new NamespacedKey(plugin, key)),
                () -> Bukkit.getLogger().log(Level.WARNING, "Impossible de supprimer '" + key + "' : Joueur hors ligne.")
        );
    }

    // 🔹 Vérifier si une donnée existe
    public static boolean hasPersistentData(UUID playerUUID, String key, PersistentDataType<?, ?> type) {
        return getPlayerPDC(playerUUID)
                .map(pdc -> pdc.has(new NamespacedKey(plugin, key), type))
                .orElse(false);
    }
}
