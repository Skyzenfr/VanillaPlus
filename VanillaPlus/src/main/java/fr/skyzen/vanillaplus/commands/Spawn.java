package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Spawn implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @Nullable String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        World world = Bukkit.getWorld("world");
        if (world == null)
            return false;

        if (args != null && args.length > 0)
            if (player.isOp()) {
                if (args[0] == null)
                    return true;
                if (args[0].equalsIgnoreCase("create")) {
                    Bukkit.broadcastMessage(Messages.pluginName + ChatColor.YELLOW + player.getDisplayName() + ChatColor.GRAY + " vient de créer le spawn.");
                    world.setSpawnLocation(player.getLocation());
                    return true;
                }
            } else {
                player.sendMessage(Messages.command_restricted);
                return false;
            }
        if (Cooldown.hasCooldown(player, "teleport")) {
            Cooldown.sendCooldownMessage(player, "teleport", ChatColor.RED + "Vous devez attendre encore {time} {unit} avant de vous téléporter.");
            return false;
        }
        Players.teleportPlayer(player, world.getSpawnLocation());
        return true;
    }
}
