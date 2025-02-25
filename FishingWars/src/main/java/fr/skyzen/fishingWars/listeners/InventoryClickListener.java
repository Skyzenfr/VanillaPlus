package fr.skyzen.fishingWars.listeners;

import fr.skyzen.fishingWars.manager.GameManager;
import fr.skyzen.fishingWars.utils.GameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    private final GameManager gameManager;

    public InventoryClickListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Empêcher les joueurs de déplacer leurs items si la partie est en attente
        if (gameManager.getStatus() == GameStatus.WAITING) {
            event.setCancelled(true);
        }

        // Empêcher les joueurs de déplacer les items dans le menu de sélection
        if (event.getView().getTitle().equals("Sélection du Kit")) {
            event.setCancelled(true);
        }
    }
}
