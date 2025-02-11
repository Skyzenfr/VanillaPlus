package fr.skyzen.vanillaplus.utils;


import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Items {

    public static void giveItemDropNaturally(Player player, Material mat, String name, List<String> lore, int nb) {
        ItemStack item = new ItemStack(mat, nb);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        if (player.getLocation().getWorld() == null)
            return;
        player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
    }

    public static ItemStack getPlayerHead(Player player, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createCustomItem(Material mat, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createCustomItemWithEnchant(Material mat, int amount, String name, List<String> lore, Enchantment enchantment, int enchantementlevel) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addEnchant(enchantment, enchantementlevel, true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getCompassWarp() {
        ItemStack machinevoyage = new ItemStack(Material.COMPASS);
        ItemMeta metacompass = machinevoyage.getItemMeta();
        assert metacompass != null;
        metacompass.setDisplayName("§d§nMachine à voyager");
        machinevoyage.setItemMeta(metacompass);
        return machinevoyage;
    }

    public static int getPlayerItemAmount(Player player, Material material) {
        int itemAmount = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == material) {
                itemAmount += itemStack.getAmount();
            }
        }
        return itemAmount;
    }

    public static void removePlayerItems(Player player, Material material, int amount) {
        int remainingAmount = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack != null && itemStack.getType() == material) {
                int stackAmount = itemStack.getAmount();
                if (stackAmount > remainingAmount) {
                    itemStack.setAmount(stackAmount - remainingAmount);
                    break;
                }
                remainingAmount -= stackAmount;
                player.getInventory().setItem(i, null);
            }
        }
    }

    public static void addGivenByLore(ItemStack item, String giverName) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            String loreLine = ChatColor.RED + "A été généré en créatif par " + giverName;
            itemMeta.setLore(Collections.singletonList(loreLine));
            item.setItemMeta(itemMeta);
        }
    }
}