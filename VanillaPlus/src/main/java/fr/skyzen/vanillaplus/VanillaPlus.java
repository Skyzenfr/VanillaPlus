package fr.skyzen.vanillaplus;

import fr.skyzen.vanillaplus.commands.*;
import fr.skyzen.vanillaplus.commands.Money;
import fr.skyzen.vanillaplus.commands.tabcompleter.tabCompleteMarket;
import fr.skyzen.vanillaplus.commands.tabcompleter.tabCompleteMoney;
import fr.skyzen.vanillaplus.commands.tabcompleter.tabCompletePay;
import fr.skyzen.vanillaplus.commands.tabcompleter.tabCompleteWarp;
import fr.skyzen.vanillaplus.manager.Listener;
import fr.skyzen.vanillaplus.manager.MarketManager;
import fr.skyzen.vanillaplus.utils.*;
import fr.skyzen.vanillaplus.utils.gui.TeleportationGUI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class VanillaPlus extends JavaPlugin implements org.bukkit.event.Listener {
    public static VanillaPlus plugin;
    public static Config config;
    public static Config configwarps;
    private boolean startupSuccess = false;
    private File messagesFile;
    private FileConfiguration messagesConfig;
    private static BukkitAudiences adventure;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        try {
            // Vérification et création du dossier du plugin si nécessaire
            if (!getDataFolder().exists()) {
                if (!getDataFolder().mkdirs()) {
                    throw new RuntimeException("Impossible de créer le dossier du plugin");
                }
            }

            // Vérification et chargement de la configuration
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                saveDefaultConfig();
            }

            adventure = BukkitAudiences.create(this);
            config = new Config(plugin, "config.yml");
            configwarps = new Config(plugin, "warps.yml");
            TeleportationGUI.initialize();
            new Listener(plugin).registerListeners();
            PersistentData.init(plugin);
            MarketManager.init(plugin);
            Messages.startTablistUpdater();

            setCommandExecutor("craft", new Craft());
            setCommandExecutor("help", new Help(plugin));
            setCommandExecutor("spawn", new Spawn());
            setCommandExecutor("stats", new Stats());
            setCommandExecutor("tp", new Teleportation());
            setCommandExecutor("warp", new Warp());
            setCommandExecutor("money", new Money());
            setCommandExecutor("pay", new Pay());
            setCommandExecutor("market", new Market());
            setCommandExecutor("msg", new PrivateMessage(this));
            setCommandExecutor("reply", new PrivateMessage(this));
            setCommandExecutor("msghistory", new PrivateMessage(this));
            setCommandExecutor("nickname", new NickName());
            setTabCompleter("warp", new tabCompleteWarp());
            setTabCompleter("money", new tabCompleteMoney());
            setTabCompleter("pay", new tabCompletePay());
            setTabCompleter("market", new tabCompleteMarket());

            loadMessagesFile();

            startupSuccess = true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Une erreur est survenue lors du démarrage du plugin", e);
            startupSuccess = false;
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        // Messages de démarrage
        Bukkit.getConsoleSender().sendMessage(Messages.pluginName + ChatColor.DARK_GRAY + " │ " + ChatColor.WHITE + "Démarrage du plugin...");
        Bukkit.getConsoleSender().sendMessage(Messages.pluginName + ChatColor.DARK_GRAY + " │ " + ChatColor.WHITE + "Version: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(Messages.pluginName + ChatColor.DARK_GRAY + " │ " + ChatColor.WHITE + "Plugin par " + String.join(", ", getDescription().getAuthors()));

        if (hasStartedSuccessfully()) {
            Bukkit.getConsoleSender().sendMessage(Messages.pluginName + ChatColor.DARK_GRAY + " │ " + ChatColor.WHITE + ChatColor.GREEN + "Le plugin a bien démarré sans erreur.");
        } else {
            Bukkit.getConsoleSender().sendMessage(Messages.pluginName + ChatColor.DARK_GRAY + " │ " + ChatColor.WHITE + ChatColor.RED + "Le plugin n'a pas pu démarrer correctement.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Player allplayers : Bukkit.getOnlinePlayers()){
            Cooldown.removeAllCooldowns(allplayers);
        }

        MarketManager.saveListings();

        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static VanillaPlus getInstance() {
        return plugin;
    }

    public void setCommandExecutor(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null)
            command.setExecutor(executor);
    }

    public void setTabCompleter(String commandName, TabCompleter completer) {
        PluginCommand command = getCommand(commandName);
        if (command != null)
            command.setTabCompleter(completer);
    }

    public boolean hasStartedSuccessfully() {
        return startupSuccess;
    }

    private void loadMessagesFile() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde de messages.yml", e);
        }
    }

    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
