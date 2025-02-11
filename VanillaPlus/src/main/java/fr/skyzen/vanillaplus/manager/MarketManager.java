package fr.skyzen.vanillaplus.manager;

import fr.skyzen.vanillaplus.utils.MarketItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MarketManager {

    private static final List<MarketItem> listings = new ArrayList<>();
    private static File file;
    public static FileConfiguration config;  // Veillez à ce qu'elle soit bien initialisée
    private static JavaPlugin plugin;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Initialise le MarketManager et charge le fichier de sauvegarde.
     */
    public static void init(JavaPlugin pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        file = new File(dataFolder, "market.yml");
        if (!file.exists()) {
            try {
                config = YamlConfiguration.loadConfiguration(file);
                config.createSection("listings");
                config.save(file);
            } catch (IOException e) {
                Bukkit.getLogger().warning("Erreur lors de la sauvegarde");
            }
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
        loadListings();
    }

    /**
     * Charge tous les listings enregistrés depuis le fichier YML.
     */
    public static void loadListings() {
        listings.clear();
        if (config.contains("listings")) {
            for (String key : Objects.requireNonNull(config.getConfigurationSection("listings")).getKeys(false)) {
                String path = "listings." + key + ".";
                try {
                    UUID seller = UUID.fromString(Objects.requireNonNull(config.getString(path + "seller")));
                    double price = config.getDouble(path + "price");
                    String dateStr = config.getString(path + "date");
                    assert dateStr != null;
                    LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
                    ItemStack item = config.getItemStack(path + "item");
                    MarketItem marketItem = new MarketItem(key, seller, price, item, date);
                    listings.add(marketItem);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Erreur lors du chargement du listing: " + key);
                }
            }
        }
    }

    /**
     * Sauvegarde l'ensemble des listings dans le fichier YML.
     */
    public static void saveListings() {
        if (config == null) {
            plugin.getLogger().severe("Impossible de sauvegarder : config est null !");
            return;
        }
        // Réinitialise la section des listings
        config.set("listings", null);
        for (MarketItem item : listings) {
            String path = "listings." + item.getId() + ".";
            config.set(path + "seller", item.getSeller().toString());
            config.set(path + "price", item.getPrice());
            config.set(path + "date", item.getDate().format(formatter));
            config.set(path + "item", item.getItem());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Erreur lors de la sauvegarde");
        }
    }

    /**
     * Ajoute un nouveau listing et le sauvegarde immédiatement.
     */
    public static void addListing(MarketItem item) {
        listings.add(item);
        if (config == null) {
            plugin.getLogger().severe("MarketManager non initialisé, config est null !");
            return;
        }
        String path = "listings." + item.getId() + ".";
        config.set(path + "seller", item.getSeller().toString());
        config.set(path + "price", item.getPrice());
        config.set(path + "date", item.getDate().format(formatter));
        config.set(path + "item", item.getItem());
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Erreur lors de la sauvegarde");
        }
    }

    /**
     * Retire un listing et l'enlève du fichier de sauvegarde.
     */
    public static void removeListing(MarketItem item) {
        listings.remove(item);
        if (config == null) {
            plugin.getLogger().severe("MarketManager non initialisé, config est null !");
            return;
        }
        config.set("listings." + item.getId(), null);
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Erreur lors de la sauvegarde");
        }
    }

    public static List<MarketItem> getListings() {
        return listings;
    }

    /**
     * Construit l'interface du market avec toutes les informations et instructions.
     */
    public static void createMarketInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Market");
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int slot = 0; slot < Math.min(listings.size(), inv.getSize()); slot++) {
            MarketItem listing = listings.get(slot);
            ItemStack item = listing.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Vendeur : " + Bukkit.getOfflinePlayer(listing.getSeller()).getName());
            lore.add(ChatColor.AQUA + "Prix : " + listing.getPrice() + "€");
            lore.add("");
            lore.add(ChatColor.GRAY + "Mise en vente : " + listing.getDate().format(displayFormatter));
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "ID: " + listing.getId());
            lore.add("");
            // Instructions pour l'action
            lore.add(ChatColor.GOLD + "Cliquez pour acheter");
            if (player.getUniqueId().equals(listing.getSeller())) {
                lore.add(ChatColor.RED + "Shift-clic pour retirer de la vente");
            }

            // Concaténer l'ancien lore si existant
            if (meta.hasLore()) {
                lore.addAll(Objects.requireNonNull(meta.getLore()));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        // ✅ Ouvrir le GUI pour le joueur
        player.openInventory(inv);
    }

}