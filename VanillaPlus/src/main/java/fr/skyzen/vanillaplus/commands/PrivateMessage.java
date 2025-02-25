package fr.skyzen.vanillaplus.commands;

import fr.skyzen.vanillaplus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PrivateMessage implements CommandExecutor {

    private final Map<UUID, UUID> lastMessaged = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final VanillaPlus plugin;
    private static final int MAX_HISTORY = 10;
    private static final long COOLDOWN_TIME = 5000; // 5 secondes

    public PrivateMessage(VanillaPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande.");
            return true;
        }

        if (label.equalsIgnoreCase("msg")) {
            if (args.length < 2) {
                player.sendMessage("§cUtilisation : /msg <joueur> <message>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§cCe joueur n'est pas en ligne.");
                return true;
            }

            if (target.equals(player)) {
                player.sendMessage("§cTu ne peux pas t'envoyer un message à toi-même !");
                return true;
            }

            if (isOnCooldown(player)) {
                player.sendMessage("§cTu dois attendre avant d'envoyer un autre message privé !");
                return true;
            }

            String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            sendPrivateMessage(player, target, message);

        } else if (label.equalsIgnoreCase("reply")) {
            if (args.length < 1) {
                player.sendMessage("§cUtilisation : /reply <message>");
                return true;
            }

            if (!lastMessaged.containsKey(player.getUniqueId())) {
                player.sendMessage("§cAucun joueur à qui répondre.");
                return true;
            }

            Player target = Bukkit.getPlayer(lastMessaged.get(player.getUniqueId()));
            if (target == null || !target.isOnline()) {
                player.sendMessage("§cCe joueur n'est plus en ligne.");
                return true;
            }

            if (isOnCooldown(player)) {
                player.sendMessage("§cTu dois attendre avant d'envoyer un autre message privé !");
                return true;
            }

            String message = String.join(" ", args);
            sendPrivateMessage(player, target, message);

        } else if (label.equalsIgnoreCase("msghistory")) {
            showHistory(player);
        }

        return true;
    }

    private void sendPrivateMessage(Player sender, Player receiver, String message) {
        String formattedMessageSender = "§7[§eMP → " + receiver.getName() + "§7] §f" + message;
        String formattedMessageReceiver = "§7[§eMP de " + sender.getName() + "§7] §f" + message;

        sender.sendMessage(formattedMessageSender);
        receiver.sendMessage(formattedMessageReceiver);

        receiver.playSound(receiver.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        lastMessaged.put(sender.getUniqueId(), receiver.getUniqueId());
        lastMessaged.put(receiver.getUniqueId(), sender.getUniqueId());

        setCooldown(sender);
        saveMessageToHistory(sender, formattedMessageSender);
        saveMessageToHistory(receiver, formattedMessageReceiver);
    }

    private void saveMessageToHistory(Player player, String message) {
        FileConfiguration config = plugin.getMessagesConfig();
        String path = "messages." + player.getUniqueId();

        List<String> history = config.getStringList(path);
        if (history.size() >= MAX_HISTORY) {
            history.removeFirst();
        }
        history.add(message);

        config.set(path, history);
        plugin.saveMessagesConfig();
    }

    private void showHistory(Player player) {
        FileConfiguration config = plugin.getMessagesConfig();
        String path = "messages." + player.getUniqueId();
        List<String> history = config.getStringList(path);

        if (history.isEmpty()) {
            player.sendMessage("§7[§eMP§7] §cAucun historique de messages.");
            return;
        }

        player.sendMessage("§7[§eHistorique MP§7] §aDerniers messages privés :");
        for (String msg : history) {
            player.sendMessage("§7- " + msg);
        }
    }

    private boolean isOnCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (cooldowns.containsKey(playerUUID)) {
            long timeLeft = (cooldowns.get(playerUUID) + COOLDOWN_TIME) - System.currentTimeMillis();
            return timeLeft > 0;
        }
        return false;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}

