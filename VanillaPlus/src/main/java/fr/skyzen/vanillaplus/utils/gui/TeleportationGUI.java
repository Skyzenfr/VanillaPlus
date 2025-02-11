package fr.skyzen.vanillaplus.utils.gui;

import java.util.ArrayList;
import java.util.List;

import fr.skyzen.vanillaplus.VanillaPlus;
import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.Items;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class TeleportationGUI implements InventoryHolder, Listener {
    private static Inventory inventory;

    public TeleportationGUI() {
        this.currentPage = 0;
        refreshInventory();
    }

    private int currentPage;
    private Player opener;

    public static void initialize() {
        inventory = Bukkit.createInventory(null, 54, "Téléportation");
        Bukkit.getPluginManager().registerEvents(new TeleportationGUI(), VanillaPlus.getInstance());
    }

    public void refreshInventory() {
        inventory.clear();

        Player[] onlinePlayers = (Player[]) Bukkit.getOnlinePlayers().toArray((Object[]) new Player[0]);
        int startIndex = this.currentPage * 45;
        int endIndex = Math.min(startIndex + 45, onlinePlayers.length);

        for (int i = startIndex; i < endIndex; i++) {
            Player target = onlinePlayers[i];
            List<String> lore = getStrings(target);

            inventory.addItem(Items.getPlayerHead(target, ChatColor.YELLOW + target.getName(), lore));
        }


        int totalPages = (int) Math.ceil(onlinePlayers.length / 45.0D);
        if (totalPages > 1) {
            if (this.currentPage < totalPages - 1) {
                ItemStack nextPageItem = new ItemStack(Material.PAPER);
                ItemMeta nextPageMeta = nextPageItem.getItemMeta();
                assert nextPageMeta != null;
                nextPageMeta.setDisplayName(ChatColor.GREEN + "Page suivante");
                nextPageItem.setItemMeta(nextPageMeta);
                inventory.setItem(53, nextPageItem);
            }

            if (this.currentPage > 0) {
                ItemStack previousPageItem = new ItemStack(Material.PAPER);
                ItemMeta previousPageMeta = previousPageItem.getItemMeta();
                assert previousPageMeta != null;
                previousPageMeta.setDisplayName(ChatColor.GREEN + "Page précédente");
                previousPageItem.setItemMeta(previousPageMeta);
                inventory.setItem(45, previousPageItem);
            }
        }
    }

    private @NotNull List<String> getStrings(Player target) {
        boolean isSamePlayer = target.equals(this.opener);
        String leftClickAction = isSamePlayer ? (ChatColor.RED + "Vous ne pouvez pas vous téléporter à vous-même.") : (ChatColor.YELLOW + "Cliquez pour vous téléporter à cette personne ⇒");
        List<String> lore = new ArrayList<>();
        lore.add(" ");

        if (target.getWorld().getName().equals("world")) {
            lore.add(ChatColor.DARK_GRAY + " Monde: " + ChatColor.WHITE + target.getWorld().getName().replace("world", "Monde Principal"));
        } else if (target.getWorld().getName().equals("world1")) {
            lore.add(ChatColor.DARK_GRAY + " Monde: " + ChatColor.WHITE + target.getWorld().getName().replace("world1", "Ancien Monde"));
        } else {
            lore.add(ChatColor.DARK_GRAY + " Monde: " + ChatColor.WHITE + target.getWorld().getName());
        }

        lore.add(" ");
        lore.add(ChatColor.DARK_GRAY + " Position:");
        lore.add("        " + ChatColor.DARK_GRAY + "x: " + ChatColor.WHITE + target.getLocation().getBlockX());
        lore.add("        " + ChatColor.DARK_GRAY + "y: " + ChatColor.WHITE + target.getLocation().getBlockY());
        lore.add("        " + ChatColor.DARK_GRAY + "z: " + ChatColor.WHITE + target.getLocation().getBlockZ());
        lore.add(" ");
        lore.add(leftClickAction);
        return lore;
    }

    public static void openTeleportationGUI(Player player) {
        TeleportationGUI gui = new TeleportationGUI();
        gui.opener = player;
        gui.currentPage = 0;
        gui.refreshInventory();
        player.openInventory(gui.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        if (event.getView().getTitle().equalsIgnoreCase("Téléportation")) {
            event.setCancelled(true);

            if (item.getType() == Material.PLAYER_HEAD) {
                Player target = Bukkit.getPlayer(item.getItemMeta().getDisplayName());
                if (target == null)
                    return;
                if (Cooldown.hasCooldown(player, "teleport")) {
                    Cooldown.sendCooldownMessage(player, "teleport", ChatColor.RED + "Vous devez attendre encore {time} {unit} avant de vous téléporter.");
                    player.closeInventory();
                    return;
                }
                if (target.equals(player)) {
                    player.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous téléporter à vous-même!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);

                    return;
                }
                Players.teleportPlayer(player, target.getLocation());

                Bukkit.getScheduler().runTaskLater(VanillaPlus.getInstance(), () -> {
                    if (player.isOnline())
                        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " vient de se téléporter sur " + ChatColor.YELLOW + target.getName());
                }, Messages.teleport_delay);


                player.closeInventory();
            } else if (item.getType() == Material.PAPER) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta != null && itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (displayName.equals(ChatColor.GREEN + "Page suivante")) {
                        TeleportationGUI gui = (TeleportationGUI) event.getInventory().getHolder();
                        assert gui != null;
                        if (gui.currentPage < gui.getTotalPages() - 1) {
                            gui.currentPage++;
                            gui.refreshInventory();
                            player.openInventory(gui.getInventory());
                        }
                    } else if (displayName.equals(ChatColor.GREEN + "Page précédente")) {
                        TeleportationGUI gui = (TeleportationGUI) event.getInventory().getHolder();
                        assert gui != null;
                        if (gui.currentPage > 0) {
                            gui.currentPage--;
                            gui.refreshInventory();
                            player.openInventory(gui.getInventory());
                        }
                    }
                }
            }
        }
    }

    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    private int getTotalPages() {
        int totalPlayers = Bukkit.getOnlinePlayers().size();
        return (int) Math.ceil(totalPlayers / 45.0D);
    }
}