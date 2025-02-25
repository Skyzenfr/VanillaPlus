package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.Money;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Level;

public class Pay implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        if (args.length != 2) {
            senderPlayer.sendMessage(ChatColor.RED + "Usage : /pay <joueur> <montant>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            senderPlayer.sendMessage(ChatColor.RED + "Joueur introuvable !");
            return true;
        }

        // Empêcher un joueur de se payer lui-même
        if (senderPlayer.equals(target)) {
            senderPlayer.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous payer vous-même !");
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return true;
        }

        try {
            double amount = Double.parseDouble(args[1]);

            if (amount <= 0) {
                senderPlayer.sendMessage(ChatColor.RED + "Le montant doit être un nombre positif !");
                return true;
            }

            UUID senderUUID = senderPlayer.getUniqueId();
            UUID targetUUID = target.getUniqueId();

            if (Money.transferMoney(senderUUID, targetUUID, amount)) {
                senderPlayer.sendMessage(ChatColor.GREEN + "💰 Vous avez envoyé " + amount + " pièces à " + ChatColor.GOLD + target.getName());
                target.sendMessage(ChatColor.GOLD + "💰 Vous avez reçu " + ChatColor.GREEN + amount + " pièces de " + ChatColor.AQUA + senderPlayer.getName());

                // ✅ Ajout d'un log pour suivre les transactions
                Bukkit.getLogger().log(Level.INFO, senderPlayer.getName() + " a envoyé " + amount + "€ à " + target.getName());
            } else {
                senderPlayer.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent !");
            }
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage(ChatColor.RED + "Montant invalide ! Entrez un nombre valide.");
        }

        return true;
    }
}