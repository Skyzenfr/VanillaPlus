package fr.skyzen.vanillaplus.utils;

import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.UUID;

public class MarketItem {
    private final String id;
    private final UUID seller;
    private final double price;
    private final ItemStack item;
    private final LocalDateTime date;

    // Constructeur utilisé lors du chargement depuis le fichier (id fourni)
    public MarketItem(String id, UUID seller, double price, ItemStack item, LocalDateTime date) {
        this.id = id;
        this.seller = seller;
        this.price = price;
        this.item = item;
        this.date = date;
    }

    // Constructeur pour une nouvelle mise en vente (id généré automatiquement)
    public MarketItem(UUID seller, double price, ItemStack item, LocalDateTime date) {
        this(UUID.randomUUID().toString(), seller, price, item, date);
    }

    public String getId() {
        return id;
    }

    public UUID getSeller() {
        return seller;
    }

    public double getPrice() {
        return price;
    }

    public ItemStack getItem() {
        return item;
    }

    public LocalDateTime getDate() {
        return date;
    }
}