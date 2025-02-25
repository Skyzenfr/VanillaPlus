package fr.skyzen.vanillaplus.listener.entity;

import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Players;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

        if (killer != null) {
            // 🔹 Récupérer la traduction Minecraft (compatibles langues du client)
            Component entityNameComponent = Component.translatable(entity.getType().getTranslationKey());

            // 🔹 Convertir en texte compatible console
            String entityName = LegacyComponentSerializer.legacySection().serialize(entityNameComponent);

            String message = Messages.info +
                    Players.getPlayerName(killer) +
                    ChatColor.GRAY + " a tué un(e) " +
                    ChatColor.YELLOW + entityName;

            // Envoyer à la console
            Bukkit.getServer().getConsoleSender().sendMessage(message);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        String deathCause = getDeathCause(player);
        String deathMsg = getDeathMessage(player, deathCause);
        Bukkit.broadcastMessage(deathMsg);
    }

    private String getDeathCause(Player player) {
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause == null) {
            return "inconnue";
        }

        return switch (lastDamageCause.getCause()) {
            case CONTACT -> "écrasé";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "explosé";
            case DROWNING -> "noyé";
            case FALL -> "chuté";
            case FIRE, FIRE_TICK -> "brûlé";
            case LAVA -> "tombé dans la lave";
            case VOID -> "tombé dans le vide";
            case LIGHTNING -> "foudroyé";
            case SUFFOCATION -> "étouffé";
            case STARVATION -> "mort de faim";
            case POISON -> "empoisonné";
            case WITHER -> "décimé par le Wither";
            case PROJECTILE, ENTITY_ATTACK -> getKillerName(player);
            default -> "inconnue";
        };
    }

    private String getKillerName(Player player) {
        Player killer = player.getKiller();
        if (killer != null) {
            return "tué par " + killer.getName();
        }

        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause == null) {
            return "attaqué par une entité";
        }

        // 🔹 Récupérer l'entité qui a infligé les dégâts
        Entity damager = (Entity) lastDamageCause.getEntity();
        if (damager instanceof LivingEntity livingEntity) {
            // 🔹 Utilisation de Paper pour récupérer le nom traduit
            Component translatedName = Component.translatable(livingEntity.getType().getTranslationKey());
            String entityName = LegacyComponentSerializer.legacySection().serialize(translatedName);

            return "tué par un " + entityName;
        }

        return "attaqué par une entité";
    }

    private String getDeathMessage(Player player, String deathCause) {
        ChatColor deathColor = ChatColor.RED;
        String playerName = ChatColor.YELLOW + player.getDisplayName();

        return deathColor + playerName + ChatColor.GRAY + " est mort (" + deathCause + ").";
    }
}
