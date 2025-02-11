package fr.skyzen.vanillaplus.utils;

import fr.skyzen.vanillaplus.VanillaPlus;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class Messages {
    private static final FileConfiguration config = VanillaPlus.getInstance().getConfig();
    public static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String pluginName = ChatColor.GREEN + VanillaPlus.getInstance().getDescription().getName();
    public static String command_restricted = config.getString("command_restricted");
    public static String tab_header = config.getString("tab_header");
    public static String tab_footer = config.getString("tab_footer");
    public static double hunger_decrease_rate = config.getDouble("hunger_decrease_rate");
    public static int teleport_delay = config.getInt("teleport.delay");

    public static String formatGameTime(long ticks) {
        long seconds = ticks / 20L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        return hours > 0L ? String.format("%d heures et %d minutes", hours, minutes % 60L) : String.format("%d minutes et %d secondes", minutes, seconds % 60L);
    }

    public static void welcomeMessage(Player player) {
        player.sendMessage(" ");
        if (player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.WHITE + "Content de vous revoir " + ChatColor.YELLOW + Players.getPlayerName(player) + ChatColor.WHITE + "!");
        } else {
            player.sendMessage(ChatColor.WHITE + "Bienvenue à vous!");
        }

        // Création du composant cliquable pour /aide
        TextComponent messageAide = new TextComponent(ChatColor.AQUA + "Pour voir les commandes faites ");
        TextComponent commandeAide = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "/aide");
        commandeAide.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aide"));
        commandeAide.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Cliquez pour exécuter /aide")));
        messageAide.addExtra(commandeAide);
        messageAide.addExtra(new TextComponent(ChatColor.RESET + "" + ChatColor.AQUA + "."));

        player.spigot().sendMessage(messageAide);
        player.sendMessage(" ");
        player.sendMessage(ChatColor.WHITE + "Mode de jeu: " + ChatColor.GOLD + ChatColor.BOLD + "Survie");
        player.sendMessage(ChatColor.WHITE + "Accessible pour " + ChatColor.GREEN + ChatColor.BOLD + "TOUT LE MONDE");
        player.sendMessage(" ");
    }

    public static void helpMessage(Player player, String title, String command, String usage, String description) {
        String help = ChatColor.GOLD + " Aide ❓" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + title;
        player.sendMessage("                    ");
        player.sendMessage(ChatColor.UNDERLINE + help);
        player.sendMessage("                    ");
        player.sendMessage(ChatColor.YELLOW + " /" + command + " " + usage + ChatColor.DARK_GRAY + " ➲ " + ChatColor.GRAY + description + ".");
        player.sendMessage("                    ");
    }
}
