package fr.skyzen.vanillaplus.listener.world;

import fr.skyzen.vanillaplus.utils.Money;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class Economy implements Listener {

    private final Map<UUID, Double> pendingRewards = new HashMap<>();
    private final Map<UUID, Double> dailyEarnings = new HashMap<>();
    private final Set<UUID> warnedPlayers = new HashSet<>();

    private static final double MAX_DAILY_REWARD = 2000.0;

    private static final Map<Material, Double> MINING_REWARDS = new EnumMap<>(Material.class);
    private static final Map<Material, Double> FARMING_REWARDS = new EnumMap<>(Material.class);
    private static final Map<EntityType, Double> MOB_REWARDS = new EnumMap<>(EntityType.class);

    static {
        // 📌 Récompenses pour le minage
        MINING_REWARDS.put(Material.COAL_ORE, 2.0);
        MINING_REWARDS.put(Material.DEEPSLATE_COAL_ORE, 2.0);
        MINING_REWARDS.put(Material.IRON_ORE, 5.0);
        MINING_REWARDS.put(Material.DEEPSLATE_IRON_ORE, 5.0);
        MINING_REWARDS.put(Material.GOLD_ORE, 10.0);
        MINING_REWARDS.put(Material.DEEPSLATE_GOLD_ORE, 10.0);
        MINING_REWARDS.put(Material.DIAMOND_ORE, 50.0);
        MINING_REWARDS.put(Material.DEEPSLATE_DIAMOND_ORE, 50.0);
        MINING_REWARDS.put(Material.ANCIENT_DEBRIS, 50.0);
        MINING_REWARDS.put(Material.EMERALD_ORE, 30.0);
        MINING_REWARDS.put(Material.DEEPSLATE_EMERALD_ORE, 30.0);
        MINING_REWARDS.put(Material.REDSTONE_ORE, 3.0);
        MINING_REWARDS.put(Material.DEEPSLATE_REDSTONE_ORE, 3.0);
        MINING_REWARDS.put(Material.LAPIS_ORE, 3.0);
        MINING_REWARDS.put(Material.DEEPSLATE_LAPIS_ORE, 3.0);

        // 📌 Récompenses pour l'agriculture
        FARMING_REWARDS.put(Material.WHEAT, 3.0);
        FARMING_REWARDS.put(Material.CARROTS, 3.0);
        FARMING_REWARDS.put(Material.POTATOES, 3.0);
        FARMING_REWARDS.put(Material.MELON, 5.0);
        FARMING_REWARDS.put(Material.PUMPKIN, 5.0);

        // 📌 Récompenses pour le combat
        MOB_REWARDS.put(EntityType.COW, 5.0);
        MOB_REWARDS.put(EntityType.SHEEP, 5.0);
        MOB_REWARDS.put(EntityType.PIG, 5.0);
        MOB_REWARDS.put(EntityType.CHICKEN, 5.0);
        MOB_REWARDS.put(EntityType.ZOMBIE, 8.0);
        MOB_REWARDS.put(EntityType.SKELETON, 8.0);
        MOB_REWARDS.put(EntityType.SPIDER, 8.0);
        MOB_REWARDS.put(EntityType.CREEPER, 15.0);
        MOB_REWARDS.put(EntityType.ENDERMAN, 25.0);
        MOB_REWARDS.put(EntityType.BLAZE, 25.0);
        MOB_REWARDS.put(EntityType.WITHER_SKELETON, 25.0);
        MOB_REWARDS.put(EntityType.WARDEN, 100.0);
        MOB_REWARDS.put(EntityType.WITHER, 100.0);
        MOB_REWARDS.put(EntityType.ENDER_DRAGON, 100.0);
    }

    /* 🔹 Récompenses minage + agriculture */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        double amount = MINING_REWARDS.getOrDefault(block, 0.0);

        if (amount == 0 && FARMING_REWARDS.containsKey(block)) {
            if (event.getBlock().getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    amount = FARMING_REWARDS.get(block);
                }
            }
        }

        if (amount > 0) {
            addReward(player, amount);
        }
    }

    /* 🔹 Récompenses pêche */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() instanceof Fish && event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            addReward(event.getPlayer(), 10);
        }
    }

    /* 🔹 Récompenses combat */
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            double amount = MOB_REWARDS.getOrDefault(event.getEntity().getType(), 0.0);
            if (amount > 0) {
                addReward(player, amount);
            }
        }
    }

    /* 🔹 Récompenses élevage */
    @EventHandler
    public void onAnimalBreed(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Animals) {
            addReward(event.getPlayer(), 20);
        }
    }

    /* 🔹 Gestion des récompenses avec limite journalière */
    private void addReward(Player player, double amount) {
        UUID uuid = player.getUniqueId();
        double currentEarnings = dailyEarnings.getOrDefault(uuid, 0.0);

        if (currentEarnings + amount > MAX_DAILY_REWARD) {
            if (!warnedPlayers.contains(uuid)) {
                player.sendMessage(ChatColor.RED + "⚠️ Vous avez atteint la limite quotidienne de " + MAX_DAILY_REWARD + "€ !");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                warnedPlayers.add(uuid);
                Bukkit.getLogger().log(Level.WARNING, "[Economy] {0} a atteint la limite journalière de {1}€.", new Object[]{player.getName(), MAX_DAILY_REWARD});
            }
            return;
        }

        pendingRewards.put(uuid, pendingRewards.getOrDefault(uuid, 0.0) + amount);
        dailyEarnings.put(uuid, currentEarnings + amount);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingRewards.containsKey(uuid)) {
                    double totalReward = pendingRewards.remove(uuid);
                    Money.addMoney(uuid, totalReward);
                    sendRewardMessage(player, totalReward);
                    Bukkit.getLogger().log(Level.INFO, "[Economy] {0} a reçu {1}€ en récompense.", new Object[]{player.getName(), totalReward});
                }
            }
        }.runTaskLaterAsynchronously(fr.skyzen.vanillaplus.VanillaPlus.getInstance(), 20L);
    }

    /* 🔹 Envoi du message unique */
    private void sendRewardMessage(Player player, double amount) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GREEN + "+ " + amount + "€").create());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
    }
}