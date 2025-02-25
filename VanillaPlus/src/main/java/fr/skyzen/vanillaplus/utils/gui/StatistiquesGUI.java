package fr.skyzen.vanillaplus.utils.gui;

import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Money;
import fr.skyzen.vanillaplus.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Statistic;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatistiquesGUI implements InventoryHolder, Listener {
    private static final String TITLE_STATS = "Statistiques";
    private static final String TITLE_ALL_PLAYERS = "Statistiques - Tous les joueurs";
    private static final int INVENTORY_SIZE = 54;
    private static final Material[] WOOD_TYPES = {Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG};
    private static final Material[] PLANT_TYPES = {Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS};

    private Inventory inventory;

    public void openMainStatsGUI(Player player) {
        inventory = Bukkit.createInventory(this, INVENTORY_SIZE, TITLE_STATS);

        ItemStack playerHead = createPlayerStatsItem(player);
        inventory.setItem(13, playerHead);

        ItemStack allPlayersItem = createCustomItem(Material.ITEM_FRAME, "§bStatistiques : §fTous les joueurs",
                Arrays.asList(" ", "§7Voir les §5statistiques", "§7de tous les §5joueurs§7 du serveur.", " ", "§8➵ §aClic-Gauche : Accéder"));
        inventory.setItem(31, allPlayersItem);

        ItemStack backItem = createCustomItem(Material.ARROW, ChatColor.RED + "Retour", null);
        inventory.setItem(53, backItem);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

    }

    public void openAllPlayersStatsGUI(Player player) {
        inventory = Bukkit.createInventory(this, INVENTORY_SIZE, TITLE_ALL_PLAYERS);

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            ItemStack playerHead = createPlayerStatsItem(offlinePlayer);
            inventory.addItem(playerHead);
        }

        ItemStack backItem = createCustomItem(Material.ARROW, ChatColor.RED + "Retour", null);
        inventory.setItem(53, backItem);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private ItemStack createPlayerStatsItem(OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);

            // ✅ Vérifier si le joueur est en ligne avant d'utiliser getPlayerName()
            String displayName = ChatColor.GRAY + player.getName(); // Par défaut, le nom est gris (hors-ligne).
            Player onlinePlayer = player.getPlayer(); // Vérifier si le joueur est en ligne

            if (onlinePlayer != null) {
                displayName = Players.getPlayerName(onlinePlayer); // Utiliser le nom personnalisé si en ligne
            }
            meta.setDisplayName(displayName);

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(ChatColor.GRAY + " Générales:");
            lore.add(" ");
            lore.add(ChatColor.DARK_GRAY + "   Status: " + (player.isOnline() ? ChatColor.GREEN + "En ligne" : ChatColor.RED + "Hors-Ligne"));
            lore.add(ChatColor.DARK_GRAY + "   Niveau: " + ChatColor.WHITE + Players.getPlayerLevel(player.getUniqueId()));
            lore.add(ChatColor.DARK_GRAY + "   Argent: " + ChatColor.WHITE + Money.getMoney(player.getUniqueId()));
            lore.add(ChatColor.DARK_GRAY + "   Première connexion: " + ChatColor.YELLOW + new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(player.getFirstPlayed())));
            lore.add(ChatColor.DARK_GRAY + "   Temps de jeu: " + ChatColor.GOLD + Messages.formatGameTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)));
            lore.add(" ");
            lore.add(ChatColor.GRAY + " Victimes/morts:");
            lore.add(" ");
            lore.add(ChatColor.DARK_GRAY + "   Joueurs tués: " + ChatColor.AQUA + player.getStatistic(Statistic.PLAYER_KILLS));
            lore.add(ChatColor.DARK_GRAY + "   Mobs tués: " + ChatColor.DARK_AQUA + player.getStatistic(Statistic.MOB_KILLS));
            lore.add(ChatColor.DARK_GRAY + "   Morts: " + ChatColor.RED + player.getStatistic(Statistic.DEATHS));
            lore.add(" ");
            lore.add(ChatColor.GRAY + " Blocs minés/autres:");
            lore.add(" ");
            lore.add(ChatColor.DARK_GRAY + "   Minerais de fer minés: " + ChatColor.WHITE + player.getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
            lore.add(ChatColor.DARK_GRAY + "   Minerais d'or minés: " + ChatColor.WHITE + player.getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
            lore.add(ChatColor.DARK_GRAY + "   Minerais de diamant minés: " + ChatColor.WHITE + player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
            lore.add(ChatColor.DARK_GRAY + "   Blocs de Cobblestone minés: " + ChatColor.WHITE + player.getStatistic(Statistic.MINE_BLOCK, Material.COBBLESTONE));

            //Ajout des stats
            int woodMined = 0;
            for (Material material : WOOD_TYPES) {
                woodMined += player.getStatistic(Statistic.MINE_BLOCK, material);
            }
            lore.add(ChatColor.DARK_GRAY + "   Bûches de bois minés: " + ChatColor.WHITE + woodMined);

            int plantsHarvested = 0;
            for (Material material : PLANT_TYPES) {
                plantsHarvested += player.getStatistic(Statistic.MINE_BLOCK, material);
            }

            lore.add(ChatColor.DARK_GRAY + "   Plantes récoltées: " + ChatColor.WHITE + plantsHarvested);
            lore.add(ChatColor.DARK_GRAY + "   Poissons péchés: " + ChatColor.WHITE + player.getStatistic(Statistic.FISH_CAUGHT));
            lore.add(ChatColor.DARK_GRAY + "   Animaux nourris: " + ChatColor.WHITE + player.getStatistic(Statistic.ANIMALS_BRED));
            lore.add(ChatColor.DARK_GRAY + "   Sauts: " + ChatColor.LIGHT_PURPLE + player.getStatistic(Statistic.JUMP));
            lore.add(" ");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCustomItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (event.getView().getTitle().equals(TITLE_STATS)) {
            if (clickedItem.getType() == Material.ITEM_FRAME) {
                openAllPlayersStatsGUI(player);
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 1.0f);
            } else if (clickedItem.getType() == Material.ARROW) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equals(TITLE_ALL_PLAYERS)) {
            if (clickedItem.getType() == Material.ARROW) {
                openMainStatsGUI(player);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}