package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.gui.TeleportationGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Teleportation implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @Nullable String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (Cooldown.hasCooldown(player, "teleport")) {
            Cooldown.sendCooldownMessage(player, "teleport", ChatColor.RED + "Vous devez attendre encore {time} {unit} avant de vous téléporter.");
            return true;
        }

        TeleportationGUI.openTeleportationGUI(player);
        return true;
    }
}