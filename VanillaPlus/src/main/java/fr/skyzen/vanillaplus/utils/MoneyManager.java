package fr.skyzen.vanillaplus.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MoneyManager {

    private static final String MONEY_KEY = "money"; // Clé utilisée pour stocker la money

    /* ======================================
       Méthodes utilisant l'objet Player
       ====================================== */

    /**
     * Récupère le solde d'un joueur
     * @param player Joueur concerné
     * @return Montant d'argent du joueur
     */
    public static double getMoney(Player player) {
        return PersistentDataUtil.getPersistentData(player, MONEY_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    /**
     * Définit le montant exact d'argent d'un joueur
     * @param player Joueur concerné
     * @param amount Nouveau montant
     */
    public static void setMoney(Player player, double amount) {
        PersistentDataUtil.setPersistentData(player, MONEY_KEY, PersistentDataType.DOUBLE, Math.max(0, amount));
    }

    /**
     * Ajoute de l'argent au solde d'un joueur
     * @param player Joueur concerné
     * @param amount Montant à ajouter
     */
    public static void addMoney(Player player, double amount) {
        setMoney(player, getMoney(player) + amount);
    }

    /**
     * Retire de l'argent du solde d'un joueur
     * @param player Joueur concerné
     * @param amount Montant à retirer
     * @return true si la transaction a réussi, false sinon
     */
    public static boolean removeMoney(Player player, double amount) {
        double currentMoney = getMoney(player);
        if (currentMoney < amount) {
            return false; // Pas assez d'argent
        }
        setMoney(player, currentMoney - amount);
        return true;
    }

    /**
     * Vérifie si un joueur à au moins X money
     * @param player Joueur concerné
     * @param amount Montant à vérifier
     * @return true si le joueur a au moins ce montant, false sinon
     */
    public static boolean hasMoney(Player player, double amount) {
        return getMoney(player) >= amount;
    }

    /**
     * Transfère de l'argent d'un joueur à un autre
     * @param sender Joueur envoyant l'argent
     * @param receiver Joueur recevant l'argent
     * @param amount Montant à transférer
     * @return true si le transfert a réussi, false sinon
     */
    public static boolean transferMoney(Player sender, Player receiver, double amount) {
        if (removeMoney(sender, amount)) {
            addMoney(receiver, amount);
            return true;
        }
        return false;
    }

    /* ======================================
       Méthodes utilisant UUID
       ====================================== */

    /**
     * Récupère le solde d'un joueur à partir de son UUID.
     * Note : Retourne 0 si le joueur n'est pas en ligne.
     * @param uuid UUID du joueur
     * @return Montant d'argent du joueur ou 0 si le joueur est hors ligne
     */
    public static double getMoney(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return getMoney(player);
        }
        // Si le joueur est hors ligne, vous pouvez :
        //  retourner une valeur par défaut,
        // - charger le montant depuis un fichier/base de données, etc.
        return 0.0;
    }

    /**
     * Définit le solde d'un joueur à partir de son UUID.
     * Note : Fonctionne uniquement si le joueur est en ligne.
     * @param uuid UUID du joueur
     * @param amount Nouveau montant
     */
    public static void setMoney(UUID uuid, double amount) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            setMoney(player, amount);
        } else {
            // Pour les joueurs hors ligne, pensez à enregistrer le montant dans un système de stockage.
        }
    }

    /**
     * Ajoute de l'argent au solde d'un joueur à partir de son UUID.
     * Note : Fonctionne uniquement si le joueur est en ligne.
     * @param uuid UUID du joueur
     * @param amount Montant à ajouter
     */
    public static void addMoney(UUID uuid, double amount) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            addMoney(player, amount);
        } else {
            // Pour les joueurs hors ligne, pensez à mettre à jour votre système de stockage.
        }
    }

    /**
     * Retire de l'argent du solde d'un joueur à partir de son UUID.
     * Note : Fonctionne uniquement si le joueur est en ligne.
     * @param uuid UUID du joueur
     * @param amount Montant à retirer
     * @return true si la transaction a réussi, false sinon
     */
    public static boolean removeMoney(UUID uuid, double amount) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return removeMoney(player, amount);
        }
        // Pour les joueurs hors ligne, pensez à mettre à jour votre système de stockage.
        return false;
    }

    /**
     * Vérifie si un joueur identifié par son UUID a au moins X money.
     * Note : Fonctionne uniquement si le joueur est en ligne.
     * @param uuid UUID du joueur
     * @param amount Montant à vérifier
     * @return true si le joueur a au moins ce montant, false sinon
     */
    public static boolean hasMoney(UUID uuid, double amount) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return hasMoney(player, amount);
        }
        // Pour les joueurs hors ligne, pensez à charger leur montant depuis un système de stockage.
        return false;
    }

    /**
     * Transfère de l'argent d'un joueur à un autre à partir de leur UUID.
     * Note : Fonctionne uniquement si les deux joueurs sont en ligne.
     * @param sender UUID du joueur envoyant l'argent
     * @param receiver UUID du joueur recevant l'argent
     * @param amount Montant à transférer
     * @return true si le transfert a réussi, false sinon
     */
    public static boolean transferMoney(UUID sender, UUID receiver, double amount) {
        if (removeMoney(sender, amount)) {
            addMoney(receiver, amount);
            return true;
        }
        return false;
    }
}
