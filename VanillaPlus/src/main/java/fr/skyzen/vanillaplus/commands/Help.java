package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.VanillaPlus;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Help implements CommandExecutor {

    private final VanillaPlus plugin;

    public Help(VanillaPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur !");
            return true;
        }
        sendHelpMessage(player);
        return true;
    }

    private void sendHelpMessage(Player player) {
        // Récupération des commandes définies dans le plugin.yml
        Map<String, Map<String, Object>> commandsMap = plugin.getDescription().getCommands();

        // Affichage du titre
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + " ❓ Aide - Liste des commandes disponibles");
        player.sendMessage("");

        // Parcours des commandes
        for (Map.Entry<String, Map<String, Object>> entry : commandsMap.entrySet()) {
            String commandName = entry.getKey();
            Map<String, Object> info = entry.getValue();

            // Récupération des infos
            String usage = info.getOrDefault("usage", "").toString();
            String description = info.getOrDefault("description", "Aucune description.").toString();

            // Vérification des permissions
            if (info.containsKey("permission")) {
                String permission = info.get("permission").toString();
                if (!player.hasPermission(permission)) {
                    continue; // Le joueur ne peut pas voir cette commande
                }
            }

            sendCommandHelp(player, commandName, usage, description);
        }
    }

    private void sendCommandHelp(Player player, String command, String usage, String description) {
        // Correction de la duplication de commande
        String fullCommand = usage.isEmpty() ? "/" + command : usage;

        // Construction du texte cliquable de la commande
        TextComponent commandComponent = new TextComponent(ChatColor.GOLD + fullCommand + ChatColor.DARK_GRAY + " ➲ " + ChatColor.GRAY + description);
        commandComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, fullCommand));

        // Hover message
        commandComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(ChatColor.GOLD + "Commande: " + ChatColor.WHITE + fullCommand + "\n" +
                        ChatColor.AQUA + "Description: " + ChatColor.GRAY + description + "\n" + "\n" +
                        ChatColor.GRAY + "Cliquez pour pré-remplir la commande")));

        // Envoi du message
        player.spigot().sendMessage(commandComponent);
    }
}