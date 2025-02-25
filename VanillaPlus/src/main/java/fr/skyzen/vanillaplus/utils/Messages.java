package fr.skyzen.vanillaplus.utils;

import fr.skyzen.vanillaplus.VanillaPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Messages {
    private static final FileConfiguration config = VanillaPlus.getInstance().getConfig();
    public static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final Map<Player, Scoreboard> playerScoreboards = new HashMap<>();

    public static String info = ChatColor.LIGHT_PURPLE + "[INFO] ";
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

    public static void sendClickableMessage(Player player, String message, String hoverText, boolean clickEvent, ClickEvent.Action action, String clickValue) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }

        // ✅ Appliquer les couleurs Minecraft (& → §)
        message = ChatColor.translateAlternateColorCodes('&', message);
        hoverText = ChatColor.translateAlternateColorCodes('&', hoverText);

        // ✅ Création du composant de texte
        TextComponent textComponent = new TextComponent(message);

        // ✅ Ajout du hover text
        if (!hoverText.isEmpty()) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverText)));
        }

        // ✅ Ajout du click event (si activé)
        if (clickEvent) {
            textComponent.setClickEvent(new ClickEvent(action, clickValue));
        }

        // ✅ Envoi du message au joueur
        player.spigot().sendMessage(textComponent);
    }

    /**
     * Met à jour la tablist et applique l'affichage de la vie au joueur.
     *
     * @param player Le joueur concerné.
     */
    public static void updatePlayerInfo(Player player) {
        if (player == null) return;

        // 🏆 1️⃣ - Mise à jour du Scoreboard personnalisé
        Scoreboard scoreboard = playerScoreboards.computeIfAbsent(player, k -> {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            return (manager != null) ? manager.getNewScoreboard() : null;
        });

        if (scoreboard == null) return;

        // 🔴 Objectif pour afficher la vie sous le pseudo
        Objective healthObjective = scoreboard.getObjective("health");
        if (healthObjective == null) {
            healthObjective = scoreboard.registerNewObjective("health", Criteria.HEALTH, ChatColor.RED + "❤");
            healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        // ❤️ Objectif pour afficher la vie dans la tablist
        Objective tabHealthObjective = scoreboard.getObjective("tab_health");
        if (tabHealthObjective == null) {
            tabHealthObjective = scoreboard.registerNewObjective("tab_health", Criteria.HEALTH, "Health", RenderType.HEARTS);
            tabHealthObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        player.setScoreboard(scoreboard);

        // 📌 2️⃣ - Mise à jour de la Tablist
        int onlineCount = Bukkit.getOnlinePlayers().size();
        String playersonline = (onlineCount <= 1) ? "Joueur en ligne" : "Joueurs en ligne";

        int posX = player.getLocation().getBlockX();
        int posY = player.getLocation().getBlockY();
        int posZ = player.getLocation().getBlockZ();

        String displayName = switch (player.getWorld().getName()) {
            case "world" -> "Monde Principal";
            case "world_nether" -> "Nether";
            case "world_end" -> "END";
            default -> player.getWorld().getName();
        };

        String playerWorldName = ChatColor.WHITE + "Monde actuel : " + ChatColor.GRAY + displayName;
        String playerPos = ChatColor.GRAY + " x: " + ChatColor.WHITE + posX +
                ChatColor.GRAY + " y: " + ChatColor.WHITE + posY +
                ChatColor.GRAY + " z: " + ChatColor.WHITE + posZ;

        String header = LegacyComponentSerializer.legacySection().serialize(
                Component.text("\n")
                        .append(Component.text(Messages.tab_header))
                        .append(Component.text("\n\n"))
                        .append(Component.text(onlineCount + " ", NamedTextColor.WHITE))
                        .append(Component.text(playersonline, NamedTextColor.GRAY))
                        .append(Component.text("\n"))
        );

        String footer = LegacyComponentSerializer.legacySection().serialize(
                Component.text("\n")
                        .append(Component.text(playerWorldName))
                        .append(Component.text("\n"))
                        .append(Component.text(playerPos))
                        .append(Component.text("\n\n"))
                        .append(Component.text(Messages.tab_footer))
                        .append(Component.text("\n"))
        );

        player.setPlayerListHeaderFooter(header, footer);

        // 🎭 3️⃣ - Mise à jour du nom dans la tablist avec couleur et ping
        String formattedName = Players.getPlayerName(player);
        String pingDisplay = Players.getPlayerPing(player);
        player.setPlayerListName(formattedName + " " + pingDisplay + " | ");
    }


    public static void startTablistUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updatePlayerInfo(player);
                }
            }
        }.runTaskTimer(VanillaPlus.getInstance(), 0L, 20L * 2); // 🔹 Mise à jour toutes les 2 secondes
    }
}
