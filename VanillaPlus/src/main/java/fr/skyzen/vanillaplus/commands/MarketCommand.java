package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.manager.MarketManager;
import fr.skyzen.vanillaplus.utils.MarketItem;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class MarketCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
            return true;
        }

        if (args.length == 0) {
            // Ouvrir le GUI du market
            MarketManager.createMarketInventory(player);
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("sell")) {
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Prix invalide.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return true;
            }
            if (price <= 0) {
                player.sendMessage(ChatColor.RED + "Le prix doit être supérieur à 0.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return true;
            }
            // Récupérer l'item tenu en main
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType().isAir()) {
                player.sendMessage(ChatColor.RED + "Vous devez tenir un item pour le vendre.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                return true;
            }
            // Retirer l'item de la main
            player.getInventory().setItemInMainHand(null);
            // Créer et ajouter le listing dans le market
            MarketItem marketItem = new MarketItem(player.getUniqueId(), price, itemInHand, LocalDateTime.now());
            MarketManager.addListing(marketItem);
            player.sendMessage(ChatColor.GREEN + "Votre item a été mis en vente pour " + price + "€.");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Utilisation : /market [sell <prix>]");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return true;
    }
}
