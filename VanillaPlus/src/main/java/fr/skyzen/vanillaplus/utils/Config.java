package fr.skyzen.vanillaplus.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
    private final File file;
    private FileConfiguration config;
    private final Logger logger;

    public Config(Plugin plugin, String path) {
        this(plugin.getDataFolder(), path, plugin.getLogger());
    }

    public Config(File dataFolder, String path, Logger logger) {
        this.file = new File(dataFolder, path);
        this.logger = logger;
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            File directory = file.getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                logger.warning("Impossible de créer le répertoire : " + directory.getPath());
            }
            try {
                if (!file.createNewFile()) {
                    logger.warning("Impossible de créer le fichier de configuration : " + file.getPath());
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Erreur lors de la création du fichier de configuration", e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la sauvegarde du fichier de configuration", e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}