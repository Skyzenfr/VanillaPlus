package fr.skyzen.vanillaplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class Money {

    private static final String MONEY_KEY = "money"; // Clé utilisée pour stocker la monnaie

    // 🔹 Obtenir un joueur optionnel (évite les null checks)
    private static Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    // 🔹 Récupérer le solde d'un joueur (détection automatique en ligne/hors ligne)
    public static double getMoney(UUID uuid) {
        return PersistentData.getPersistentData(uuid, MONEY_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    // 🔹 Définir le solde d'un joueur
    public static void setMoney(UUID uuid, double amount) {
        PersistentData.setPersistentData(uuid, MONEY_KEY, PersistentDataType.DOUBLE, Math.max(0, amount));
    }

    // 🔹 Ajouter de l'argent à un joueur
    public static void addMoney(UUID uuid, double amount) {
        setMoney(uuid, getMoney(uuid) + amount);
    }

    // 🔹 Retirer de l'argent d'un joueur
    public static boolean removeMoney(UUID uuid, double amount) {
        double currentMoney = getMoney(uuid);
        if (currentMoney < amount) {
            return false; // Pas assez d'argent
        }
        setMoney(uuid, currentMoney - amount);
        return true;
    }

    // 🔹 Vérifier si un joueur a au moins X money
    public static boolean hasMoney(UUID uuid, double amount) {
        return getMoney(uuid) >= amount;
    }

    // 🔹 Transférer de l'argent d'un joueur à un autre
    public static boolean transferMoney(UUID senderUUID, UUID receiverUUID, double amount) {
        if (!removeMoney(senderUUID, amount)) {
            Bukkit.getLogger().log(Level.WARNING, "Transaction échouée : " + amount + "€ de " + senderUUID + " vers " + receiverUUID + " (fonds insuffisants)");
            return false;
        }

        addMoney(receiverUUID, amount);

        // ✅ Récupérer les noms des joueurs, même hors ligne
        String senderName = getPlayer(senderUUID).map(Player::getName).orElse(Bukkit.getOfflinePlayer(senderUUID).getName());
        String receiverName = getPlayer(receiverUUID).map(Player::getName).orElse(Bukkit.getOfflinePlayer(receiverUUID).getName());

        Bukkit.getServer().getConsoleSender().sendMessage(Messages.info + senderName + ChatColor.GRAY + " a envoyé " + amount + "€ à " + receiverName);
        return true;
    }
}