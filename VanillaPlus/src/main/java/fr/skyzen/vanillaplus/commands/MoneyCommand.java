package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.MoneyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent exécuter cette commande !");
            return true;
        }

        // /money → Voir son solde
        if (args.length == 0) {
            double balance = MoneyManager.getMoney(player);
            player.sendMessage(ChatColor.GOLD + "💰 Solde actuel : " + ChatColor.GREEN + balance + " pièces");
            return true;
        }

        // /money add <montant> <joueur> (OP uniquement)
        if (args.length == 3 && args[0].equalsIgnoreCase("add") && player.isOp()) {
            try {
                double amount = Double.parseDouble(args[1]);
                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                    return true;
                }

                MoneyManager.addMoney(target, amount);
                player.sendMessage(ChatColor.GREEN + "✅ Ajouté " + amount + " pièces à " + target.getName());
                target.sendMessage(ChatColor.GOLD + "💰 Vous avez reçu " + ChatColor.GREEN + amount + " pièces !");
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Montant invalide !");
            }
            return true;
        }

        // /money remove <montant> <joueur> (OP uniquement)
        if (args.length == 3 && args[0].equalsIgnoreCase("remove") && player.isOp()) {
            try {
                double amount = Double.parseDouble(args[1]);
                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Joueur introuvable !");
                    return true;
                }

                if (MoneyManager.removeMoney(target, amount)) {
                    player.sendMessage(ChatColor.GREEN + "❌ Retiré " + amount + " pièces à " + target.getName());
                    target.sendMessage(ChatColor.RED + "💰 Vous avez perdu " + amount + " pièces !");
                } else {
                    player.sendMessage(ChatColor.RED + "Le joueur n'a pas assez d'argent !");
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Montant invalide !");
            }
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage : /money [add/remove <montant> <joueur>]");
        return true;
    }
}

