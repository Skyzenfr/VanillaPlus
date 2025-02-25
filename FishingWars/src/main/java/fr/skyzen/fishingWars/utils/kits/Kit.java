package fr.skyzen.fishingWars.utils.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class Kit {
    private final KitType type;
    private final List<ItemStack> items;

    public Kit(KitType type, List<ItemStack> items) {
        this.type = type;
        this.items = items;
    }

    public KitType getType() {
        return type;
    }

    public void giveTo(Player player) {
        player.getInventory().clear();
        for (ItemStack item : items) {
            player.getInventory().addItem(item);
        }
    }
}
