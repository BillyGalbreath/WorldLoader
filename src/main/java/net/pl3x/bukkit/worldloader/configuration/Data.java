package net.pl3x.bukkit.worldloader.configuration;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Data {
    public static final Map<UUID, Location> SPAWN = new HashMap<>();

    private static void init(Plugin plugin) {
        Map<UUID, Location> spawns = new HashMap<>();

        ConfigurationSection spawn = config.getConfigurationSection("spawn");
        if (spawn != null) {
            for (String worldName : spawn.getKeys(false)) {
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("Skipping spawn point for world: " + worldName);
                    continue;
                }
                Location loc = spawn.getLocation(worldName);
                if (loc != null) {
                    spawns.put(world.getUID(), loc);
                }
            }
        }

        SPAWN.clear();
        SPAWN.putAll(spawns);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload(Plugin plugin) {
        configFile = new File(plugin.getDataFolder(), "data.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not load data.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the data file for " + plugin.getName());
        config.options().copyDefaults(true);
        Data.init(plugin);
        Data.save(plugin);
    }

    private static void save(Plugin plugin) {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;
    private static File configFile;

    public static Location getSpawn(World world) {
        Location spawn = SPAWN.get(world.getUID());
        if (spawn == null) {
            spawn = world.getSpawnLocation();
        }
        return spawn;
    }

    public static void setSpawn(Plugin plugin, World world, Location spawn) {
        SPAWN.put(world.getUID(), spawn);
        config.set("spawn." + world.getName(), spawn);
        world.setSpawnLocation(spawn);
        save(plugin);
    }
}
