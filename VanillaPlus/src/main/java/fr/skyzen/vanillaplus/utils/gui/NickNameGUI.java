package fr.skyzen.vanillaplus.utils.gui;

import fr.skyzen.vanillaplus.utils.*;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class NickNameGUI implements Listener {
    private static final String SHOP_TITLE = "🛒 Boutique de pseudo";
    private static final String COLOR_KEY = "name_colors";  // Stocke toutes les couleurs possédées
    private static final String SELECTED_COLOR_KEY = "selected_color";  // Stocke la couleur active
    private static final String ICON_KEY = "name_icons";  // Stocke toutes les icônes possédées
    private static final String SELECTED_ICON_KEY = "selected_icon";  // Stocke l’icône active
    private static final double PRICE = 100.0; // Prix de chaque achat

    private static final Map<Material, String> colorOptions = new LinkedHashMap<>();
    private static final Map<Material, String> iconOptions = new LinkedHashMap<>();

    static {
        // 📌 Définition des couleurs disponibles
        colorOptions.put(Material.RED_DYE, ChatColor.RED.toString());
        colorOptions.put(Material.BLUE_DYE, ChatColor.BLUE.toString());
        colorOptions.put(Material.GREEN_DYE, ChatColor.GREEN.toString());
        colorOptions.put(Material.YELLOW_DYE, ChatColor.YELLOW.toString());
        colorOptions.put(Material.LIGHT_BLUE_DYE, ChatColor.AQUA.toString());
        colorOptions.put(Material.PURPLE_DYE, ChatColor.DARK_PURPLE.toString()); // Violet
        colorOptions.put(Material.ORANGE_DYE, ChatColor.GOLD.toString()); // Orange
        colorOptions.put(Material.PINK_DYE, ChatColor.LIGHT_PURPLE.toString()); // Rose
        colorOptions.put(Material.WHITE_DYE, ChatColor.WHITE.toString()); // Blanc
        colorOptions.put(Material.GRAY_DYE, ChatColor.DARK_GRAY.toString()); // Gris foncé

        // 📌 Définition des icônes disponibles
        iconOptions.put(Material.DIAMOND, "💎");
        iconOptions.put(Material.GOLD_INGOT, "🏆");
        iconOptions.put(Material.NETHER_STAR, "⭐");
        iconOptions.put(Material.EMERALD, "🍀");
        iconOptions.put(Material.IRON_SWORD, "⚔");
        iconOptions.put(Material.FIRE_CHARGE, "🔥");
        iconOptions.put(Material.HEART_OF_THE_SEA, "❤️");
        iconOptions.put(Material.TRIDENT, "⚡");
    }

    public static void openNameShopGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 36, SHOP_TITLE);

        UUID uuid = player.getUniqueId();
        List<String> ownedColors = getPlayerList(player, COLOR_KEY);
        List<String> ownedIcons = getPlayerList(player, ICON_KEY);

        String selectedColor = PersistentData.getPersistentData(uuid, SELECTED_COLOR_KEY, PersistentDataType.STRING, ChatColor.GRAY.toString());
        String selectedIcon = PersistentData.getPersistentData(uuid, SELECTED_ICON_KEY, PersistentDataType.STRING, "");

        int[] colorSlots = {0, 1, 2, 3, 9, 10, 11, 12};
        int[] iconSlots = {5, 6, 7, 8, 14, 15, 16, 17};

        int colorIndex = 0, iconIndex = 0;

        // 📌 Ajout des couleurs (colonnes 0 → 3)
        for (Material material : colorOptions.keySet()) {
            if (colorIndex >= colorSlots.length) break;
            String color = colorOptions.get(material);
            boolean alreadyOwned = ownedColors.contains(color);
            boolean isSelected = color.equals(selectedColor);

            String title = (isSelected ? ChatColor.GREEN + "✔" : ChatColor.GRAY + "•") + color + " Couleur";
            List<String> lore = new ArrayList<>();

            lore.add("");
            if (alreadyOwned) {
                if (isSelected) {
                    lore.add(ChatColor.GREEN + "Déjà sélectionné");
                } else {
                    lore.add(ChatColor.GRAY + "Déjà possédé");
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "➜ Cliquez pour sélectionner");
                }
            } else {
                lore.add(ChatColor.YELLOW + "Prix : " + ChatColor.AQUA + PRICE + "€");
                lore.add("");
                lore.add(ChatColor.GREEN + "➜ Cliquez pour acheter");
            }

            ItemStack item = Items.createCustomItem(material, 1, title, lore);
            inventory.setItem(colorSlots[colorIndex], item);
            colorIndex++;
        }

        // 📌 Séparateur visuel
        for (int i = 4; i < 36; i += 9) {
            inventory.setItem(i, Items.createCustomItem(Material.BARRIER, 1, " ", Collections.singletonList("")));
        }

        // 📌 Ajout des icônes (colonnes 5 → 8)
        for (Material material : iconOptions.keySet()) {
            if (iconIndex >= iconSlots.length) break;
            String icon = iconOptions.get(material);
            boolean alreadyOwned = ownedIcons.contains(icon);
            boolean isSelected = icon.equals(selectedIcon);

            String title = (isSelected ? ChatColor.GREEN + "✔ " : ChatColor.GRAY + "• ") + icon + " Icône";
            List<String> lore = new ArrayList<>();

            lore.add("");
            if (alreadyOwned) {
                if (isSelected) {
                    lore.add(ChatColor.GREEN + "Déjà sélectionné");
                } else {
                    lore.add(ChatColor.GRAY + "Déjà possédé");
                    lore.add("");
                    lore.add(ChatColor.YELLOW + "➜ Cliquez pour sélectionner");
                }
            } else {
                lore.add(ChatColor.YELLOW + "Prix : " + ChatColor.AQUA + PRICE + "€");
                lore.add("");
                lore.add(ChatColor.GREEN + "➜ Cliquez pour acheter");
            }

            ItemStack item = Items.createCustomItem(material, 1, title, lore);
            inventory.setItem(iconSlots[iconIndex], item);
            iconIndex++;
        }

        player.openInventory(inventory);
    }


    @EventHandler
    public void handleShopClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(SHOP_TITLE)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        Material material = clickedItem.getType();
        UUID uuid = player.getUniqueId();

        if (colorOptions.containsKey(material)) {
            String color = colorOptions.get(material);
            handleSelection(player, uuid, COLOR_KEY, SELECTED_COLOR_KEY, color, "couleur");
        } else if (iconOptions.containsKey(material)) {
            String icon = iconOptions.get(material);
            handleSelection(player, uuid, ICON_KEY, SELECTED_ICON_KEY, icon, "icône");
        }
    }

    private void handleSelection(Player player, UUID uuid, String listKey, String selectedKey, String value, String type) {
        boolean isColor = listKey.equals("name_colors"); // Vérifie si on achète une couleur
        boolean isIcon = listKey.equals("name_icons"); // Vérifie si on achète une icône
        List<String> ownedItems = getPlayerList(player, listKey);
        String displayValue = value; // Valeur affichée (couleur ou icône)
        String currentSelection = PersistentData.getPersistentData(uuid, selectedKey, PersistentDataType.STRING, "");

        if (isColor) {
            displayValue = value + " ●"; // Ajoute un point coloré pour identifier visuellement
        } else if (isIcon) {
            displayValue = value + " "; // Ajoute un espace pour éviter que l’icône soit collée au pseudo
        }

        if (currentSelection.equals(value)) {
            player.sendMessage(ChatColor.RED + "Vous avez déjà équipé cette " + type + ".");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (!Money.hasMoney(player.getUniqueId(), PRICE)){
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        if (ownedItems.contains(value)) {
            PersistentData.setPersistentData(uuid, selectedKey, PersistentDataType.STRING, value);
            player.sendMessage(ChatColor.GRAY + "Vous avez sélectionné cette " + type + ChatColor.DARK_GRAY + " (Aperçu: " + Players.getPlayerName(player) + ChatColor.DARK_GRAY + ")");
            player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1.0f, 1.2f);
        } else if (Money.hasMoney(uuid, PRICE)) {
            Money.removeMoney(uuid, PRICE);
            ownedItems.add(value);
            PersistentData.setPersistentData(uuid, listKey, PersistentDataType.STRING, String.join(",", ownedItems));

            for (Player people : Bukkit.getOnlinePlayers()){
                Messages.sendClickableMessage(
                        people,
                        Players.getPlayerName(player) + ChatColor.GRAY + " vient d'acheter une " + ChatColor.GOLD + type + ChatColor.GRAY + " de pseudo.",

                        Players.getPlayerName(player)
                                + "\n\n"
                                + ChatColor.DARK_GRAY + type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase() + ": " + displayValue + "\n"
                                + ChatColor.DARK_GRAY + "Prix: " + ChatColor.AQUA + PRICE + "€"
                                + "\n\n"
                                + ChatColor.GREEN + "Cliquez ici pour accéder à la boutique.",

                        true,
                        ClickEvent.Action.RUN_COMMAND,
                        "/shopname");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        }
        openNameShopGUI(player);
    }

    private static List<String> getPlayerList(Player player, String key) {
        String data = PersistentData.getPersistentData(player.getUniqueId(), key, PersistentDataType.STRING, "");
        return data.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(data.split(",")));
    }
}