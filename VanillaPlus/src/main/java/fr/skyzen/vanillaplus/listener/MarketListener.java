package fr.skyzen.vanillaplus.listener;

import fr.skyzen.vanillaplus.manager.MarketManager;
import fr.skyzen.vanillaplus.utils.MarketItem;
import fr.skyzen.vanillaplus.utils.MoneyManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class MarketListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifier que l'inventaire est celui du market
        if (!event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Market")) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Récupération de l'ID du listing via le lore
        if (!clickedItem.hasItemMeta() || !Objects.requireNonNull(clickedItem.getItemMeta()).hasLore()) {
            player.sendMessage(ChatColor.RED + "Cet item n'est pas valide.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        ItemMeta meta = clickedItem.getItemMeta();
        String id = null;
        for (String line : Objects.requireNonNull(meta.getLore())) {
            if (line.startsWith(ChatColor.DARK_GRAY + "ID: ")) {
                id = line.substring((ChatColor.DARK_GRAY + "ID: ").length());
                break;
            }
        }
        if (id == null) {
            player.sendMessage(ChatColor.RED + "Erreur lors de l'identification du listing.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        MarketItem clickedListing = null;
        for (MarketItem listing : MarketManager.getListings()) {
            if (listing.getId().equals(id)) {
                clickedListing = listing;
                break;
            }
        }
        if (clickedListing == null) {
            player.sendMessage(ChatColor.RED + "Ce listing n'existe plus.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }

        // Si le joueur est le vendeur
        if (clickedListing.getSeller().equals(player.getUniqueId())) {
            if (event.isShiftClick()) {
                MarketManager.removeListing(clickedListing);
                player.getInventory().addItem(clickedListing.getItem());
                player.sendMessage(ChatColor.GREEN + "Vous avez retiré votre item du market.");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                player.closeInventory();
                MarketManager.saveListings();
            } else {
                player.sendMessage(ChatColor.RED + "Vous ne pouvez pas acheter votre propre item.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            }
            return;
        }

        // Pour un acheteur : vérification du solde et achat
        double price = clickedListing.getPrice();
        if (!MoneyManager.hasMoney(player, price)) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent pour acheter cet item.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return;
        }
        MoneyManager.transferMoney(player.getUniqueId(), clickedListing.getSeller(), price);
        player.getInventory().addItem(clickedListing.getItem());
        MarketManager.removeListing(clickedListing);
        player.sendMessage(ChatColor.GREEN + "Vous avez acheté l'item pour " + price + "€.");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        player.closeInventory();
        MarketManager.saveListings();
    }
}

