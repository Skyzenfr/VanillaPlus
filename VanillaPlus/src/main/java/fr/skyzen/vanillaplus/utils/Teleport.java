package fr.skyzen.vanillaplus.utils;

import fr.skyzen.vanillaplus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Teleport {

    public static void teleportPlayer(Player player, Location destination) {
        // Récupération de l'instance principale pour accéder à la config et au scheduler
        JavaPlugin plugin = VanillaPlus.getInstance();

        // Récupération de la section "teleport" dans la config
        ConfigurationSection teleportConfig = plugin.getConfig().getConfigurationSection("teleport");
        if (teleportConfig == null) {
            plugin.getLogger().severe("La section 'teleport' est absente du fichier de configuration !");
            return;
        }

        // Paramètres principaux
        int delay = teleportConfig.getInt("delay", 5);             // délai avant téléportation (en secondes)
        int cooldown = teleportConfig.getInt("cooldown", 10);        // cooldown après téléportation (en secondes)
        boolean cancelOnMove = teleportConfig.getBoolean("cancelOnMove", true);
        String cooldownKey = "teleport";

        // Récupération des messages personnalisables
        ConfigurationSection messages = teleportConfig.getConfigurationSection("messages");
        assert messages != null;
        String waitingMessage = messages.getString("waiting", "&eTéléportation dans {delay} secondes. Ne bougez pas !");
        String cancelledMessage = messages.getString("cancelled", "&cTéléportation annulée car vous avez bougé.");
        String teleportedMessage = messages.getString("teleported", "&aVous avez été téléporté.");
        String cooldownMessage = messages.getString("cooldown", "&cVous devez attendre encore {timeLeft}{timeUnit} avant de vous téléporter.");

        // Récupération de la configuration des titres
        ConfigurationSection titles = teleportConfig.getConfigurationSection("titles");
        // Titre pour le compte à rebours
        assert titles != null;
        ConfigurationSection countdownTitleConfig = titles.getConfigurationSection("countdown");
        assert countdownTitleConfig != null;
        String countdownTitle = countdownTitleConfig.getString("title", "&eTéléportation dans");
        String countdownSubtitle = countdownTitleConfig.getString("subtitle", "&a{countdown}");
        int countdownFadeIn = countdownTitleConfig.getInt("fadeIn", 0);
        int countdownStay = countdownTitleConfig.getInt("stay", 20);
        int countdownFadeOut = countdownTitleConfig.getInt("fadeOut", 0);

        // Titre pour l'annulation
        ConfigurationSection cancelledTitleConfig = titles.getConfigurationSection("cancelled");
        assert cancelledTitleConfig != null;
        String cancelledTitle = cancelledTitleConfig.getString("title", "&cTéléportation");
        String cancelledSubtitle = cancelledTitleConfig.getString("subtitle", "&cAnnulée");
        int cancelledFadeIn = cancelledTitleConfig.getInt("fadeIn", 0);
        int cancelledStay = cancelledTitleConfig.getInt("stay", 40);
        int cancelledFadeOut = cancelledTitleConfig.getInt("fadeOut", 10);

        // Titre pour la réussite
        ConfigurationSection teleportedTitleConfig = titles.getConfigurationSection("teleported");
        assert teleportedTitleConfig != null;
        String teleportedTitle = teleportedTitleConfig.getString("title", "&aTéléportation");
        String teleportedSubtitle = teleportedTitleConfig.getString("subtitle", "&aRéussie");
        int teleportedFadeIn = teleportedTitleConfig.getInt("fadeIn", 0);
        int teleportedStay = teleportedTitleConfig.getInt("stay", 40);
        int teleportedFadeOut = teleportedTitleConfig.getInt("fadeOut", 10);

        // Vérification du cooldown
        if (Cooldown.hasCooldown(player, cooldownKey)) {
            long timeLeft = Cooldown.getCooldownTimeLeft(player, cooldownKey);
            String timeUnit = timeLeft == 1 ? " seconde" : " secondes";
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    cooldownMessage.replace("{timeLeft}", String.valueOf(timeLeft))
                            .replace("{timeUnit}", timeUnit)
            ));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return;
        }

        // Récupérer la position initiale du joueur
        Location initialLocation = player.getLocation().clone();
        // Envoi du message d'attente avec remplacement du placeholder {delay}
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                waitingMessage.replace("{delay}", String.valueOf(delay))
        ));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

        AtomicInteger countdown = new AtomicInteger(Messages.teleport_delay);
        AtomicBoolean teleportCancelled = new AtomicBoolean(false);

        // Tâche de compte à rebours (exécutée chaque seconde)
        BukkitTask countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Si l'annulation est activée et que le joueur a bougé
            if (cancelOnMove && !teleportCancelled.get() &&
                    (player.getLocation().getX() != initialLocation.getX() ||
                            player.getLocation().getY() != initialLocation.getY() ||
                            player.getLocation().getZ() != initialLocation.getZ())) {

                teleportCancelled.set(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelledMessage));
                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', cancelledTitle),
                        ChatColor.translateAlternateColorCodes('&', cancelledSubtitle),
                        cancelledFadeIn, cancelledStay, cancelledFadeOut
                );
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            int currentCount = countdown.getAndDecrement();
            if (currentCount > 0 && !teleportCancelled.get()) {
                String subtitle = countdownSubtitle.replace("{countdown}", String.valueOf(currentCount));
                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', countdownTitle),
                        ChatColor.translateAlternateColorCodes('&', subtitle),
                        countdownFadeIn, countdownStay, countdownFadeOut
                );
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            }
        }, 0L, 20L);

        // Tâche différée qui exécute la téléportation une fois le délai écoulé
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            countdownTask.cancel();

            if (!teleportCancelled.get()) {
                player.teleport(destination);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', teleportedMessage));
                player.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', teleportedTitle),
                        ChatColor.translateAlternateColorCodes('&', teleportedSubtitle),
                        teleportedFadeIn, teleportedStay, teleportedFadeOut
                );
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                Cooldown.setCooldown(player, cooldownKey, cooldown);
            }
        }, delay * 20L);
    }
}
