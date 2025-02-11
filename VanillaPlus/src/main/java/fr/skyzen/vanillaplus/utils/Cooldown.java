package fr.skyzen.vanillaplus.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Cooldown {
    private static final HashMap<String, HashMap<UUID, Long>> cooldowns = new HashMap<>();

    /**
     * Définit un cooldown pour un joueur.
     *
     * @param player Le joueur
     * @param key La clé du cooldown (par exemple, "commande.tp")
     * @param seconds La durée du cooldown en secondes
     */
    public static void setCooldown(Player player, String key, int seconds) {
        if (!cooldowns.containsKey(key)) {
            cooldowns.put(key, new HashMap<>());
        }
        long expireTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        cooldowns.get(key).put(player.getUniqueId(), expireTime);
    }

    /**
     * Vérifie si un joueur a un cooldown actif.
     *
     * @param player Le joueur
     * @param key La clé du cooldown
     * @return true si le joueur a un cooldown actif, false sinon
     */
    public static boolean hasCooldown(Player player, String key) {
        if (!cooldowns.containsKey(key)) {
            return false;
        }

        HashMap<UUID, Long> cooldownMap = cooldowns.get(key);
        if (!cooldownMap.containsKey(player.getUniqueId())) {
            return false;
        }

        return cooldownMap.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    /**
     * Obtient le temps restant du cooldown en secondes.
     *
     * @param player Le joueur
     * @param key La clé du cooldown
     * @return Le temps restant en secondes, ou 0 s'il n'y a pas de cooldown
     */
    public static long getCooldownTimeLeft(Player player, String key) {
        if (!hasCooldown(player, key)) {
            return 0;
        }

        long timeLeft = cooldowns.get(key).get(player.getUniqueId()) - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toSeconds(timeLeft);
    }

    /**
     * Envoie un message au joueur avec le temps de cooldown restant.
     *
     * @param player Le joueur
     * @param key La clé du cooldown
     * @param message Le message à envoyer (utilisez {time} pour le temps restant et {unit} pour l'unité).
     */
    public static void sendCooldownMessage(Player player, String key, String message) {
        if (!hasCooldown(player, key)) {
            return;
        }

        long timeLeft = getCooldownTimeLeft(player, key);
        String timeUnit = timeLeft <= 1 ? "seconde" : "secondes";

        String formattedMessage = message
                .replace("{time}", String.valueOf(timeLeft))
                .replace("{unit}", timeUnit);

        player.sendMessage(formattedMessage);
    }

    /**
     * Supprime le cooldown d'un joueur.
     *
     * @param player Le joueur
     * @param key La clé du cooldown
     */
    public static void removeCooldown(Player player, String key) {
        if (cooldowns.containsKey(key)) {
            cooldowns.get(key).remove(player.getUniqueId());
        }
    }

    /**
     * Supprime tous les cooldowns d'un joueur.
     *
     * @param player Le joueur
     */
    public static void removeAllCooldowns(Player player) {
        for (HashMap<UUID, Long> cooldownMap : cooldowns.values()) {
            cooldownMap.remove(player.getUniqueId());
        }
    }
}