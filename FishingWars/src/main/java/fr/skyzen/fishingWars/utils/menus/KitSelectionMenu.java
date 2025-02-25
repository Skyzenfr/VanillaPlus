package fr.skyzen.fishingWars.utils.menus;

import fr.skyzen.fishingWars.utils.kits.KitManager;
import fr.skyzen.fishingWars.utils.kits.KitType;
import fr.skyzen.fishingWars.utils.scoreboard.LobbyScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSelectionMenu implements Listener {
    private final KitManager kitManager;
    private final LobbyScoreboard lobbyScoreboard; // 📌 Ajout du scoreboard

    public KitSelectionMenu(KitManager kitManager, LobbyScoreboard lobbyScoreboard) {
        this.kitManager = kitManager;
        this.lobbyScoreboard = lobbyScoreboard;
    }

    public static void openMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "Sélection du Kit");

        menu.setItem(0, createItem(Material.IRON_SWORD, "§6Guerrier"));
        menu.setItem(1, createItem(Material.BOW, "§6Archer"));
        menu.setItem(2, createItem(Material.FLINT_AND_STEEL, "§6Pyromane"));
        menu.setItem(3, createItem(Material.FISHING_ROD, "§6Pêcheur"));
        menu.setItem(4, createItem(Material.DIAMOND_CHESTPLATE, "§6Tank"));

        player.openInventory(menu);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Sélection du Kit")) {
            event.setCancelled(true); // Empêche de prendre ou déplacer les items
            Player player = (Player) event.getWhoClicked();

            KitType selectedKit = switch (event.getSlot()) {
                case 0 -> KitType.GUERRIER;
                case 1 -> KitType.ARCHER;
                case 2 -> KitType.PYROMANE;
                case 3 -> KitType.PECHEUR;
                case 4 -> KitType.TANK;
                default -> null;
            };

            if (selectedKit != null) {
                kitManager.assignKit(player, selectedKit);
                player.sendMessage(ChatColor.GREEN + "Tu as sélectionné le kit " + ChatColor.GOLD + selectedKit.name());
                player.closeInventory();

                // 📌 Mise à jour du scoreboard après le choix du kit
                if (lobbyScoreboard != null) {
                    lobbyScoreboard.setLobbyScoreboard(player);
                }
            }
        }
    }
}