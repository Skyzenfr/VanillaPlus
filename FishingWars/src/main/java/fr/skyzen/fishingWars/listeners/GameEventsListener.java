package fr.skyzen.fishingWars.listeners;

import fr.skyzen.fishingWars.utils.menus.KitSelectionMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GameEventsListener implements Listener {

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (item.getType() == Material.RED_BED) {
            // Quitter le jeu
            player.kickPlayer(ChatColor.RED + "Tu as quitté la partie !");
        } else if (item.getType() == Material.COMPASS) {
            // Ouvrir le menu de sélection de kit
            KitSelectionMenu.openMenu(player);
        }
    }
}
