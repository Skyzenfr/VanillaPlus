package fr.skyzen.vanillaplus.listener.players;

import fr.skyzen.vanillaplus.utils.Items;
import fr.skyzen.vanillaplus.utils.Players;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerAnother implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setFormat(Players.getPlayerName(player) + ChatColor.DARK_GRAY + " ➲ " + ChatColor.GRAY + event.getMessage());
    }

    @EventHandler
    public void onPlayerBedClick(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        String message = ChatColor.GOLD + player.getName() + " a envie de dormir, allez-y...";
        Location location = player.getLocation();

        Bukkit.getOnlinePlayers().forEach(people -> {
            people.playSound(location, Sound.ENTITY_FOX_SLEEP, 1.0F, 1.0F);
            people.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        });
    }

    @EventHandler
    public void heldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

            if (newItem != null && newItem.getType() != Material.AIR) {
                Items.addGivenByLore(newItem, player.getName());
            }
        }
    }
}