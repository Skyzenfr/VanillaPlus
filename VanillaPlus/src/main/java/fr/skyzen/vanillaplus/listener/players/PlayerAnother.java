package fr.skyzen.vanillaplus.listener.players;

import fr.skyzen.vanillaplus.VanillaPlus;
import fr.skyzen.vanillaplus.utils.Items;
import fr.skyzen.vanillaplus.utils.Money;
import fr.skyzen.vanillaplus.utils.Players;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerAnother implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();
        String playerName = Players.getPlayerName(player);

        // Création du pseudo cliquable UNIQUEMENT
        Component playerComponent = Component.text(playerName).color(NamedTextColor.WHITE)
                .hoverEvent(HoverEvent.showText(
                        Component.text(Players.getPlayerName(player))
                                .appendNewline().appendNewline()
                                .append(Component.text(" Argent: ").color(NamedTextColor.DARK_GRAY).append(Component.text(Money.getMoney(player.getUniqueId()) + "€").color(NamedTextColor.AQUA)))
                                .appendNewline()
                                .append(Component.text(" Niveau: ").color(NamedTextColor.DARK_GRAY).append(Component.text(Players.getPlayerLevel(player.getUniqueId())).color(NamedTextColor.YELLOW)))
                                .appendNewline().appendNewline()
                                .append(Component.text("Envoyer un message privé à " + playerName).color(NamedTextColor.GRAY))
                ))
                .clickEvent(ClickEvent.suggestCommand("/msg " + player.getName() + " "));

        // Séparateur (non cliquable)
        Component separator = Component.text(" " + ChatColor.DARK_GRAY + "➲ ");

        // Message du joueur (non cliquable)
        Component chatMessage = Component.text(message).color(NamedTextColor.GRAY);

        // Envoi du message sous forme de plusieurs composants distincts
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            VanillaPlus.adventure().player(onlinePlayer).sendMessage(Component.empty()
                    .append(playerComponent) // Seul le pseudo est cliquable
                    .append(separator) // Flèche ➲
                    .append(chatMessage) // Message normal
            );
        }
        Bukkit.getServer().getConsoleSender().sendMessage(Players.getPlayerName(player) + separator + chatMessage);
    }

    @EventHandler
    public void onPlayerBedClick(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        String message = Players.getPlayerName(player) + " a envie de dormir.";
        Location location = player.getLocation();

        Bukkit.getOnlinePlayers().forEach(people -> {
            people.playSound(location, Sound.ENTITY_FOX_SLEEP, 1.0F, 1.0F);
            people.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        });
    }

    @EventHandler
    public void onPlayerLevelUp(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        Players.savePlayerLevel(player);
    }

    @EventHandler
    public void heldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

            if (newItem != null && newItem.getType() != Material.AIR) {
                Items.addGivenByLore(newItem, Players.getPlayerName(player));
            }
        }
    }
}