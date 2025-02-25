package fr.skyzen.fishingWars.commands;

import fr.skyzen.fishingWars.manager.GameManager;
import fr.skyzen.fishingWars.utils.GameStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForceStart implements CommandExecutor {
    private final GameManager gameManager;

    public ForceStart(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender,@NonNull Command command,@NonNull String label,@NonNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande !");
            return true;
        }

        if (gameManager.getStatus() == GameStatus.IN_GAME) {
            sender.sendMessage("§cLa partie est déjà en cours !");
            return true;
        }

        sender.sendMessage("§aDémarrage forcé du jeu !");
        gameManager.startGame();
        return true;
    }
}
