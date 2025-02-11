package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.MoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {

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

        try {
            double amount = Double.parseDouble(args[1]);

            if (amount <= 0) {
                senderPlayer.sendMessage(ChatColor.RED + "Le montant doit être positif !");
                return true;
            }

            if (MoneyManager.transferMoney(senderPlayer, target, amount)) {
                senderPlayer.sendMessage(ChatColor.GREEN + "💰 Vous avez envoyé " + amount + " pièces à " + target.getName());
                target.sendMessage(ChatColor.GOLD + "💰 Vous avez reçu " + ChatColor.GREEN + amount + " pièces de " + senderPlayer.getName());
            } else {
                senderPlayer.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent !");
            }
        } catch (NumberFormatException e) {
            senderPlayer.sendMessage(ChatColor.RED + "Montant invalide !");
        }

        return true;
    }
}

