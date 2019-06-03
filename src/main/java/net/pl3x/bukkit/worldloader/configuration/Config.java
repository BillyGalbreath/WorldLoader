package net.pl3x.bukkit.worldloader.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.worldloader.WorldSettings;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static boolean SPAWN_USES_CURRENT_WORLD = false;

    public static boolean PER_WORLD_PERMISSIONS = false;

    public static int RTP_COOLDOWN = 300; // 5 minutes

    public static final Map<String, WorldSettings> WORLD_SETTINGS = new HashMap<>();

    private static void init() {
        LANGUAGE_FILE = getString("language-file", LANGUAGE_FILE);

        SPAWN_USES_CURRENT_WORLD = getBoolean("spawn-uses-current-world", SPAWN_USES_CURRENT_WORLD);

        PER_WORLD_PERMISSIONS = getBoolean("per-world-permissions", PER_WORLD_PERMISSIONS);

        RTP_COOLDOWN = getInt("rtp-cooldown", RTP_COOLDOWN);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload(Plugin plugin) {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the configuration file for " + plugin.getName());
        config.options().copyDefaults(true);

        Config.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }

        WORLD_SETTINGS.clear();
        ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
        if (worlds != null) {
            for (String name : worlds.getKeys(false)) {
                ConfigurationSection section = worlds.getConfigurationSection(name);
                if (section != null) {


                    try {
                        WORLD_SETTINGS.put(name, new WorldSettings(name, section));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Could not load level \"" + name + "\"");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    public static WorldSettings getSettings(World world) {
        return getSettings(world.getName());
    }

    public static WorldSettings getSettings(String worldName) {
        return WORLD_SETTINGS.get(worldName);
    }
}
