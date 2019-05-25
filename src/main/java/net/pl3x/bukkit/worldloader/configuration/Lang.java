package net.pl3x.bukkit.worldloader.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.worldloader.WorldLoader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Lang {
    public static String COMMAND_NO_PERMISSION = "&4You do not have permission for that command!";
    public static String PLAYER_COMMAND = "&4This command is only available to players!";

    public static String TELEPORTING = "&dTeleporting...";

    public static String SET_SPAWN_SUCCESS = "&dSpawn has been set for &7{world}";
    public static String SET_SPAWN_ERROR = "&cThere was a problem trying to set spawn";

    public static String SPAWN_SUCCESS = "&dTeleported to spawn";
    public static String SPAWN_ERROR = "&cThere was a problem trying to teleport to spawn";

    public static String WORLD_NOT_FOUND = "&4World not found";
    public static String WORLD_SUCCESS = "&dTeleported to &7{world}";
    public static String WORLD_ERROR = "&cThere was a problem trying to teleport to &7{world}";

    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", COMMAND_NO_PERMISSION);
        PLAYER_COMMAND = getString("player-command", PLAYER_COMMAND);

        TELEPORTING = getString("teleporting", TELEPORTING);

        SET_SPAWN_SUCCESS = getString("set-spawn-success", SET_SPAWN_SUCCESS);
        SET_SPAWN_ERROR = getString("set-spawn-error", SET_SPAWN_ERROR);

        SPAWN_SUCCESS = getString("spawn-success", SPAWN_SUCCESS);
        SPAWN_ERROR = getString("spawn-error", SPAWN_ERROR);

        WORLD_NOT_FOUND = getString("world-not-found", WORLD_NOT_FOUND);
        WORLD_SUCCESS = getString("world-success", WORLD_SUCCESS);
        WORLD_ERROR = getString("world-error", WORLD_ERROR);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the language file
     */
    public static void reload() {
        WorldLoader plugin = WorldLoader.getInstance();
        File configFile = new File(plugin.getDataFolder(), Config.LANGUAGE_FILE);
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + Config.LANGUAGE_FILE + ", please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the main language file for Chatter.");
        config.options().copyDefaults(true);

        Lang.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (recipient != null) {
            for (String part : colorize(message).split("\n")) {
                recipient.sendMessage(part);
            }
        }
    }

    /**
     * Broadcast a message to server
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        for (String part : colorize(message).split("\n")) {
            Bukkit.getOnlinePlayers().forEach(recipient -> recipient.sendMessage(part));
            Bukkit.getConsoleSender().sendMessage(part);
        }
    }

    /**
     * Colorize a String
     *
     * @param str String to colorize
     * @return Colorized String
     */
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }
}
