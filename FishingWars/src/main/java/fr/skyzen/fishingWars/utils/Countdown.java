package fr.skyzen.fishingWars.utils;

import fr.skyzen.fishingWars.FishingWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown {
    private int seconds;
    private final Runnable onEnd;

    public Countdown(int seconds, Runnable onEnd) {
        this.seconds = seconds;
        this.onEnd = onEnd;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (seconds <= 0) {
                    onEnd.run();
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(ChatColor.YELLOW + "Début dans " + ChatColor.RED + seconds, "", 10, 20, 10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                }

                Bukkit.broadcastMessage(ChatColor.YELLOW + "Début du jeu dans " + ChatColor.RED + seconds + " secondes...");
                seconds--;
            }
        }.runTaskTimer(FishingWars.getInstance(), 0, 20);
    }
}
