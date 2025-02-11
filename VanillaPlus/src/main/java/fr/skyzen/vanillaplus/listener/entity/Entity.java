package fr.skyzen.vanillaplus.listener.entity;

import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Entity implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            int currentFoodLevel = player.getFoodLevel();
            int newFoodLevel = event.getFoodLevel();

            if (Cooldown.hasCooldown(player, "afk")) {
                event.setCancelled(true);
            }

            if (newFoodLevel < currentFoodLevel) {
                int foodLoss = currentFoodLevel - newFoodLevel;
                int reducedFoodLoss = (int) Math.ceil(foodLoss * Messages.hunger_decrease_rate);
                int adjustedFoodLevel = currentFoodLevel - reducedFoodLoss;

                event.setFoodLevel(Math.max(adjustedFoodLevel, 0));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer != null)
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Joueur " + ChatColor.YELLOW + killer.getName() + ChatColor.GRAY + " a tué une entité " + ChatColor.YELLOW + entity.getName());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        String deathCause = this.getDeathCause(player);
        String deathMsg = this.getDeathMessage(player, deathCause);
        Bukkit.broadcastMessage(deathMsg);
    }

    private String getDeathCause(Player player) {
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause == null) {
            return "inconnue";
        } else {
            return switch (lastDamageCause.getCause()) {
                case CONTACT -> "écrasé";
                case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "explosé";
                case DROWNING -> "noyé";
                case FALL -> "chuté";
                case ENTITY_ATTACK, PROJECTILE -> {
                    if (player.getKiller() != null) {
                        yield "tué par " + player.getKiller().getName();
                    }

                    yield "attaqué par une entité";
                }
                default -> "inconnue";
            };
        }
    }

    private String getDeathMessage(Player player, String deathCause) {
        ChatColor deathColor = ChatColor.GREEN;
        String playerName = player.getDisplayName();

        if (deathCause.equals("tué par un autre joueur")) {
            Player killer = player.getKiller();
            ChatColor killerColor = ChatColor.RED;

            assert killer != null;

            String killerName = killer.getName();
            return deathColor + playerName + ChatColor.GRAY + " a été " + deathCause + " " + killerColor + killerName;
        } else {
            return deathColor + playerName + ChatColor.GRAY + " est mort (" + deathCause + ").";
        }
    }
}
