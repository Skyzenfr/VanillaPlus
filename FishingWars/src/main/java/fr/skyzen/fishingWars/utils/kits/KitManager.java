package fr.skyzen.fishingWars.utils.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class KitManager {
    private final Map<KitType, Kit> kits = new HashMap<>();
    private final Map<Player, KitType> playerKits = new HashMap<>();

    public KitManager() {
        kits.put(KitType.GUERRIER, new Kit(KitType.GUERRIER, Arrays.asList(
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.IRON_CHESTPLATE)
        )));

        kits.put(KitType.ARCHER, new Kit(KitType.ARCHER, Arrays.asList(
                new ItemStack(Material.BOW),
                new ItemStack(Material.ARROW, 32),
                new ItemStack(Material.LEATHER_CHESTPLATE)
        )));

        kits.put(KitType.PYROMANE, new Kit(KitType.PYROMANE, Arrays.asList(
                new ItemStack(Material.FLINT_AND_STEEL),
                new ItemStack(Material.FIRE_CHARGE, 5)
        )));

        kits.put(KitType.PECHEUR, new Kit(KitType.PECHEUR, Arrays.asList(
                new ItemStack(Material.FISHING_ROD),
                new ItemStack(Material.COOKED_COD, 5)
        )));

        kits.put(KitType.TANK, new Kit(KitType.TANK, Arrays.asList(
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.SHIELD)
        )));
    }

    public void assignKit(Player player, KitType kitType) {
        playerKits.put(player, kitType);
    }

    public KitType getPlayerKit(Player player) {
        return playerKits.getOrDefault(player, KitType.GUERRIER); // Guerrier par défaut
    }

    public void giveKit(Player player) {
        KitType kitType = getPlayerKit(player);
        kits.get(kitType).giveTo(player);
    }
}
