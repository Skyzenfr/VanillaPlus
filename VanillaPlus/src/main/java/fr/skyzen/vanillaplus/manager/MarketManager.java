package fr.skyzen.vanillaplus.manager;

import fr.skyzen.vanillaplus.utils.MarketItem;
import fr.skyzen.vanillaplus.utils.Money;
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
    public static FileConfiguration config;
    private static JavaPlugin plugin;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void init(JavaPlugin pl) {
        plugin = pl;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().severe("Impossible de créer le dossier des données !");
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
        startExpirationTask(); // ✅ Vérification automatique des items expirés
    }

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

    public static void saveListings() {
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

    public static List<MarketItem> getListings() {
        return listings;
    }

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

    public static void createMarketInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Market");
        LocalDateTime now = LocalDateTime.now();

        for (int slot = 0; slot < Math.min(listings.size(), inv.getSize()); slot++) {
            MarketItem listing = listings.get(slot);
            ItemStack item = listing.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            String seller = player.getUniqueId().equals(listing.getSeller()) ? "Vous" : Bukkit.getOfflinePlayer(listing.getSeller()).getName();

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Vendeur : " + seller);
            lore.add(ChatColor.AQUA + "Prix : " + listing.getPrice() + "€");
            lore.add("");

            // ✅ Temps restant avant expiration
            LocalDateTime expirationDate = listing.getDate().plusDays(7);
            long secondsLeft = java.time.Duration.between(now, expirationDate).getSeconds();
            if (secondsLeft > 0) {
                long days = secondsLeft / 86400;
                long hours = (secondsLeft % 86400) / 3600;
                long minutes = (secondsLeft % 3600) / 60;

                String timeLeft = ChatColor.GRAY + "Expire dans : " + ChatColor.RED;
                if (days > 0) timeLeft += days + "j ";
                if (hours > 0) timeLeft += hours + "h ";
                if (minutes > 0) timeLeft += minutes + "m";

                lore.add(timeLeft);
            } else {
                lore.add(ChatColor.RED + "Offre expirée !");
            }

            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "ID: " + listing.getId()); // ✅ Ajout correct de l'ID
            lore.add("");

            if (!player.getUniqueId().equals(listing.getSeller())) {
                if (Money.hasMoney(player.getUniqueId(), listing.getPrice())) {
                    lore.add(ChatColor.GOLD + "Cliquez pour acheter");
                } else {
                    lore.add(ChatColor.RED + "Pas assez d'argent");
                }
            }

            if (player.getUniqueId().equals(listing.getSeller())) {
                lore.add(ChatColor.RED + "Shift-clic pour retirer de la vente");
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }
        player.openInventory(inv);
    }

    public static void checkExpiredListings() {
        List<MarketItem> toRemove = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (MarketItem item : listings) {
            if (item.getDate().plusDays(7).isBefore(now)) {
                toRemove.add(item);
                Player seller = Bukkit.getPlayer(item.getSeller());
                if (seller != null) {
                    seller.getInventory().addItem(item.getItem());
                    seller.sendMessage(ChatColor.RED + "Votre item listé a expiré et vous a été rendu.");
                }
            }
        }

        for (MarketItem item : toRemove) {
            removeListing(item);
        }
        saveListings();
    }

    public static void startExpirationTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, MarketManager::checkExpiredListings, 20 * 60 * 10, 20 * 60 * 10);
    }
}
