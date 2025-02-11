package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.gui.StatistiquesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Stats implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @Nullable String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }
        StatistiquesGUI statsGUI = new StatistiquesGUI();
        statsGUI.openMainStatsGUI(player);
        return true;
    }
}
