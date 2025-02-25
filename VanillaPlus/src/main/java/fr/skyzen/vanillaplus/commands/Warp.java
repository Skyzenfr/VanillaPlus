package fr.skyzen.vanillaplus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import fr.skyzen.vanillaplus.VanillaPlus;
import fr.skyzen.vanillaplus.utils.Cooldown;
import fr.skyzen.vanillaplus.utils.Messages;
import fr.skyzen.vanillaplus.utils.Teleport;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Warp implements CommandExecutor, Listener {
    static ConfigurationSection warpSection = VanillaPlus.configwarps.getConfig().getConfigurationSection("warp");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @Nullable String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }
        if (args.length == 0) {
            if (Cooldown.hasCooldown(player, "teleport")) {
                Cooldown.sendCooldownMessage(player, "teleport", ChatColor.RED + "Vous devez attendre encore {time} {unit} avant de vous téléporter.");
                return false;
            }
            openMenu(player);
            return true;
        }
        assert args[0] != null;
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 2) {
                Messages.helpMessage(player, "Warp", "Warp create", "nom", "Créer une zone de téléportation.");
                return false;
            }
            assert args[1] != null;
            String warpName = args[1].toLowerCase();
            if (VanillaPlus.configwarps.getConfig().isSet("warp." + warpName)) {
                sender.sendMessage(ChatColor.RED + "Ce warp est déjà éxistant.");
                return false;
            }
            Date date = new Date();
            Location PlayerLocation = player.getLocation();
            assert PlayerLocation.getWorld() != null;
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".x", PlayerLocation.getX());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".y", PlayerLocation.getY());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".z", PlayerLocation.getZ());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".pitch", PlayerLocation.getPitch());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".yaw", PlayerLocation.getYaw());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".world", PlayerLocation.getWorld().getName());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".pseudo", player.getDisplayName());
            VanillaPlus.configwarps.getConfig().set("warp." + warpName + ".date", Messages.formatDate.format(date));
            VanillaPlus.configwarps.save();
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + ChatColor.GREEN + " vient de créer le warp: " + ChatColor.AQUA + warpName);
            return true;
        }
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                Messages.helpMessage(player, "Warp", "Warp delete", "nom", "Supprimer une zone de téléportation.");
                return false;
            }
            assert args[1] != null;
            String warpName = args[1].toLowerCase();
            if (!VanillaPlus.configwarps.getConfig().isSet("warp." + warpName)) {
                TextComponent warpinfo = new TextComponent(ChatColor.RED + "/warp pour avoir la liste des warps disponibles");
                warpinfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Voir les warps disponibles en cliquant ici")));
                warpinfo.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp"));
                player.spigot().sendMessage(warpinfo);
                return true;
            }
            VanillaPlus.configwarps.getConfig().set("warp." + warpName, null);
            VanillaPlus.configwarps.save();
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + ChatColor.RED + " vient de supprimer le warp: " + ChatColor.AQUA + warpName);
            return true;
        }
        Messages.helpMessage(player, "Warp", "Warp (create/delete)", "nom", "Accès/Créer/Supprimer une zone de téléportation.");
        return true;
    }

    public static void openMenu(Player player) {
        if (!VanillaPlus.configwarps.getConfig().isSet("warp.")) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Aucuns warps n'a été trouvé. /setwarp pour définir un warp."));
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 54, "Warps");
        if (warpSection != null)
            for (String warp : warpSection.getKeys(false)) {
                inventory.addItem(getWarp(warp, player));
            }
        player.openInventory(inventory);
    }

    static List<String> motsMinage = Arrays.asList("mine", "grotte", "faille");
    static List<String> motsGlace = Arrays.asList("glace", "glacier", "neige");
    static List<String> motsNether = Arrays.asList("nether", "forteresse", "forteress", "lave");
    static List<String> motsForets = Arrays.asList("foret", "jungle", "bois", "buches");

    public static ItemStack getWarp(String warp, Player player) {
        Random random;
        int num;
        switch (detecterTypeWarp(warp.toLowerCase())) {
            case MINAGE:
            case GLACE:
            case NETHER:
            case FORETS:
            default:
                random = new Random();
                num = random.nextInt(3);
                switch (num) {
                    case 0:
                    case 1:
                    case 2:
                }
        }
        ItemStack item = new ItemStack(Material.BLUE_BED, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + warp);
            double x = VanillaPlus.configwarps.getConfig().getDouble("warp." + warp + ".x");
            double y = VanillaPlus.configwarps.getConfig().getDouble("warp." + warp + ".y");
            double z = VanillaPlus.configwarps.getConfig().getDouble("warp." + warp + ".z");
            String world = VanillaPlus.configwarps.getConfig().getString("warp." + warp + ".world");
            String pseudo = VanillaPlus.configwarps.getConfig().getString("warp." + warp + ".pseudo");
            String date = VanillaPlus.configwarps.getConfig().getString("warp." + warp + ".date");

            if (world != null) {
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY + " Monde: " + ChatColor.WHITE + world);
                lore.add("");
                lore.add(ChatColor.GRAY + " Position:");
                lore.add(ChatColor.DARK_GRAY + "        x: " + ChatColor.WHITE + x);
                lore.add(ChatColor.DARK_GRAY + "        y: " + ChatColor.WHITE + y);
                lore.add(ChatColor.DARK_GRAY + "        z: " + ChatColor.WHITE + z);
                lore.add("");
                lore.add(ChatColor.GRAY + " Créé par: " + ChatColor.AQUA + pseudo);
                lore.add(ChatColor.DARK_GRAY + "          Le " + ChatColor.AQUA + date);
                lore.add("");

                if (pseudo != null && pseudo.equals(player.getName())) {
                    lore.add(" " + ChatColor.DARK_GRAY + "⏵" + ChatColor.RED + " Shift + Clic : Supprimer ce warp");
                }

                lore.add(" " + ChatColor.DARK_GRAY + "⏵" + ChatColor.YELLOW + " Clic : Téléportation à ce warp ⇒");
                meta.setLore(lore);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private static TypeWarp detecterTypeWarp(String nomWarp) {
        if (contientMot(nomWarp, motsMinage))
            return TypeWarp.MINAGE;
        if (contientMot(nomWarp, motsGlace))
            return TypeWarp.GLACE;
        if (contientMot(nomWarp, motsNether))
            return TypeWarp.NETHER;
        if (contientMot(nomWarp, motsForets))
            return TypeWarp.FORETS;
        return TypeWarp.DEFAULT;
    }

    private static boolean contientMot(String nomWarp, List<String> mots) {
        for (String mot : mots) {
            if (nomWarp.contains(mot))
                return true;
        }
        return false;
    }

    enum TypeWarp {
        MINAGE, GLACE, NETHER, FORETS, DEFAULT
    }

    @EventHandler
    public void onWarpMenu(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Warps")) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }

        String warpName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (warpSection != null && warpSection.contains(warpName)) {
            if (event.isShiftClick()) {
                String creator = VanillaPlus.configwarps.getConfig().getString("warp." + warpName + ".pseudo");
                if (creator != null && creator.equals(player.getName())) {
                    VanillaPlus.configwarps.getConfig().set("warp." + warpName, null);
                    VanillaPlus.configwarps.save();
                    Bukkit.broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + ChatColor.RED + " vient de supprimer le warp: " + ChatColor.AQUA + warpName);
                }
            } else {
                String world = VanillaPlus.configwarps.getConfig().getString("warp." + warpName + ".world");
                if (world != null) {
                    double x = VanillaPlus.configwarps.getConfig().getDouble("warp." + warpName + ".x");
                    double y = VanillaPlus.configwarps.getConfig().getDouble("warp." + warpName + ".y");
                    double z = VanillaPlus.configwarps.getConfig().getDouble("warp." + warpName + ".z");
                    float pitch = (float) VanillaPlus.configwarps.getConfig().getDouble("warp." + warpName + ".pitch");
                    float yaw = (float) VanillaPlus.configwarps.getConfig().getDouble("warp." + warpName + ".yaw");
                    Teleport.teleportPlayer(player, new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
                }
            }
            player.closeInventory();
        }
    }

}