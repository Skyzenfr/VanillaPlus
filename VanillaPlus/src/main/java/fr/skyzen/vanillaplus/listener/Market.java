package fr.skyzen.vanillaplus.listener;

import fr.skyzen.vanillaplus.manager.MarketManager;
import fr.skyzen.vanillaplus.utils.MarketItem;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Money;
import fr.skyzen.vanillaplus.utils.Players;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class Market implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Market")) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // 🔹 Vérification si l'item a bien un ID dans son lore
        if (!clickedItem.hasItemMeta() || !Objects.requireNonNull(clickedItem.getItemMeta()).hasLore()) {
            player.sendMessage(ChatColor.RED + "Cet item n'est pas valide.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        // 🔹 Extraction de l'ID depuis le lore
        ItemMeta meta = clickedItem.getItemMeta();
        String id = null;
        for (String line : Objects.requireNonNull(meta.getLore())) {
            if (ChatColor.stripColor(line).startsWith("ID: ")) { // 🔥 Corrigé pour éviter les problèmes de couleur
                id = ChatColor.stripColor(line).substring(4);
                break;
            }
        }

        if (id == null) {
            player.sendMessage(ChatColor.RED + "Erreur lors de l'identification du listing.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        // 🔹 Forcer la mise à jour des listings avant la recherche
        MarketManager.loadListings();

        // 🔹 Recherche du listing correspondant
        MarketItem clickedListing = null;
        for (MarketItem listing : MarketManager.getListings()) {
            if (listing.getId().equals(id)) {
                clickedListing = listing;
                break;
            }
        }

        // 🔹 Vérification si l'item a été trouvé
        if (clickedListing == null) {
            player.sendMessage(ChatColor.RED + "Cette vente n'est plus en ligne.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            Bukkit.getLogger().warning("ERREUR: Impossible de trouver l'item avec l'ID " + id);
            return;
        }

        // 🔹 Vérifier si l'item est expiré
        if (clickedListing.getDate().plusDays(7).isBefore(java.time.LocalDateTime.now())) {
            player.sendMessage(ChatColor.RED + "Cet item a expiré et n'est plus disponible.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            MarketManager.removeListing(clickedListing);
            MarketManager.saveListings();
            return;
        }

        // 🔹 Vérifier si le joueur est le vendeur
        if (clickedListing.getSeller().equals(player.getUniqueId())) {
            if (event.isShiftClick()) {
                MarketManager.removeListing(clickedListing);
                player.getInventory().addItem(clickedListing.getItem());
                player.sendMessage(ChatColor.GREEN + "Vous avez retiré votre item du market.");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                player.closeInventory();
                MarketManager.saveListings();
            }
            else {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez pas acheter votre propre item.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
            return;
        }

        // 🔹 Vérifier si le joueur a assez d'argent
        double price = clickedListing.getPrice();
        if (!Money.hasMoney(player.getUniqueId(), price)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            player.closeInventory();
            return;
        }

        // 🔹 Effectuer l'achat
        String itemName = clickedListing.getItem().hasItemMeta() && Objects.requireNonNull(clickedListing.getItem().getItemMeta()).hasDisplayName()
                ? clickedListing.getItem().getItemMeta().getDisplayName()
                : ChatColor.YELLOW + clickedListing.getItem().getType().name().toLowerCase().replace("_", " ");

        Money.transferMoney(player.getUniqueId(), clickedListing.getSeller(), price);
        player.getInventory().addItem(clickedListing.getItem());
        MarketManager.removeListing(clickedListing);

        for (Player people : Bukkit.getOnlinePlayers())
            Messages.sendClickableMessage(people,
                    Players.getPlayerName(player) + ChatColor.GRAY + " vient d'acheter " +
                            ChatColor.YELLOW + ChatColor.BOLD + "[" + itemName + "]" + ChatColor.RESET
                            + ChatColor.GRAY + " à " + ChatColor.AQUA + Bukkit.getOfflinePlayer(clickedListing.getSeller()).getName(),

                    ChatColor.GOLD + "" + ChatColor.BOLD + itemName + ChatColor.RESET
                            + "\n" + "\n"
                            + ChatColor.AQUA + "Vendeur: " + Bukkit.getOfflinePlayer(clickedListing.getSeller()).getName()
                            + "\n"
                            + ChatColor.GRAY + "Prix: " + clickedListing.getPrice() + "€"
                            + "\n" + "\n"
                            + ChatColor.WHITE + "Cliquez pour accéder au market.",
                    true,
                    ClickEvent.Action.RUN_COMMAND,
                    "/market");

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.closeInventory();
        MarketManager.saveListings();
    }
}