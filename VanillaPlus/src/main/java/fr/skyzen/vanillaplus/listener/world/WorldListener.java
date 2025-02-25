package fr.skyzen.vanillaplus.listener.world;

import fr.skyzen.vanillaplus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldListener implements Listener {

    private final Random random = new Random();

    // Liste des types de bois compatibles
    private final Set<Material> LOG_TYPES = Set.of(
            Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.CHERRY_LOG, Material.MANGROVE_LOG, Material.BAMBOO_BLOCK
    );

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        World world = event.getWorld();

        // Vérifier si l'orage commence
        if (event.toThunderState()) {
            Bukkit.broadcastMessage("§c⚡ Une tempête approche. Faites attention aux monstres.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!world.isThundering()) {
                        this.cancel(); // Stopper la tâche si l'orage se termine
                        return;
                    }

                    // Booster les monstres pendant l'orage
                    world.getEntities().forEach(entity -> {
                        if (entity instanceof Mob mob && random.nextInt(10) == 0) {
                            if (mob.getType() == EntityType.CREEPER) {
                                world.strikeLightningEffect(mob.getLocation()); // Effet visuel pour les creepers
                            }
                        }
                    });
                }
            }.runTaskTimer(VanillaPlus.getInstance(), 0L, 20L * 10); // Toutes les 10 secondes
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Vérifier si le bloc cassé est un tronc d'arbre
        if (!LOG_TYPES.contains(block.getType())) {
            return;
        }

        // Vérifier si le joueur utilise une hache (optionnel, retire cette condition si inutile)
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType() == Material.AIR || !tool.getType().name().endsWith("_AXE")) {
            return; // Nécessite une hache
        }

        // Récupérer tous les blocs de l'arbre
        Set<Block> treeBlocks = new HashSet<>();
        getConnectedLogs(block, treeBlocks);

        // Vérifier qu'il y a bien un arbre et éviter de casser trop de blocs par erreur
        if (treeBlocks.size() < 3) return;

        // Effet sonore réaliste
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);

        // Faire tomber l'arbre progressivement
        new BukkitRunnable() {
            @Override
            public void run() {
                if (treeBlocks.isEmpty()) {
                    this.cancel();
                    return;
                }
                Block log = treeBlocks.iterator().next();
                treeBlocks.remove(log);
                log.breakNaturally(); // Casse le bloc naturellement
            }
        }.runTaskTimer(VanillaPlus.getInstance(), 0L, 5L); // Casse un bloc toutes les 5 ticks (0.25s)
    }

    /**
     * Récupère tous les troncs connectés pour éviter de casser plusieurs arbres.
     */
    private void getConnectedLogs(Block block, Set<Block> logs) {
        if (logs.contains(block) || !LOG_TYPES.contains(block.getType())) {
            return; // Évite les boucles infinies
        }

        logs.add(block);

        // Vérifier les blocs autour (X, Y, Z)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) { // Seulement vers le haut et sur les côtés
                for (int dz = -1; dz <= 1; dz++) {
                    Block relative = block.getRelative(dx, dy, dz);
                    getConnectedLogs(relative, logs);
                }
            }
        }
    }
}