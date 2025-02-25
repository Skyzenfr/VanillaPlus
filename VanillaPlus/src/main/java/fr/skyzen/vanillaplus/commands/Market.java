package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.manager.MarketManager;
import fr.skyzen.vanillaplus.utils.MarketItem;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

public class Market implements CommandExecutor {

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

            // Récupérer le nom de l'item (ou un nom par défaut)
            String itemName = itemInHand.hasItemMeta() && Objects.requireNonNull(itemInHand.getItemMeta()).hasDisplayName()
                    ? itemInHand.getItemMeta().getDisplayName()
                    : ChatColor.YELLOW + itemInHand.getType().name().toLowerCase().replace("_", " ");

            // Retirer l'item de la main
            player.getInventory().setItemInMainHand(null);

            // Créer et ajouter le listing dans le market
            MarketItem marketItem = new MarketItem(player.getUniqueId(), price, itemInHand, LocalDateTime.now());
            MarketManager.addListing(marketItem);

            // 🔹 Construction du message
            String message = Players.getPlayerName(player) + ChatColor.GRAY + " a mis en vente " +
                    ChatColor.YELLOW + ChatColor.BOLD + "[" + itemName + "]" + ChatColor.RESET + ChatColor.GRAY +
                    " pour " + ChatColor.AQUA + price + "€ " + ChatColor.GREEN + "(Cliquez ici)";

            // 🔹 Envoi du message cliquable à tous les joueurs
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Messages.sendClickableMessage(onlinePlayer, message,
                        ChatColor.GRAY + "Cliquez ici pour accéder au market.",
                        true, ClickEvent.Action.RUN_COMMAND, "/market");
            }

            // ✅ Message console
            Bukkit.getServer().getConsoleSender().sendMessage(Messages.info + Players.getPlayerName(player) + ChatColor.GRAY + " vient de mettre en vente : " + ChatColor.GOLD + itemName);

            // ✅ Joueur entend un son
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Utilisation : /market [sell <prix>]");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        return true;
    }
}
